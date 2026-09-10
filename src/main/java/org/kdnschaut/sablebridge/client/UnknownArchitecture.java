package org.kdnschaut.sablebridge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.kdnschaut.sablebridge.SableBridge;
import org.kdnschaut.sablebridge.SableBridgeLogger;

@EventBusSubscriber(modid = "sablebridge", value = Dist.CLIENT)
public class UnknownArchitecture {

    public static boolean hasprompted = false;

    public static void showUnknownArchitectureScreen() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new UnknownArchitectureScreen());
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (!(event.getScreen() instanceof TitleScreen)) {
            return;
        }

        if (SableBridge.isDesktop()) {
            hasprompted = true;
            return;
        }

        if (!SableBridge.getarchitecture().equals("unknown")) {
            return;
        }

        hasprompted = true;

        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new UnknownArchitectureScreen());
        event.setCanceled(true);
    }

    public static class UnknownArchitectureScreen extends Screen {

        private final String architecture;

        protected UnknownArchitectureScreen() {
            super(Component.literal("Unknown Architecture"));
            this.architecture = System.getProperty("os.arch", "unknown");
        }

        @Override
        protected void init() {
            super.init();

            this.addRenderableWidget(
                    Button.builder(
                            Component.literal("Report Problem"),
                            button -> {
                                copyDiscordUrl();
                                button.setMessage(Component.literal("URL Copied!"));
                            }
                    ).bounds(
                            this.width / 2 - 100,
                            this.height / 2 + 40,
                            200,
                            20
                    ).build()
            );
        }

        @Override
        public void render(
                GuiGraphics guiGraphics,
                int mouseX,
                int mouseY,
                float partialTick
        ) {
            this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

            super.render(guiGraphics, mouseX, mouseY, partialTick);

            guiGraphics.drawCenteredString(
                    this.font,
                    Component.literal("[SableBridge]"),
                    this.width / 2,
                    20,
                    0xFFFFFF
            );

            Component message = Component.literal(
                    "Detected Unknown Architecture: " + architecture
            );

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(1.5F, 1.5F, 1.5F);

            guiGraphics.drawCenteredString(
                    this.font,
                    message,
                    this.width / 3,
                    this.height / 3 - 13,
                    0xFFFFFF
            );

            guiGraphics.pose().popPose();
        }

        private void copyDiscordUrl() {
            Minecraft.getInstance().keyboardHandler.setClipboard(
                    "https://discord.gg/ESfAbHxrCN"
            );

            SableBridgeLogger.logSable(
                    "Discord URL copied to clipboard."
            );
        }

        @Override
        public boolean shouldCloseOnEsc() {
            return false;
        }
    }
}