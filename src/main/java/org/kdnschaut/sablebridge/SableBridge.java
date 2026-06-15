package org.kdnschaut.sablebridge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@Mod("sablebridge")
public class SableBridge {
    public static final String MOD_ID = "sable_android";
    public static final Logger LOGGER = LoggerFactory.getLogger("SableAndroid");

    public SableBridge(IEventBus var1) {
        SableBridgeLogger.init();
        SableBridgeLogger.logBridge("Sable Android Support v1.2.0 (NeoForge) loaded");
        SableBridgeLogger.logBridge("Platform     : " + (isAndroid() ? "Android" : "PC/Desktop"));
        SableBridgeLogger.logBridge("OS Name      : " + System.getProperty("os.name", "unknown"));
        SableBridgeLogger.logBridge("OS Version   : " + System.getProperty("os.version", "unknown"));
        SableBridgeLogger.logBridge("OS Arch      : " + System.getProperty("os.arch", "unknown"));
        SableBridgeLogger.logBridge("Java Vendor  : " + System.getProperty("java.vendor", "unknown"));
        SableBridgeLogger.logBridge("Java Version : " + System.getProperty("java.version", "unknown"));
        if (!isAndroid()) {
            SableBridgeLogger.logBridge("Running on PC - native redirect inactive");
        }
    }

    public static void logNativeLoad(String var0, long var1) {
        SableBridgeLogger.logSable("Native intercepted: " + var0);
        SableBridgeLogger.logSable("Extracted Android native (" + var1 + "KB) - loading...");
    }

    public static void logNativeSuccess() {
        SableBridgeLogger.logSable("Rapier native loaded successfully");
    }

    public static void logNativeError(String var0) {
        SableBridgeLogger.logSableWarn("Native load error: " + var0);
    }


    public static boolean isAndroid() {
        String var0 = System.getProperty("os.version", "").toLowerCase();
        String var1 = System.getProperty("java.vendor", "").toLowerCase();
        String var2 = System.getProperty("java.vm.name", "").toLowerCase();
        if (var0.contains("android")) {
            return true;
        }

        if (var1.contains("android")) {
            return true;
        }

        if (var2.contains("android")) {
            return true;
        }

        try {
            Class.forName("android.os.Build");
            return true;
        } catch (ClassNotFoundException var4) {
            return false;
        }
    }
}