package gg.lode.bettercommandhistory.mixin;

import gg.lode.bettercommandhistory.SavedCommands;
import java.nio.file.Path;
import net.minecraft.client.CommandHistory;
import net.minecraft.util.ArrayListDeque;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Hands vanilla's saved command history to {@link SavedCommands} for expiry, size and the save switch. */
@Mixin(CommandHistory.class)
public abstract class CommandHistoryMixin {
    @Shadow
    @Final
    private Path commandsPath;

    @Shadow
    @Final
    private ArrayListDeque<String> lastCommands;

    @Shadow
    private void save() {
        throw new AssertionError();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void bettercommandhistory$attach(Path gameFolder, CallbackInfo ci) {
        SavedCommands.attach(lastCommands, commandsPath, this::save);
    }

    @Inject(method = "addCommand", at = @At("HEAD"), cancellable = true)
    private void bettercommandhistory$add(String command, CallbackInfo ci) {
        SavedCommands.add(command);
        ci.cancel();
    }

    @Inject(method = "save", at = @At("HEAD"), cancellable = true)
    private void bettercommandhistory$onlyWhenSaving(CallbackInfo ci) {
        if (!SavedCommands.mayWrite()) {
            ci.cancel();
        }
    }
}
