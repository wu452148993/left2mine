package net.thecallunxz.left2mine.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class InitKeybinding {
   public static KeyBinding reload;
   public static KeyBinding changeHand;
   public static KeyBinding flashlight;

   public static void init() {
      Minecraft.getMinecraft().gameSettings.keyBindSwapHands.setKeyCode(47);
      reload = new KeyBinding("key.reload", 19, "key.category.left2mine");
      ClientRegistry.registerKeyBinding(reload);
      flashlight = new KeyBinding("key.flashlight", 33, "key.category.left2mine");
      ClientRegistry.registerKeyBinding(flashlight);
   }
}
