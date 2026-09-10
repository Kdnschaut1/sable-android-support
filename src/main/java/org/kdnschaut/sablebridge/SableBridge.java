package org.kdnschaut.sablebridge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (var0.contains("android") ||
                var1.contains("android") ||
                var2.contains("android")) {
            return true;
        }
        try {
            Class.forName("android.os.Build");
            return true;
        } catch (ClassNotFoundException var4) {
            return false;
        }
    }

    public static boolean isIOS() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        String osArch = System.getProperty("os.arch", "").toLowerCase();
        boolean isMacBased = osName.contains("mac") || osName.contains("darwin");
        if (!isMacBased) {
            return false;
        }
        if (osArch.contains("arm") || osArch.contains("aarch")) {
            String userHome = System.getProperty("user.home", "");
            if (userHome.contains("/mobile/")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDesktop() {
        if (isAndroid()) {
            return false;
        } else if (isIOS()) {
            return false;
        } else {
            return true;
        }
    }

    public static String getarchitecture() {
        String arch = System.getProperty("os.arch", "").toLowerCase();

        switch (arch) {
            case "arm":
            case "arm32":
            case "armeabi":
            case "armeabi-v7a":
                return "armeabi-v7a";

            case "arm64":
            case "aarch64":
            case "arm64-v8a":
                return "arm64-v8a";

            case "x86":
            case "i386":
            case "i686":
                return "x86";

            case "x86_64":
            case "amd64":
                return "x86_64";

            default:
                return "unknown";
        }
    }
}