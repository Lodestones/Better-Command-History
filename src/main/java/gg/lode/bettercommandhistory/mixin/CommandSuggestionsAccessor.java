package gg.lode.bettercommandhistory.mixin;

import net.minecraft.client.gui.components.CommandSuggestions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The suggestion list on screen, or null when none is showing. */
@Mixin(CommandSuggestions.class)
public interface CommandSuggestionsAccessor {
    @Accessor("suggestions")
    CommandSuggestions.SuggestionsList bettercommandhistory$getSuggestions();
}
