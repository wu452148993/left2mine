package net.thecallunxz.left2mine.render.projectiles;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.thecallunxz.left2mine.entities.projectiles.EntityProjectile;

public class RenderProjectile extends Render<EntityProjectile> {
   public static final RenderProjectile.Factory FACTORY = new RenderProjectile.Factory();

   public RenderProjectile(RenderManager renderManager) {
      super(renderManager);
      this.shadowSize = 0.25F;
   }

   protected ResourceLocation getEntityTexture(EntityProjectile entity) {
      return null;
   }

   public void doRender(EntityProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(entityYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(entity.rotationPitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-0.5D, 0.0D, -0.5D);
      GlStateManager.scale(0.15D, 0.15D, 0.15D);
      this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(entity.getItem());
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.ITEM);
      EnumFacing[] var13 = EnumFacing.values();
      int var14 = var13.length;

      for(int var15 = 0; var15 < var14; ++var15) {
         EnumFacing enumfacing = var13[var15];
         this.renderQuads(buffer, model.getQuads((IBlockState)null, enumfacing, 0L));
      }

      this.renderQuads(buffer, model.getQuads((IBlockState)null, (EnumFacing)null, 0L));
      tessellator.draw();
      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   private void renderQuads(BufferBuilder renderer, List<BakedQuad> quads) {
      int i = 0;

      for(int j = quads.size(); i < j; ++i) {
         BakedQuad quad = (BakedQuad)quads.get(i);
         LightUtil.renderQuadColor(renderer, quad, -1);
      }

   }

   public static class Factory implements IRenderFactory<EntityProjectile> {
      public Render<? super EntityProjectile> createRenderFor(RenderManager manager) {
         return new RenderProjectile(manager);
      }
   }
}
