package gg.lode.bettercommandhistory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/** Opens a screen the way this version does. */
public final class Screens {
    public static void open(Screen screen) {
        Minecraft.getInstance().gui.setScreen(screen);
    }

    private Screens() {}
}
