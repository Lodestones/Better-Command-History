package gg.lode.bettercommandhistory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.ArrayListDeque;

/**
 * Expiry, size and the save switch for vanilla's saved command history.
 *
 * <p>Vanilla already writes the last 50 commands to {@code command_history.txt}, forever. That
 * file stays the store, so removing the mod keeps the history. Beside it, {@code
 * bettercommandhistory/last_used.json} records when each command was last run, which is what
 * expiry is measured from. A command found without a time, such as one saved before the mod was
 * installed, starts its clock when first seen.
 */
public final class SavedCommands {
    private static final Gson GSON = new Gson();
    private static final Type TIMES_TYPE = new TypeToken<Map<String, Long>>() {}.getType();

    private static ArrayListDeque<String> commands;
    private static Path commandsFile;
    private static Path timesFile;
    private static Runnable writeCommands;
    private static final Map<String, Long> lastUsed = new HashMap<>();

    /** Takes over vanilla's history once it has loaded {@code command_history.txt}. */
    public static void attach(ArrayListDeque<String> history, Path commandsPath, Runnable save) {
        commands = history;
        commandsFile = commandsPath;
        timesFile = commandsPath.resolveSibling("bettercommandhistory").resolve("last_used.json");
        writeCommands = save;
        lastUsed.clear();
        if (Files.exists(timesFile)) {
            try {
                Map<String, Long> saved = GSON.fromJson(Files.readString(timesFile, StandardCharsets.UTF_8), TIMES_TYPE);
                if (saved != null) {
                    lastUsed.putAll(saved);
                }
            } catch (Exception e) {
                Config.LOGGER.error("Failed to read {}, command ages restart now", timesFile, e);
            }
        }
        apply();
    }

    /** Records a command just run. Replaces vanilla's add, which knows nothing of expiry or size. */
    public static void add(String command) {
        if (commands == null) {
            return;
        }
        if (!command.equals(commands.peekLast())) {
            commands.addLast(command);
        }
        lastUsed.put(command, System.currentTimeMillis());
        apply();
    }

    /** Applies the current settings: drops expired and surplus commands, then saves or deletes. */
    public static void apply() {
        if (commands == null) {
            return;
        }
        Config config = Config.get();
        long now = System.currentTimeMillis();
        long cutoff = config.expiryDays == Config.NEVER ? Long.MIN_VALUE : now - TimeUnit.DAYS.toMillis(config.expiryDays);
        for (Iterator<String> it = commands.iterator(); it.hasNext(); ) {
            String command = it.next();
            if (lastUsed.computeIfAbsent(command, unseen -> now) < cutoff) {
                it.remove();
            }
        }
        while (commands.size() > config.historySize) {
            commands.removeFirst();
        }
        lastUsed.keySet().retainAll(commands);

        if (config.saveHistory) {
            writeCommands.run();
            writeTimes();
        } else {
            deleteFiles();
        }
    }

    /** Forgets every command, in memory and on disk. */
    public static void clear() {
        if (commands != null) {
            commands.clear();
        }
        lastUsed.clear();
        deleteFiles();
    }

    /** Whether vanilla may write {@code command_history.txt} right now. */
    public static boolean mayWrite() {
        return Config.get().saveHistory;
    }

    private static void writeTimes() {
        try {
            Files.createDirectories(timesFile.getParent());
            Files.writeString(timesFile, GSON.toJson(lastUsed, TIMES_TYPE), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Config.LOGGER.error("Failed to write {}", timesFile, e);
        }
    }

    private static void deleteFiles() {
        try {
            if (commandsFile != null) {
                Files.deleteIfExists(commandsFile);
            }
            if (timesFile != null) {
                Files.deleteIfExists(timesFile);
            }
        } catch (IOException e) {
            Config.LOGGER.error("Failed to delete saved command history", e);
        }
    }

    private SavedCommands() {}
}
