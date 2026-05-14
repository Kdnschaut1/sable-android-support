package dev.sableandroid;

import net.fabricmc.api.ClientModInitializer;

public class SableAndroidFabricClient implements ClientModInitializer {
   public void onInitializeClient() {
      SableAndroid.LOGGER.info("[SableAndroid] Fabric client initialized.", new Object[0]);
   }
}
