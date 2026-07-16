package org.kdnschaut.sablebridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class SableBridgeLoader {
    private static final String ANDROID_NATIVE = "/natives/sablebridge/sable_rapier_aarch64_android.so";
    public static boolean hasPrompted = false;
    public static String originalPathFallback = null;

    // Local directory paths
    private static final Path SABLE_BRIDGE_DIR = Paths.get("SableBridge");
    private static final Path ENGINE_DIR = SABLE_BRIDGE_DIR.resolve("Engine");

    public static void load(String var0) {
        if (!SableBridge.isAndroid()) {
            SableBridgeLogger.logSable("PC environment Detected! Skipping Android interception.");
            return;
        }

        originalPathFallback = var0;

        // Auto-create SableBridge/Engine directories if they are missing
        try {
            if (!Files.exists(ENGINE_DIR)) {
                Files.createDirectories(ENGINE_DIR);
                SableBridgeLogger.logSable("Created missing folder: " + ENGINE_DIR.toAbsolutePath());
            }
        } catch (IOException e) {
            SableBridgeLogger.logSableWarn("Failed to auto-create directories: " + e.getMessage());
        }

        if (isExperimentalEnabled()) {
            if (!hasPrompted) {
                SableBridgeLogger.logSableWarn(
                        "EXPERIMENTAL: No user confirmation was received before this native was needed. "
                                + "Falling back to the default native to avoid a crash. "
                                + "Visit the title screen and accept the sideloading prompt to use the custom engine instead.");
                loadDefaultNative();
                return;
            } else {
                loadExperimentalEngine();
            }
        } else {
            loadDefaultNative();
        }
    }

    public static void loadExperimentalEngine() {
        if (Files.exists(ENGINE_DIR) && Files.isDirectory(ENGINE_DIR)) {
            try (Stream<Path> paths = Files.list(ENGINE_DIR)) {
                // Find any regular file inside SableBridge/Engine/
                Path targetFile = paths
                        .filter(Files::isRegularFile)
                        .findFirst()
                        .orElse(null);

                // Verify the file exists, is readable, and is not empty
                if (targetFile != null && Files.isReadable(targetFile) && Files.size(targetFile) > 1024) {
                    SableBridgeLogger.logSable("EXPERIMENTAL: Verified and loading custom file: " + targetFile);
                    System.load(targetFile.toAbsolutePath().toString());
                    SableBridge.logNativeSuccess();
                    SableBridgeLogger.flush();
                    return;
                } else {
                    SableBridgeLogger.logSableWarn("EXPERIMENTAL: No valid custom engine found in SableBridge/Engine/ folder.");
                }
            } catch (Throwable e) {
                SableBridgeLogger.logSableWarn("EXPERIMENTAL: Error scanning Engine directory: " + e.getMessage());
            }
        }

        SableBridgeLogger.logSableWarn("EXPERIMENTAL: Fallback triggered to prevent crash.");
        loadDefaultNative();
    }

    public static void loadDefaultNative() {
        try (InputStream var1 = SableBridgeLoader.class.getResourceAsStream(ANDROID_NATIVE)) {
            if (var1 == null) {
                System.load(originalPathFallback);
            } else {
                Path var2 = Files.createTempFile("sable_rapier_android", ".so");
                var2.toFile().deleteOnExit();
                Files.copy(var1, var2, StandardCopyOption.REPLACE_EXISTING);
                System.load(var2.toAbsolutePath().toString());
                SableBridge.logNativeSuccess();
                SableBridgeLogger.flush();
            }
        } catch (IOException var7) {
            throw new RuntimeException("[SableAndroid] Failed to load Default Android Rapier native", var7);
        }
    }

    public static boolean isExperimentalEnabled() {
        if (!SableBridge.isAndroid()) return false;

        Path expFile = SABLE_BRIDGE_DIR.resolve("SableBridgeExperimentals.txt");
        if (!Files.exists(expFile)) return false;

        try (BufferedReader reader = Files.newBufferedReader(expFile)) {
            String line = reader.readLine();
            return line != null && line.trim().equalsIgnoreCase("true");
        } catch (IOException e) {
            return false;
        }
    }
}
