package dev.sableandroid.mixin;

import dev.sableandroid.SableAndroidLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "dev.ryanhcode.sable.physics.impl.rapier.Rapier3D", remap = false)
public class Rapier3DMixin {
   @Redirect(method = "loadLibrary", at = @At(value = "INVOKE", target = "Ljava/lang/System;load(Ljava/lang/String;)V"), remap = false)
   private static void redirectLoad(String var0) {
      SableAndroidLoader.load(var0);
   }
}
