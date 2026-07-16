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

    private static final Path SABLE_BRIDGE_DIR = Paths.get("SableBridge");
    private static final Path ENGINE_DIR = SABLE_BRIDGE_DIR.resolve("Engine");

    public static void load(String var0) {
        if (!SableBridge.isMobileTablet()) {
            SableBridgeLogger.logSable("PC environment Detected! Skipping mobile interception.");
            return;
        }

        originalPathFallback = var0;

        try {
            if (!Files.exists(ENGINE_DIR)) {
                Files.createDirectories(ENGINE_DIR);
                SableBridgeLogger.logSable("Created missing folder: " + ENGINE_DIR.toAbsolutePath());
            }
        } catch (IOException e) {
            SableBridgeLogger.logSableWarn("Failed to auto-create directories: " + e.getMessage());
        }

        if (SableBridge.isIOS()) {
            if (isExperimentalEnabled() && isCustomEnginePresent()) {
                loadExperimentalEngine();
            } else {
                SableBridgeLogger.logSableWarn("iOS: Waiting for custom .dylib library. See SableBridge/Engine folder.");
            }
            return;
        }

        if (SableBridge.isAndroid()) {
            if (isExperimentalEnabled() && isCustomEnginePresent()) {
                if (!hasPrompted) {
                    SableBridgeLogger.logSable("EXPERIMENTAL: Delaying native load until User UI Confirmation.");
                    return;
                } else {
                    loadExperimentalEngine();
                }
            } else {
                loadDefaultNative();
            }
        }
    }

    public static boolean isCustomEnginePresent() {
        if (!Files.exists(ENGINE_DIR) || !Files.isDirectory(ENGINE_DIR)) return false;
        try (Stream<Path> paths = Files.list(ENGINE_DIR)) {
            return paths.anyMatch(p -> Files.isRegularFile(p) && p.toFile().length() > 1024);
        } catch (IOException e) {
            return false;
        }
    }

    public static void loadExperimentalEngine() {
        if (Files.exists(ENGINE_DIR) && Files.isDirectory(ENGINE_DIR)) {
            try (Stream<Path> paths = Files.list(ENGINE_DIR)) {
                Path targetFile = paths
                        .filter(Files::isRegularFile)
                        .findFirst()
                        .orElse(null);

                if (targetFile != null && Files.isReadable(targetFile) && Files.size(targetFile) > 1024) {
                    SableBridgeLogger.logSable("EXPERIMENTAL: Verified custom file: " + targetFile);
                    
                    if (SableBridge.isAndroid()) {
                        Path tempFile = Files.createTempFile("sable_custom_rapier", ".so");
                        tempFile.toFile().deleteOnExit();
                        Files.copy(targetFile, tempFile, StandardCopyOption.REPLACE_EXISTING);
                        
                        System.load(tempFile.toAbsolutePath().toString());
                    } else if (SableBridge.isIOS()) {
                        System.load(targetFile.toAbsolutePath().toString());
                    } else {
                        System.load(targetFile.toAbsolutePath().toString());
                    }
                    
                    SableBridge.logNativeSuccess();
                    SableBridgeLogger.flush();
                    return;
                } else {
                    SableBridgeLogger.logSableWarn("EXPERIMENTAL: No valid custom engine found in SableBridge/Engine/ folder.");
                }
            } catch (Throwable e) {
                SableBridgeLogger.logSableWarn("EXPERIMENTAL: Error loading experimental engine: " + e.getMessage());
            }
        }

        SableBridgeLogger.logSableWarn("EXPERIMENTAL: Fallback triggered to prevent crash.");
        if (SableBridge.isAndroid()) {
            loadDefaultNative();
        }
    }

    public static void loadDefaultNative() {
        try (InputStream var1 = SableBridgeLoader.class.getResourceAsStream(ANDROID_NATIVE)) {
            if (var1 == null) {
                if (originalPathFallback != null) {
                    System.load(originalPathFallback);
                } else {
                    SableBridgeLogger.logSableWarn("Failed to load native: Fallback path is null.");
                }
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
        if (!SableBridge.isMobileTablet()) return false;

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
