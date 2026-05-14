package dev.sableandroid;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TickThrottleConfig {
   private static final String CONFIG_FILE = "config/sable-android-patches.toml";
   private static final Map<String, Integer> DEFAULTS = new LinkedHashMap<>();
   private final Map<String, Integer> throttleMap = new LinkedHashMap<>();

   public void loadOrCreate() {
      Path var1 = Paths.get("config/sable-android-patches.toml");
      this.throttleMap.putAll(DEFAULTS);
      if (!Files.exists(var1)) {
         this.writeToFile(var1);
         SableAndroid.LOGGER.info("[SableAndroid] Created default patch config at {}", new Object[]{"config/sable-android-patches.toml"});
      } else {
         try {
            List var2 = Files.readAllLines(var1);
            boolean var3 = false;

            for (String var5 : var2) {
               var5 = var5.trim();
               if (var5.equals("[tick_throttle]")) {
                  var3 = true;
               } else if (var5.startsWith("[")) {
                  var3 = false;
               } else if (var3 && !var5.isEmpty()) {
                  int var6 = var5.indexOf(61);
                  if (var6 >= 0) {
                     String var7 = var5.substring(0, var6).trim().replace('.', '/');
                     String var8 = var5.substring(var6 + 1).trim();

                     try {
                        int var9 = Integer.parseInt(var8);
                        if (var9 >= 0) {
                           this.throttleMap.put(var7, var9);
                        }
                     } catch (NumberFormatException var10) {
                     }
                  }
               }
            }

            SableAndroid.LOGGER.info("[SableAndroid] Loaded patch config from {}", new Object[]{"config/sable-android-patches.toml"});
         } catch (IOException var11) {
            SableAndroid.LOGGER.error("[SableAndroid] Failed to read config: {}", new Object[]{var11.getMessage()});
         }
      }
   }

   public void applyAndSave(Map<String, Integer> var1) {
      this.throttleMap.clear();
      this.throttleMap.putAll(var1);
      TickThrottleManager.updateIntervals(this.throttleMap);
      this.writeToFile(Paths.get("config/sable-android-patches.toml"));
   }

   private void writeToFile(Path var1) {
      try {
         Files.createDirectories(var1.getParent());

         try (PrintWriter var2 = new PrintWriter(Files.newBufferedWriter(var1))) {
            var2.println("[tick_throttle]");

            for (Entry var4 : this.throttleMap.entrySet()) {
               var2.println(((String)var4.getKey()).replace('/', '.') + " = " + var4.getValue());
            }
         }
      } catch (IOException var7) {
         SableAndroid.LOGGER.error("[SableAndroid] Failed to write config: {}", new Object[]{var7.getMessage()});
      }
   }

   public int getThrottle(String var1) {
      return this.throttleMap.getOrDefault(var1, 1);
   }

   public Map<String, Integer> getAll() {
      return Collections.unmodifiableMap(this.throttleMap);
   }

   static {
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/gimbal_sensor/GimbalSensorBlockEntity", 4);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/velocity_sensor/VelocitySensorBlockEntity", 3);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/altitude_sensor/AltitudeSensorBlockEntity", 3);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/spring/SpringBlockEntity", 3);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/nav_table/NavTableBlockEntity", 4);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/lasers/optical_sensor/OpticalSensorBlockEntity", 2);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/redstone_magnet/RedstoneMagnetBlockEntity", 3);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/docking_connector/DockingConnectorBlockEntity", 3);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/nameplate/NameplateBlockEntity", 4);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/rope/rope_winch/RopeWinchBlockEntity", 2);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/swivel_bearing/SwivelBearingBlockEntity", 2);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/physics_assembler/PhysicsAssemblerBlockEntity", 2);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/redstone/AbstractLinkedReceiverBlockEntity", 2);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/redstone/redstone_inductor/RedstoneInductorBlockEntity", 2);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/throttle_lever/ThrottleLeverBlockEntity", 2);
      DEFAULTS.put("dev/simulated_team/simulated/content/blocks/merging_glue/MergingGlueBlockEntity", 3);
   }
}
