package gg.lode.bettercommandhistory.mixin;

import com.mojang.brigadier.suggestion.Suggestion;
import java.util.List;
import net.minecraft.client.gui.components.CommandSuggestions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The suggestions in an open list. */
@Mixin(CommandSuggestions.SuggestionsList.class)
public interface SuggestionsListAccessor {
    @Accessor("suggestionList")
    List<Suggestion> bettercommandhistory$getSuggestionList();
}
