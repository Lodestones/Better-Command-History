package gg.lode.bettercommandhistory;

import java.util.List;

/**
 * Prefix search through chat history: only entries starting with the typed text are visited.
 *
 * <p>The text in the box when browsing starts is the prefix. Older and newer step only through
 * entries that begin with it, ignoring case. Each distinct entry appears once, at its most recent
 * position, so a command run many times costs one press. Stepping newer past the latest match
 * puts the typed prefix back and ends the search. Editing the box also ends it: the next step
 * starts over with the new text.
 */
public final class HistorySearch {
    private String prefix;
    private String shown;
    private int index;

    /** The next older entry starting with the prefix, or null when there is none. */
    public String older(List<String> history, String input) {
        if (!isBrowsing(input)) {
            prefix = input;
            shown = input;
            index = history.size();
        }
        index = Math.min(index, history.size());
        for (int i = index - 1; i >= 0; i--) {
            String entry = history.get(i);
            if (matches(history, i)) {
                index = i;
                shown = entry;
                return entry;
            }
        }
        return null;
    }

    /**
     * The next newer entry starting with the prefix, the prefix itself once past the newest, or
     * null when not browsing, since nothing is newer than what was typed.
     */
    public String newer(List<String> history, String input) {
        if (!isBrowsing(input)) {
            return null;
        }
        for (int i = index + 1; i < history.size(); i++) {
            String entry = history.get(i);
            if (matches(history, i)) {
                index = i;
                shown = entry;
                return entry;
            }
        }
        String typed = prefix;
        prefix = null;
        return typed;
    }

    /** True while the box still holds what the search last put there. */
    public boolean isBrowsing(String input) {
        return prefix != null && input.equals(shown);
    }

    /**
     * Whether entry {@code i} starts with the prefix, is the latest copy of itself, and differs
     * from what the box holds, so every step visibly changes it.
     */
    private boolean matches(List<String> history, int i) {
        String entry = history.get(i);
        return !entry.equals(shown)
                && entry.regionMatches(true, 0, prefix, 0, prefix.length())
                && history.lastIndexOf(entry) == i;
    }
}
