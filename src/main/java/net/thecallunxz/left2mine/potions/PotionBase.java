package net.thecallunxz.left2mine.potions;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionBase extends Potion {
   private final ResourceLocation iconTexture;

   public PotionBase(boolean isBadEffect, int liquidColor, String name) {
      super(isBadEffect, liquidColor);
      setPotionName(this, name);
      this.iconTexture = new ResourceLocation("left2mine", "textures/potions/placeholder.png");
   }

   public PotionBase(boolean isBadEffect, int liquidR, int liquidG, int liquidB, String name) {
      this(isBadEffect, (new Color(liquidR, liquidG, liquidB)).getRGB(), name);
   }

   public static void setPotionName(Potion potion, String potionName) {
      potion.setRegistryName("left2mine", potionName);
      potion.setPotionName("effect." + potion.getRegistryName().toString());
   }

   public boolean hasStatusIcon() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      if (mc.currentScreen != null) {
         mc.getTextureManager().bindTexture(this.iconTexture);
         Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
      }

   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      mc.getTextureManager().bindTexture(this.iconTexture);
      Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
   }
}
