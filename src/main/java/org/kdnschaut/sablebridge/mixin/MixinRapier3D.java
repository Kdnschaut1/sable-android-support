package org.kdnschaut.sablebridge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Dynamic;

@Mixin(targets = "dev.ryanhcode.sable.physics.impl.rapier.Rapier3D", remap = false)
public class MixinRapier3D {

    @Dynamic("Bypasses compiler verification to inject into loadLibrary at runtime")
    private static void loadLibrary() {
        try {
            // 1. Emulate the standard path layout that the mod engine expects
            java.nio.file.Path dir = java.nio.file.Paths.get(".sable/natives");
            java.nio.file.Path tempFile = dir.resolve("sable_rapier_aarch64_linux.so");
            String targetPath = tempFile.toAbsolutePath().toString();

            // 2. Safely route execution over to your outer bridge loader
            org.kdnschaut.sablebridge.SableBridgeLoader.load(targetPath);

        } catch (Throwable t) {
            System.err.println("[SableBridge] Critical failure forcing intercept on JarJar engine module!");
            t.printStackTrace();
        }
    }
}