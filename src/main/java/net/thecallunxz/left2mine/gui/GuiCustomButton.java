package net.thecallunxz.left2mine.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustomButton extends GuiButton {
   public GuiCustomButton(int buttonId, int x, int y, String buttonText) {
      this(buttonId, x, y, 100, 10, buttonText);
   }

   public GuiCustomButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
      super(buttonId, x, y, widthIn, heightIn, buttonText);
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         FontRenderer fontrenderer = mc.fontRenderer;
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int j = 14737632;
         if (this.packedFGColour != 0) {
            j = this.packedFGColour;
         } else if (!this.enabled) {
            j = 10526880;
         } else if (this.hovered) {
            j = 16777120;
         }

         this.drawString(fontrenderer, this.displayString, this.x + 2, this.y + (this.height - 8) / 2, j);
      }

   }
}
