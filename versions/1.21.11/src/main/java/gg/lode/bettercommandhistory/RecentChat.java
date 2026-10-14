package gg.lode.bettercommandhistory;

import java.util.List;
import net.minecraft.client.Minecraft;

/** Where this version keeps the messages and commands sent this session, oldest first. */
public final class RecentChat {
    public static List<String> get() {
        return Minecraft.getInstance().gui.getChat().getRecentChat();
    }

    private RecentChat() {}
}
