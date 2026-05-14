package dev.sableandroid.mixin;

import dev.sableandroid.SableAndroid;
import dev.sableandroid.TickThrottleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class TickThrottleMixin {
   @Mixin(targets = "dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorBlockEntity", remap = false)
   public abstract static class AltitudeSensor {
      @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
      private void sable_android$throttle(CallbackInfo var1) {
         if (SableAndroid.isAndroid() && !TickThrottleManager.shouldTick(this, 3)) {
            var1.cancel();
         }
      }
   }

   @Mixin(targets = "dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity", remap = false)
   public abstract static class DockingConnector {
      @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
      private void sable_android$throttle(CallbackInfo var1) {
         if (SableAndroid.isAndroid() && !TickThrottleManager.shouldTick(this, 3)) {
            var1.cancel();
         }
      }
   }

   @Mixin(targets = "dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlockEntity", remap = false)
   public abstract static class GimbalSensor {
      @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
      private void sable_android$throttle(CallbackInfo var1) {
         if (SableAndroid.isAndroid() && !TickThrottleManager.shouldTick(this, 4)) {
            var1.cancel();
         }
      }
   }

   @Mixin(targets = "dev.simulated_team.simulated.content.blocks.merging_glue.MergingGlueBlockEntity", remap = false)
   public abstract static class MergingGlue {
      @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
      private void sable_android$throttle(CallbackInfo var1) {
         if (SableAndroid.isAndroid() && !TickThrottleManager.shouldTick(this, 3)) {
            var1.cancel();
         }
      }
   }

   @Mixin(targets = "dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity", remap = false)
   public abstract static class NavTable {
      @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
      private void sable_android$throttle(CallbackInfo var1) {
         if (SableAndroid.isAndroid() && !TickThrottleManager.shouldTick(this, 4)) {
            var1.cancel();
         }
      }
   }

   @Mixin(targets = "dev.simulated_team.simulated.content.blocks.physics_assembler.PhysicsAssemblerBlockEntity", remap = false)
   public abstract static class PhysicsAssembler {
      @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
      private void sable_android$throttle(CallbackInfo var1) {
         if (SableAndroid.isAndroid() && !TickThrottleManager.shouldTick(this, 2)) {
            var1.cancel();
         }
      }
   }

   @Mixin(targets = "dev.simulated_team.simulated.content.blocks.redstone_magnet.RedstoneMagnetBlockEntity", remap = false)
   public abstract static class RedstoneMagnet {
      @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
      private void sable_android$throttle(CallbackInfo var1) {
         if (SableAndroid.isAndroid() && !TickThrottleManager.shouldTick(this, 3)) {
            var1.cancel();
         }
      }
   }

   @Mixin(targets = "dev.simulated_team.simulated.content.blocks.spring.SpringBlockEntity", remap = false)
   public abstract static class Spring {
      @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
      private void sable_android$throttle(CallbackInfo var1) {
         if (SableAndroid.isAndroid() && !TickThrottleManager.shouldTick(this, 3)) {
            var1.cancel();
         }
      }
   }

   @Mixin(targets = "dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlockEntity", remap = false)
   public abstract static class SwivelBearing {
      @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
      private void sable_android$throttle(CallbackInfo var1) {
         if (SableAndroid.isAndroid() && !TickThrottleManager.shouldTick(this, 2)) {
            var1.cancel();
         }
      }
   }

   @Mixin(targets = "dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlockEntity", remap = false)
   public abstract static class ThrottleLever {
      @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
      private void sable_android$throttle(CallbackInfo var1) {
         if (SableAndroid.isAndroid() && !TickThrottleManager.shouldTick(this, 2)) {
            var1.cancel();
         }
      }
   }

   @Mixin(targets = "dev.simulated_team.simulated.content.blocks.velocity_sensor.VelocitySensorBlockEntity", remap = false)
   public abstract static class VelocitySensor {
      @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
      private void sable_android$throttle(CallbackInfo var1) {
         if (SableAndroid.isAndroid() && !TickThrottleManager.shouldTick(this, 3)) {
            var1.cancel();
         }
      }
   }
}
