package net.thecallunxz.left2mine.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.render.specialinfected.RenderWitch;

@SideOnly(Side.CLIENT)
public class LayerWitchEyes implements LayerRenderer<EntityWitch> {
   private static final ResourceLocation WITCH_EYES = new ResourceLocation("left2mine:textures/entity/mobs/witch_eyes.png");
   private final RenderWitch witchRenderer;

   public LayerWitchEyes(RenderWitch witchRendererIn) {
      this.witchRenderer = witchRendererIn;
   }

   public void doRenderLayer(EntityWitch entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.witchRenderer.bindTexture(WITCH_EYES);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.disableLighting();
      GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
      Minecraft.getMinecraft().entityRenderer.disableLightmap();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.witchRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      Minecraft.getMinecraft().entityRenderer.enableLightmap();
      this.witchRenderer.setLightmap(entitylivingbaseIn);
      GlStateManager.enableLighting();
      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
