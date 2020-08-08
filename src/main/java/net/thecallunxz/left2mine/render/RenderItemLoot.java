package net.thecallunxz.left2mine.render;

import java.util.Random;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.thecallunxz.left2mine.entities.EntityItemLoot;

public class RenderItemLoot extends Render<EntityItemLoot> {
   private final RenderItem itemRenderer;
   private Random random = new Random();
   public static final RenderItemLoot.Factory FACTORY = new RenderItemLoot.Factory();

   public RenderItemLoot(RenderManager renderManagerIn, RenderItem p_i46167_2_) {
      super(renderManagerIn);
      this.itemRenderer = p_i46167_2_;
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   private int transformModelCount(EntityItemLoot itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_) {
      ItemStack itemstack = itemIn.getItem();
      Item item = itemstack.getItem();
      if (item == null) {
         return 0;
      } else {
         boolean flag = p_177077_9_.isGui3d();
         int i = this.getModelCount(itemstack);
         float var10000;
         if (this.shouldBob()) {
            var10000 = MathHelper.sin(((float)itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F;
         } else {
            var10000 = 0.0F;
         }

         float f2 = p_177077_9_.getItemCameraTransforms().getTransform(TransformType.GROUND).scale.y;
         GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_, (float)p_177077_6_);
         if (flag || this.renderManager.options != null) {
            IBlockState bsDown = itemIn.world.getBlockState(new BlockPos(itemIn.posX, itemIn.posY - 0.25D, itemIn.posZ));
            boolean inWater = itemIn.isInWater() || bsDown.getBlock() instanceof BlockLiquid || bsDown.getBlock() instanceof IFluidBlock;
            if (!itemIn.onGround && !inWater) {
               float f3 = (((float)itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * 57.295776F;
               GlStateManager.rotate(itemIn.hoverStart * 360.0F, 0.0F, 1.0F, 0.0F);
            } else {
               GlStateManager.rotate(itemIn.hoverStart * 360.0F, 0.0F, 1.0F, 0.0F);
            }
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         return i;
      }
   }

   protected int getModelCount(ItemStack stack) {
      int i = 1;
      if (stack.getCount() > 48) {
         i = 5;
      } else if (stack.getCount() > 32) {
         i = 4;
      } else if (stack.getCount() > 16) {
         i = 3;
      } else if (stack.getCount() > 1) {
         i = 2;
      }

      return i;
   }

   public void doRender(EntityItemLoot entity, double x, double y, double z, float entityYaw, float partialTicks) {
      ItemStack itemstack = entity.getItem();
      int i;
      if (itemstack != null && itemstack.getItem() != null) {
         i = Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata();
      } else {
         i = 187;
      }

      this.random.setSeed((long)i);
      boolean flag = false;
      if (this.bindEntityTexture(entity)) {
         this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
         flag = true;
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.pushMatrix();
      IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entity.world, (EntityLivingBase)null);
      int j = this.transformModelCount(entity, x, y, z, partialTicks, ibakedmodel);
      boolean flag1 = ibakedmodel.isGui3d();
      float f7;
      float f9;
      if (!flag1) {
         float f3 = -0.0F * (float)(j - 1) * 0.5F;
         f7 = -0.0F * (float)(j - 1) * 0.5F;
         f9 = -0.09375F * (float)(j - 1) * 0.5F;
         GlStateManager.translate(f3, f7, f9);
      }

      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(entity));
      }

      for(int k = 0; k < j; ++k) {
         if (flag1) {
            GlStateManager.pushMatrix();
            if (k > 0) {
               f7 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               f9 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f6 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.translate(this.shouldSpreadItems() ? f7 : 0.0F, this.shouldSpreadItems() ? f9 : 0.0F, f6);
            }

            ibakedmodel = ForgeHooksClient.handleCameraTransforms(ibakedmodel, TransformType.GROUND, false);
            this.itemRenderer.renderItem(itemstack, ibakedmodel);
            GlStateManager.popMatrix();
         } else {
            GlStateManager.pushMatrix();
            if (k > 0) {
               f7 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               f9 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               GlStateManager.translate(f7, f9, 0.0F);
            }

            ibakedmodel = ForgeHooksClient.handleCameraTransforms(ibakedmodel, TransformType.GROUND, false);
            this.itemRenderer.renderItem(itemstack, ibakedmodel);
            GlStateManager.popMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.09375F);
         }
      }

      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      this.bindEntityTexture(entity);
      if (flag) {
         this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
      }

      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   protected ResourceLocation getEntityTexture(EntityItemLoot entity) {
      return TextureMap.LOCATION_BLOCKS_TEXTURE;
   }

   public boolean shouldSpreadItems() {
      return true;
   }

   public boolean shouldBob() {
      return false;
   }

   public static class Factory implements IRenderFactory<EntityItemLoot> {
      public Render<? super EntityItemLoot> createRenderFor(RenderManager manager) {
         return new RenderItemLoot(manager, Minecraft.getMinecraft().getRenderItem());
      }
   }
}
