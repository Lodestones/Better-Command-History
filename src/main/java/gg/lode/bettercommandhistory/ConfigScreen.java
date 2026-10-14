package gg.lode.bettercommandhistory;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/** Settings screen, opened from Mod Menu. Every change applies and saves straight away. */
public class ConfigScreen extends Screen {
    private static final int WIDTH = 210;
    /** Slider stops for expiry, in days. The last stop, {@link Config#NEVER}, keeps commands forever. */
    private static final int[] EXPIRY_STOPS = {1, 2, 3, 5, 7, 14, 30, 60, 90, Config.NEVER};

    private final Screen parent;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Config config = Config.get();

    public ConfigScreen(Screen parent) {
        super(Component.literal("Better Command History"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        layout.addTitleHeader(title, font);

        LinearLayout body = layout.addToContents(LinearLayout.vertical().spacing(8));
        body.addChild(CycleButton.onOffBuilder(config.saveHistory)
                .create(0, 0, WIDTH, 20, Component.literal("Save history"), (button, value) -> {
                    config.saveHistory = value;
                    changed();
                }));
        body.addChild(new ExpirySlider());
        body.addChild(new SizeSlider());
        body.addChild(Button.builder(Component.literal("Clear history"), button -> {
            SavedCommands.clear();
            RecentChat.get().clear();
        }).width(WIDTH).build());

        layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> onClose()).width(WIDTH).build());
        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
    }

    @Override
    public void onClose() {
        Screens.open(parent);
    }

    private void changed() {
        config.save();
        SavedCommands.apply();
    }

    private static int stopIndex(int days) {
        for (int i = 0; i < EXPIRY_STOPS.length; i++) {
            if (EXPIRY_STOPS[i] == days) {
                return i;
            }
        }
        return 4;
    }

    private class ExpirySlider extends AbstractSliderButton {
        ExpirySlider() {
            super(0, 0, WIDTH, 20, Component.empty(), stopIndex(config.expiryDays) / (double) (EXPIRY_STOPS.length - 1));
            updateMessage();
        }

        private int days() {
            return EXPIRY_STOPS[(int) Math.round(value * (EXPIRY_STOPS.length - 1))];
        }

        @Override
        protected void updateMessage() {
            int days = days();
            String label = days == Config.NEVER ? "Never" : days == 1 ? "1 day" : days + " days";
            setMessage(Component.literal("Forget after: " + label));
        }

        @Override
        protected void applyValue() {
            if (days() != config.expiryDays) {
                config.expiryDays = days();
                changed();
            }
        }
    }

    private class SizeSlider extends AbstractSliderButton {
        private static final int STEP = 10;

        SizeSlider() {
            super(0, 0, WIDTH, 20, Component.empty(),
                    (config.historySize - Config.MIN_SIZE) / (double) (Config.MAX_SIZE - Config.MIN_SIZE));
            updateMessage();
        }

        private int size() {
            int raw = Config.MIN_SIZE + (int) Math.round(value * (Config.MAX_SIZE - Config.MIN_SIZE));
            return Math.round(raw / (float) STEP) * STEP;
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal("Commands kept: " + size()));
        }

        @Override
        protected void applyValue() {
            if (size() != config.historySize) {
                config.historySize = size();
                changed();
            }
        }
    }
}
