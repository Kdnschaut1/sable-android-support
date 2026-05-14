package dev.sableandroid;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SableAndroidLoader {
   private static final String ANDROID_NATIVE = "/natives/sable_rapier/sable_rapier_aarch64_android.so";

   public static void load(String var0) {
      if (!SableAndroid.isAndroid()) {
         SableBridgeLogger.logSable("PC environment - using original native: " + var0);
         System.load(var0);
      } else {
         SableBridgeLogger.logSable("Android environment - intercepting native load");
         SableBridgeLogger.logSable("Original path: " + var0);

         try (InputStream var1 = SableAndroidLoader.class.getResourceAsStream("/natives/sable_rapier/sable_rapier_aarch64_android.so")) {
            if (var1 == null) {
               SableBridgeLogger.logSableWarn("Android native not found in JAR: /natives/sable_rapier/sable_rapier_aarch64_android.so");
               SableBridgeLogger.logSableWarn("Falling back to original path");
               System.load(var0);
            } else {
               Path var2 = Files.createTempFile("sable_rapier_android", ".so");
               var2.toFile().deleteOnExit();
               Files.copy(var1, var2, StandardCopyOption.REPLACE_EXISTING);
               long var3 = Files.size(var2) / 1024L;
               SableAndroid.logNativeLoad(var0, var3);
               System.load(var2.toAbsolutePath().toString());
               SableAndroid.logNativeSuccess();
               SableAndroid.logRendererInfo();
               SableBridgeLogger.flush();
            }
         } catch (IOException var7) {
            SableAndroid.logNativeError(var7.getMessage());
            SableBridgeLogger.flush();
            throw new RuntimeException("[SableAndroid] Failed to load Android Rapier native", var7);
         }
      }
   }
}
