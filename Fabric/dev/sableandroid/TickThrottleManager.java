package dev.sableandroid;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class TickThrottleManager {
   private static final WeakHashMap<Object, int[]> COUNTERS = new WeakHashMap<>();
   private static final ConcurrentHashMap<String, Integer> LIVE_INTERVALS = new ConcurrentHashMap<>();

   public static boolean shouldTick(Object var0, int var1) {
      String var2 = var0.getClass().getName().replace('.', '/');
      int var3 = LIVE_INTERVALS.getOrDefault(var2, var1);
      if (var3 <= 1) {
         return var3 == 1;
      } else {
         int[] var4 = COUNTERS.computeIfAbsent(var0, var0x -> new int[]{0});
         var4[0]++;
         if (var4[0] >= var3) {
            var4[0] = 0;
            return true;
         } else {
            return false;
         }
      }
   }

   public static void updateIntervals(Map<String, Integer> var0) {
      LIVE_INTERVALS.clear();
      LIVE_INTERVALS.putAll(var0);
      COUNTERS.clear();
   }
}
