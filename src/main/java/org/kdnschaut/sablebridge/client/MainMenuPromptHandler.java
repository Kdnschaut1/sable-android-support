package org.kdnschaut.sablebridge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.kdnschaut.sablebridge.SableBridge;
import org.kdnschaut.sablebridge.SableBridgeLoader;
import org.kdnschaut.sablebridge.SableBridgeLogger;

@EventBusSubscriber(modid = "sablebridge", value = Dist.CLIENT)
public class MainMenuPromptHandler {

    public static boolean hasprompted = false;

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof TitleScreen mainMenu) {

            if (!SableBridge.isMobileTablet()) {
                hasprompted = true;
                SableBridgeLoader.hasPrompted = true;
                return;
            }

            boolean isExpEnabled = SableBridgeLoader.isExperimentalEnabled();
            boolean isCustomPresent = SableBridgeLoader.isCustomEnginePresent();

            if (!isExpEnabled || !isCustomPresent) {
                if (!hasprompted) {
                    hasprompted = true;
                    SableBridgeLoader.hasPrompted = true;
                    if (SableBridge.isAndroid()) {
                        SableBridgeLoader.loadDefaultNative();
                    }
                }
                return;
            }

            if (hasprompted) {
                return;
            }

            event.setCanceled(true);
            hasprompted = true;
            SableBridgeLoader.hasPrompted = true;

            Minecraft mc = Minecraft.getInstance();
            ConfirmScreen confirmScreen = new ConfirmScreen(
                    (confirmed) -> {
                        if (confirmed) {
                            SableBridgeLogger.logSable("User Accepted risk. Booting custom engine.");
                            SableBridgeLoader.loadExperimentalEngine();
                        } else {
                            SableBridgeLogger.logSable("User Didn't Accept. Falling back to default native.");
                            SableBridgeLoader.loadDefaultNative();
                        }

                        mc.setScreen(mainMenu);
                    },
                    Component.literal("SableBridge Warning"),
                    Component.literal("You are about to Sideload an External File. Are you sure to Load it?\n\n" +
                            "The Owner of SableBridge is not Responsible for your Risk!"),
                    Component.literal("Yes"),
                    Component.literal("No")
            );
            mc.setScreen(confirmScreen);
        }
    }
}