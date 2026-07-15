package org.kdnschaut.sablebridge.Experimentals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.kdnschaut.sablebridge.SableBridgeLoader;

public class ExperimentalPromptScreen extends Screen {
    private final Screen parent;

    public ExperimentalPromptScreen(Screen parent) {
        super(Component.literal("SableBridge Sideloading Warning"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = 120;
        int buttonHeight = 20;
        int spacing = 15;

        int startX = (this.width / 2) - (buttonWidth + spacing / 2);
        int startY = this.height / 2 + 50; // Shifted lower to make room for the warning text

        // "YES" Button - Loads the custom user file from external storage
        this.addRenderableWidget(Button.builder(Component.literal("Yes"), button -> {
            SableBridgeLoader.hasPrompted = true;
            SableBridgeLoader.loadExperimentalEngine();
            this.minecraft.setScreen(this.parent);
        }).bounds(startX, startY, buttonWidth, buttonHeight).build());

        // "NO" Button - Cancels and runs the stable default internal native
        this.addRenderableWidget(Button.builder(Component.literal("No"), button -> {
            SableBridgeLoader.hasPrompted = true;
            SableBridgeLoader.loadDefaultNative();
            this.minecraft.setScreen(this.parent);
        }).bounds(startX + buttonWidth + spacing, startY, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Renders the standard dimmed background (dark tint overlay) safely in modern Minecraft
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Header Title
        guiGraphics.drawCenteredString(this.font, "WARNING: EXTERNAL ENGINE SIDELOADING", centerX, centerY - 65, 0xFF5555); // Red text

        // Warning Body
        guiGraphics.drawCenteredString(this.font, "You are about to execute a custom native file directly from your device storage.", centerX, centerY - 40, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, "This can cause severe memory corruption, instability, or system crashes.", centerX, centerY - 25, 0xFFFFFF);

        // Disclaimer / Responsibility Check
        guiGraphics.drawCenteredString(this.font, "The Mod Owner (\"Kdnschaut\") is NOT responsible for any damage or malfunctions.", centerX, centerY + 0, 0xFFAA00); // Gold text
        guiGraphics.drawCenteredString(this.font, "Sideloading custom engines is entirely at your own risk.", centerX, centerY + 15, 0xFFAA00);

        guiGraphics.drawCenteredString(this.font, "Do you want to proceed with the external engine file?", centerX, centerY + 35, 0xAAAAAA);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        // Safe fallback to default internal load if the user hits ESC or closes the prompt
        if (!SableBridgeLoader.hasPrompted) {
            SableBridgeLoader.hasPrompted = true;
            SableBridgeLoader.loadDefaultNative();
        }
        this.minecraft.setScreen(this.parent);
    }
}