package net.thecallunxz.left2mine.models;

import java.util.List;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.thecallunxz.left2mine.blocks.BlockFakeWall;
import org.apache.commons.lang3.tuple.Pair;

public class ModelFakeWall implements IBakedModel {
   private IBakedModel modelWhenNotCamouflaged;
   public static final ModelResourceLocation blockStatesFileName = new ModelResourceLocation("left2mine:fakewall");
   public static final ModelResourceLocation variantTagNorth = new ModelResourceLocation("left2mine:fakewall", "facing=north");
   public static final ModelResourceLocation variantTagEast = new ModelResourceLocation("left2mine:fakewall", "facing=east");
   public static final ModelResourceLocation variantTagSouth = new ModelResourceLocation("left2mine:fakewall", "facing=south");
   public static final ModelResourceLocation variantTagWest = new ModelResourceLocation("left2mine:fakewall", "facing=west");

   public ModelFakeWall(IBakedModel unCamouflagedModel) {
      this.modelWhenNotCamouflaged = unCamouflagedModel;
   }

   public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
      return this.handleBlockState(state).getQuads(state, side, rand);
   }

   private IBakedModel handleBlockState(@Nullable IBlockState iBlockState) {
      IBakedModel retval = this.modelWhenNotCamouflaged;
      IBlockState UNCAMOUFLAGED_BLOCK = Blocks.AIR.getDefaultState();
      if (iBlockState instanceof IExtendedBlockState) {
         IExtendedBlockState iExtendedBlockState = (IExtendedBlockState)iBlockState;
         IBlockState copiedBlockIBlockState = (IBlockState)iExtendedBlockState.getValue(BlockFakeWall.COPIEDBLOCK);
         if (copiedBlockIBlockState != UNCAMOUFLAGED_BLOCK) {
            Minecraft mc = Minecraft.getMinecraft();
            BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
            BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
            IBakedModel copiedBlockModel = blockModelShapes.getModelForState(copiedBlockIBlockState);
            retval = copiedBlockModel;
         }
      }

      return retval;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.modelWhenNotCamouflaged.getParticleTexture();
   }

   public boolean isAmbientOcclusion() {
      return true;
   }

   public boolean isGui3d() {
      return this.modelWhenNotCamouflaged.isGui3d();
   }

   public boolean isBuiltInRenderer() {
      return this.modelWhenNotCamouflaged.isBuiltInRenderer();
   }

   public ItemOverrideList getOverrides() {
      return this.modelWhenNotCamouflaged.getOverrides();
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.modelWhenNotCamouflaged.getItemCameraTransforms();
   }

   public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
      Matrix4f matrix4f = (Matrix4f)this.modelWhenNotCamouflaged.handlePerspective(cameraTransformType).getRight();
      return Pair.of(this, matrix4f);
   }
}
