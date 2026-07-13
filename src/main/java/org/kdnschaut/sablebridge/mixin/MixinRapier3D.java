package org.kdnschaut.sablebridge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.physics.impl.rapier.Rapier3D", remap = false)
public class MixinRapier3D {

    @Dynamic("Execute dynamic runtime hook on hidden target method")
    @Inject(method = "loadLibrary", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onLoadLibrary(CallbackInfo ci) {
        if (!org.kdnschaut.sablebridge.SableBridge.isAndroid()) {
            return;
        }

        try {
            java.nio.file.Path dir = java.nio.file.Paths.get(".sable/natives");
            java.nio.file.Path tempFile = dir.resolve("sable_rapier_aarch64_linux.so");
            String targetPath = tempFile.toAbsolutePath().toString();

            org.kdnschaut.sablebridge.SableBridgeLoader.load(targetPath);
            ci.cancel();

        } catch (Throwable t) {
            System.err.println("[SableBridge] Critical failure forcing intercept on Android!");
            t.printStackTrace();
        }
    }
}