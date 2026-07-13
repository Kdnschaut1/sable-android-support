package org.kdnschaut.sablebridge;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SableBridgeLoader {
    private static final String ANDROID_NATIVE = "/natives/sablebridge/sable_rapier_aarch64_android.so";

    public static void load(String var0) {
        if (!SableBridge.isAndroid()) {
            SableBridgeLogger.logSable("PC environment Detected! Skipping)");
        } else {
            SableBridgeLogger.logSable("Android environment - intercepting native load");
            SableBridgeLogger.logSable("Original path: " + var0);

            try (InputStream var1 = SableBridgeLoader.class.getResourceAsStream(ANDROID_NATIVE)) {
                if (var1 == null) {
                    SableBridgeLogger.logSableWarn("Android native not found in JAR: " + ANDROID_NATIVE);
                    SableBridgeLogger.logSableWarn("Falling back to original path");
                    System.load(var0);
                } else {
                    Path var2 = Files.createTempFile("sable_rapier_android", ".so");
                    var2.toFile().deleteOnExit();
                    Files.copy(var1, var2, StandardCopyOption.REPLACE_EXISTING);
                    long var3 = Files.size(var2) / 1024L;
                    SableBridge.logNativeLoad(var0, var3);
                    System.load(var2.toAbsolutePath().toString());
                    SableBridge.logNativeSuccess();
                    SableBridgeLogger.flush();
                }
            } catch (IOException var7) {
                SableBridge.logNativeError(var7.getMessage());
                SableBridgeLogger.flush();
                throw new RuntimeException("[SableAndroid] Failed to load Android Rapier native", var7);
            }
        }
    }
}