package org.kdnschaut.sablebridge.Experimentals;

import net.minecraft.client.gui.screens.TitleScreen;
import org.kdnschaut.sablebridge.SableBridgeLoader;
import org.kdnschaut.sablebridge.Experimentals.ExperimentalPromptScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        // Safe-check: Force check if system is strictly Android AND experimental is active
        if (org.kdnschaut.sablebridge.SableBridge.isAndroid()
                && SableBridgeLoader.isExperimentalEnabled()
                && !SableBridgeLoader.hasPrompted) {

            TitleScreen self = (TitleScreen)(Object)this;
            self.getMinecraft().setScreen(new ExperimentalPromptScreen(self));
        }
    }
}