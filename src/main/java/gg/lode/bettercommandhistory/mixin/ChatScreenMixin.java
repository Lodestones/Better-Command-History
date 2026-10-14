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
 * <p>The search only takes the arrows when the cursor is at the end of the text and there is
 * nothing to pick from: no suggestion list, or a list of one. With several suggestions open the
 * arrows browse them as in vanilla, and Shift with an arrow browses history instead. Editing
 * mid-line keeps vanilla's keys too.
 */
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Shadow
    protected EditBox input;

    @Shadow
    private CommandSuggestions commandSuggestions;

    @Unique
    private final HistorySearch bettercommandhistory$search = new HistorySearch();

    /** Set while a key is left to vanilla, whose own history then runs as it normally would. */
    @Unique
    private boolean bettercommandhistory$vanilla;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void bettercommandhistory$browse(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        boolean up = event.isUp();
        if (!up && !event.isDown()) {
            return;
        }

        bettercommandhistory$vanilla = !bettercommandhistory$owns(event);
        if (bettercommandhistory$vanilla) {
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

    /** Whether this arrow press belongs to the search rather than the suggestion list or the cursor. */
    @Unique
    private boolean bettercommandhistory$owns(KeyEvent event) {
        if (input.getCursorPosition() != input.getValue().length()) {
            return false;
        }
        CommandSuggestions.SuggestionsList list = ((CommandSuggestionsAccessor) commandSuggestions).bettercommandhistory$getSuggestions();
        return list == null
                || event.hasShiftDown()
                || ((SuggestionsListAccessor) list).bettercommandhistory$getSuggestionList().size() <= 1;
    }

    /** Vanilla's unfiltered history would undo the search, so it only runs for keys left to vanilla. */
    @Inject(method = "moveInHistory", at = @At("HEAD"), cancellable = true)
    private void bettercommandhistory$skipVanillaHistory(int dir, CallbackInfo ci) {
        if (!bettercommandhistory$vanilla) {
            ci.cancel();
        }
    }
}
