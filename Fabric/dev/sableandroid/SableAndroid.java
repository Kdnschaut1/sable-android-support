package dev.sableandroid;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SableAndroid {
   public static final String MOD_ID = "sable_android";
   public static final Logger LOGGER = LoggerFactory.getLogger("SableAndroid");
   public static final TickThrottleConfig THROTTLE_CONFIG = new TickThrottleConfig();

   public static void init() {
      SableBridgeLogger.init();
      THROTTLE_CONFIG.loadOrCreate();
      SableBridgeLogger.logBridge("Sable Android Support v1.2.0 (Fabric) loaded");
      SableBridgeLogger.logBridge("Platform     : " + (isAndroid() ? "Android" : "PC/Desktop"));
      SableBridgeLogger.logBridge("OS Name      : " + System.getProperty("os.name", "unknown"));
      SableBridgeLogger.logBridge("OS Version   : " + System.getProperty("os.version", "unknown"));
      SableBridgeLogger.logBridge("OS Arch      : " + System.getProperty("os.arch", "unknown"));
      SableBridgeLogger.logBridge("Java Vendor  : " + System.getProperty("java.vendor", "unknown"));
      SableBridgeLogger.logBridge("Java Version : " + System.getProperty("java.version", "unknown"));
      SableBridgeLogger.logBridge("Throttle Config: " + THROTTLE_CONFIG.getAll().size() + " classes configured");
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

   public static void logRendererInfo() {
      try {
         Class var0 = Class.forName("org.lwjgl.opengl.GL11");
         Method var1 = var0.getMethod("glGetString", int.class);
         String var2 = (String)var1.invoke(null, 7938);
         String var3 = (String)var1.invoke(null, 7937);
         String var4 = (String)var1.invoke(null, 7936);
         SableBridgeLogger.logRender("GL Vendor   : " + var4);
         SableBridgeLogger.logRender("GL Renderer : " + var3);
         SableBridgeLogger.logRender("GL Version  : " + var2);
      } catch (Exception var5) {
         SableBridgeLogger.logRenderWarn("Could not query GL renderer info: " + var5.getMessage());
      }
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
