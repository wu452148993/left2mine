package net.thecallunxz.left2mine.gui;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class GuiGameOver extends GuiScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
   private static final ResourceLocation BACKGROUND = new ResourceLocation("left2mine:textures/gui/lose_background.png");
   private float time;
   private List<String> lines;
   private int totalScrollLength;
   private float scrollSpeed = 1.25F;

   public void updateScreen() {
      float f = 210.0F;
      if (this.time > f) {
         this.sendRespawnPacket();
      }

   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   private void sendRespawnPacket() {
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void initGui() {
   }

   private void drawWinGameScreen(int p_146575_1_, int p_146575_2_, float p_146575_3_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      this.mc.getTextureManager().bindTexture(BACKGROUND);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      int j = this.width / 2;
      int k = this.height / 2;
      int i = this.width;
      float f = -this.time * 0.5F * this.scrollSpeed;
      float f1 = (float)this.height - this.time * 0.5F * this.scrollSpeed;
      float f2 = 0.015625F;
      float f3 = this.time * 0.02F;
      float f4 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
      float f5 = (f4 - 20.0F - this.time) * 0.005F;
      if (f5 < f3) {
         f3 = f5;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      f3 *= f3;
      f3 = f3 * 96.0F / 255.0F;
      bufferbuilder.pos(0.0D, (double)this.height, (double)this.zLevel).tex(0.0D, (double)(f * 0.015625F)).color(f3, f3, f3, 1.0F).endVertex();
      bufferbuilder.pos((double)i, (double)this.height, (double)this.zLevel).tex((double)((float)i * 0.015625F), (double)(f * 0.015625F)).color(f3, f3, f3, 1.0F).endVertex();
      bufferbuilder.pos((double)i, 0.0D, (double)this.zLevel).tex((double)((float)i * 0.015625F), (double)(f1 * 0.015625F)).color(f3, f3, f3, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, 0.0D, (double)this.zLevel).tex(0.0D, (double)(f1 * 0.015625F)).color(f3, f3, f3, 1.0F).endVertex();
      tessellator.draw();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawWinGameScreen(mouseX, mouseY, partialTicks);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      int i = true;
      int j = this.width / 2;
      int k = this.height / 2;
      this.time += partialTicks;
      float f = -this.time * this.scrollSpeed;
      this.fontRenderer.drawStringWithShadow(I18n.format("gui.gameover", new Object[0]), (float)j - (float)(this.fontRenderer.getStringWidth(I18n.format("gui.gameover", new Object[0])) / 2), (float)k - 15.0F, 16777215);
      this.fontRenderer.drawStringWithShadow(I18n.format("gui.overwhelmed", new Object[0]), (float)j - (float)(this.fontRenderer.getStringWidth(I18n.format("gui.overwhelmed", new Object[0])) / 2), (float)k + 15.0F, 16777215);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, f, 0.0F);
      GlStateManager.disableAlpha();
      GlStateManager.popMatrix();
      this.mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_COLOR);
      int j1 = this.width;
      int k1 = this.height;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos(0.0D, (double)k1, (double)this.zLevel).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      bufferbuilder.pos((double)j1, (double)k1, (double)this.zLevel).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      bufferbuilder.pos((double)j1, 0.0D, (double)this.zLevel).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, 0.0D, (double)this.zLevel).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      tessellator.draw();
      GlStateManager.disableBlend();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
