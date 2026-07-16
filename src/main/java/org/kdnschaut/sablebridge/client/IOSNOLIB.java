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
public class IOSNOLIB {

    public static boolean hasprompted = false;

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof TitleScreen mainMenu) {

            if (!SableBridge.isIOS()) {
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
                            SableBridgeLogger.logSable("User acknowledged iOS instructions. Checking for custom library...");
                            if (SableBridgeLoader.isExperimentalEnabled() && SableBridgeLoader.isCustomEnginePresent()) {
                                SableBridgeLoader.loadExperimentalEngine();
                            } else {
                                SableBridgeLogger.logSableWarn("Instructions acknowledged, but no library found yet. Please add the file and restart.");
                            }
                        } else {
                            SableBridgeLogger.logSable("User closed iOS warning. No native library will be loaded.");
                        }
                        mc.setScreen(mainMenu);
                    },
                    Component.literal("§c§lSableBridge iOS Warning"),
                    Component.literal(
                            "§4Warning: Native Android libraries (.so) do NOT work on iOS!\n\n" +
                                    "§6To make SableBridge work on your iPhone/iPad, you must compile the library yourself:\n\n" +
                                    "§eSource Code: §f§ohttps://github.com/ryanhcode/sable/tree/main/sable_rapier§r\n\n" +
                                    "§e1. Compile the code for iOS (ARM64, .dylib format).\n" +
                                    "§e2. Save the file (e.g., 'libsable_rapier_ios.dylib') to:\n" +
                                    "§f   §oinstance/SableBridge/Engine/§r\n" +
                                    "§e3. Create or edit the file:\n" +
                                    "§f   §oinstance/SableBridge/SableBridgeExperimentals.txt§r\n" +
                                    "§e4. Write exactly §ltrue§r into this text file.\n\n" +
                                    "§cWithout these steps, the mod will not work!"
                    ),
                    Component.literal("§aUnderstood"),
                    Component.literal("§cCancel")
            );
            mc.setScreen(confirmScreen);
        }
    }
}