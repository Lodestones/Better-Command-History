package gg.lode.bettercommandhistory.mixin;

import gg.lode.bettercommandhistory.HistorySearch;
import gg.lode.bettercommandhistory.RecentChat;
import java.util.List;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Replaces chat's Up/Down history with a prefix search over the same history.
 *
 * <p>Runs ahead of the command suggestion list, so with a match the arrows browse history
 * even while suggestions are showing; Tab still cycles them. With no match the key falls
 * through to vanilla, where the suggestion list can still use it.
 */
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Shadow
    protected EditBox input;

    @Shadow
    private CommandSuggestions commandSuggestions;

    @Unique
    private final HistorySearch bettercommandhistory$search = new HistorySearch();

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void bettercommandhistory$browse(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        boolean up = event.isUp();
        if (!up && !event.isDown()) {
            return;
        }

        List<String> history = RecentChat.get();
        String value = input.getValue();
        String found = up
                ? bettercommandhistory$search.older(history, value)
                : bettercommandhistory$search.newer(history, value);
        if (found != null) {
            input.setValue(found);
            // Set after the value, whose edit turns suggestions back on, as vanilla's history does.
            commandSuggestions.setAllowSuggestions(false);
            cir.setReturnValue(true);
        } else if (bettercommandhistory$search.isBrowsing(value)) {
            // Already at the oldest match: stay on it rather than jumping to unrelated history.
            cir.setReturnValue(true);
        }
    }

    /** Vanilla's unfiltered history would undo the search, so it never runs. */
    @Inject(method = "moveInHistory", at = @At("HEAD"), cancellable = true)
    private void bettercommandhistory$skipVanillaHistory(int dir, CallbackInfo ci) {
        ci.cancel();
    }
}
