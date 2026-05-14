package dev.sableandroid;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SableBridgeLogger {
   private static final String LOG_DIR = "SableBridge_Logs";
   private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
   private static PrintWriter bridgeWriter;
   private static PrintWriter sableWriter;
   private static PrintWriter renderWriter;

   public static void init() {
      try {
         Path var0 = Paths.get("SableBridge_Logs");
         Files.createDirectories(var0);
         String var1 = LocalDateTime.now().format(FILE_TIMESTAMP);
         archiveOldLog(var0, "SableBridge_Latest.log", "SableBridge_" + var1 + ".log");
         archiveOldLog(var0, "Sable_Latest.log", "Sable_" + var1 + ".log");
         archiveOldLog(var0, "Render_Latest.log", "Render_" + var1 + ".log");
         bridgeWriter = openWriter(var0.resolve("SableBridge_Latest.log"));
         sableWriter = openWriter(var0.resolve("Sable_Latest.log"));
         renderWriter = openWriter(var0.resolve("Render_Latest.log"));
         logBridge("SableBridge Logger initialized - Session: " + var1);
         logBridge("Log directory: " + var0.toAbsolutePath());
      } catch (IOException var2) {
         SableAndroid.LOGGER.error("[SableAndroid] Failed to initialize SableBridge log directory: {}", new Object[]{var2.getMessage()});
      }
   }

   private static void archiveOldLog(Path var0, String var1, String var2) {
      try {
         Path var3 = var0.resolve(var1);
         if (Files.exists(var3) && Files.size(var3) > 0L) {
            Files.move(var3, var0.resolve(var2), StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (IOException var4) {
      }
   }

   private static PrintWriter openWriter(Path var0) throws IOException {
      return new PrintWriter(new BufferedWriter(new FileWriter(var0.toFile(), false)), true);
   }

   private static String timestamp() {
      return "[" + LocalDateTime.now().format(TIMESTAMP) + "] ";
   }

   public static void logBridge(String var0) {
      String var1 = timestamp() + "[SableBridge] " + var0;
      SableAndroid.LOGGER.info("[SableAndroid] {}", new Object[]{var0});
      if (bridgeWriter != null) {
         bridgeWriter.println(var1);
      }
   }

   public static void logBridgeWarn(String var0) {
      String var1 = timestamp() + "[SableBridge/WARN] " + var0;
      SableAndroid.LOGGER.warn("[SableAndroid] {}", new Object[]{var0});
      if (bridgeWriter != null) {
         bridgeWriter.println(var1);
      }
   }

   public static void logBridgeError(String var0) {
      String var1 = timestamp() + "[SableBridge/ERROR] " + var0;
      SableAndroid.LOGGER.error("[SableAndroid] {}", new Object[]{var0});
      if (bridgeWriter != null) {
         bridgeWriter.println(var1);
      }
   }

   public static void logSable(String var0) {
      String var1 = timestamp() + "[Sable] " + var0;
      SableAndroid.LOGGER.info("[Sable] {}", new Object[]{var0});
      if (sableWriter != null) {
         sableWriter.println(var1);
      }
   }

   public static void logSableWarn(String var0) {
      String var1 = timestamp() + "[Sable/WARN] " + var0;
      SableAndroid.LOGGER.warn("[Sable] {}", new Object[]{var0});
      if (sableWriter != null) {
         sableWriter.println(var1);
      }
   }

   public static void logRender(String var0) {
      String var1 = timestamp() + "[Render] " + var0;
      SableAndroid.LOGGER.info("[Render] {}", new Object[]{var0});
      if (renderWriter != null) {
         renderWriter.println(var1);
      }
   }

   public static void logRenderWarn(String var0) {
      String var1 = timestamp() + "[Render/WARN] " + var0;
      SableAndroid.LOGGER.warn("[Render] {}", new Object[]{var0});
      if (renderWriter != null) {
         renderWriter.println(var1);
      }
   }

   public static void flush() {
      if (bridgeWriter != null) {
         bridgeWriter.flush();
      }

      if (sableWriter != null) {
         sableWriter.flush();
      }

      if (renderWriter != null) {
         renderWriter.flush();
      }
   }

   public static void close() {
      flush();
      if (bridgeWriter != null) {
         bridgeWriter.close();
      }

      if (sableWriter != null) {
         sableWriter.close();
      }

      if (renderWriter != null) {
         renderWriter.close();
      }
   }
}
