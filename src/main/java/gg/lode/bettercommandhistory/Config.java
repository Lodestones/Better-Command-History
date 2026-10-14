package gg.lode.bettercommandhistory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Player settings, kept in {@code config/bettercommandhistory.json}. */
public final class Config {
    public static final Logger LOGGER = LoggerFactory.getLogger("bettercommandhistory");
    /** Expiry that never forgets a command. */
    public static final int NEVER = 0;
    public static final int MIN_SIZE = 10;
    /** Chat's own history holds 100 entries, so a larger saved list could never be browsed. */
    public static final int MAX_SIZE = 100;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("bettercommandhistory.json");
    private static Config instance;

    /** Whether commands are written to disk at all. Off keeps them for this session only. */
    public boolean saveHistory = true;
    /** Days a command stays saved after it was last used, or {@link #NEVER}. */
    public int expiryDays = 7;
    /** How many commands are kept. */
    public int historySize = 50;

    public static Config get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static Config load() {
        if (Files.exists(FILE)) {
            try {
                Config config = GSON.fromJson(Files.readString(FILE, StandardCharsets.UTF_8), Config.class);
                if (config != null) {
                    config.clamp();
                    return config;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to read {}, using defaults", FILE, e);
            }
        }
        return new Config();
    }

    public void save() {
        clamp();
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Failed to write {}", FILE, e);
        }
    }

    private void clamp() {
        expiryDays = Math.max(NEVER, expiryDays);
        historySize = Math.max(MIN_SIZE, Math.min(MAX_SIZE, historySize));
    }
}
