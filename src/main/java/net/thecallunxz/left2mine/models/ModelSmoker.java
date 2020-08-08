package net.thecallunxz.left2mine.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;

@SideOnly(Side.CLIENT)
public class ModelSmoker extends ModelBase {
   public ModelRenderer bipedHead;
   public ModelRenderer bipedBody;
   public ModelRenderer bipedRightArm;
   public ModelRenderer bipedLeftArm;
   public ModelRenderer bipedRightLeg;
   public ModelRenderer bipedLeftLeg;
   public ModelRenderer smokerTongue;
   public ModelRenderer headBoil1;
   public ModelRenderer headBoil2;
   public ModelRenderer headBoil3;
   public ModelRenderer headBoil4;
   public ModelSmoker.ArmPose leftArmPose;
   public ModelSmoker.ArmPose rightArmPose;
   public boolean isSneak;

   public ModelSmoker() {
      this(0.0F);
   }

   public ModelSmoker(float modelSize) {
      this(modelSize, 0.0F, 128, 64);
   }

   public ModelSmoker(float modelSize, float p_i1149_2_, int textureWidthIn, int textureHeightIn) {
      this.leftArmPose = ModelSmoker.ArmPose.EMPTY;
      this.rightArmPose = ModelSmoker.ArmPose.EMPTY;
      this.textureWidth = textureWidthIn;
      this.textureHeight = textureHeightIn;
      this.bipedHead = new ModelRenderer(this, 0, 0);
      this.bipedHead.setRotationPoint(0.0F, -2.0F, 0.0F);
      this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
      this.bipedBody = new ModelRenderer(this, 0, 48);
      this.bipedBody.setRotationPoint(0.0F, -2.0F, 0.0F);
      this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
      this.bipedRightArm = new ModelRenderer(this, 27, 47);
      this.bipedRightArm.setRotationPoint(-4.0F, 0.0F, 0.5F);
      this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 3, 12, 3, 0.0F);
      this.bipedLeftArm = new ModelRenderer(this, 27, 47);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.setRotationPoint(5.0F, 0.0F, 0.5F);
      this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 3, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 0, 27);
      this.bipedRightLeg.setRotationPoint(-1.9F, 10.0F, 0.5F);
      this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 3, 14, 3, 0.0F);
      this.bipedLeftLeg = new ModelRenderer(this, 0, 27);
      this.bipedLeftLeg.mirror = true;
      this.bipedLeftLeg.setRotationPoint(2.9F, 10.0F, 0.5F);
      this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 3, 14, 3, 0.0F);
      this.smokerTongue = new ModelRenderer(this, 70, 0);
      this.smokerTongue.setRotationPoint(0.0F, -1.5F, -4.0F);
      this.smokerTongue.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1, 0.0F);
      this.headBoil1 = new ModelRenderer(this, 87, 0);
      this.headBoil1.setRotationPoint(1.5F, -6.9F, -4.5F);
      this.headBoil1.addBox(0.0F, 0.0F, 0.0F, 3, 2, 6, 0.0F);
      this.headBoil2 = new ModelRenderer(this, 38, 29);
      this.headBoil2.setRotationPoint(0.3F, -5.3F, -4.2F);
      this.headBoil2.addBox(0.0F, 0.0F, 0.0F, 4, 3, 6, 0.0F);
      this.headBoil3 = new ModelRenderer(this, 42, 0);
      this.headBoil3.setRotationPoint(0.8F, -2.3F, -4.5F);
      this.headBoil3.addBox(0.0F, 0.0F, 0.0F, 3, 3, 6, 0.0F);
      this.headBoil4 = new ModelRenderer(this, 43, 12);
      this.headBoil4.setRotationPoint(1.5F, -4.6F, -4.5F);
      this.headBoil4.addBox(0.0F, 0.0F, 0.0F, 3, 2, 6, 0.0F);
      this.bipedHead.addChild(this.smokerTongue);
      this.bipedHead.addChild(this.headBoil1);
      this.bipedHead.addChild(this.headBoil2);
      this.bipedHead.addChild(this.headBoil3);
      this.bipedHead.addChild(this.headBoil4);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      GlStateManager.pushMatrix();
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.scale(0.75F, 0.75F, 0.75F);
         GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
         this.bipedHead.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
         GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
         this.bipedBody.render(scale);
         this.bipedRightArm.render(scale);
         this.bipedLeftArm.render(scale);
         this.bipedRightLeg.render(scale);
         this.bipedLeftLeg.render(scale);
      } else {
         if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         this.bipedHead.rotationPointY = -2.0F;
         ModelRenderer var10000 = this.bipedLeftArm;
         var10000.rotationPointZ += 0.5F;
         var10000 = this.bipedRightArm;
         var10000.rotationPointZ += 0.5F;
         ++this.bipedRightArm.rotationPointX;
         var10000 = this.bipedLeftLeg;
         var10000.rotationPointY -= 2.0F;
         var10000 = this.bipedRightLeg;
         var10000.rotationPointY -= 2.0F;
         if (entityIn instanceof EntitySmoker) {
            EntitySmoker smoker = (EntitySmoker)entityIn;
            if (smoker.isSmoked()) {
               this.smokerTongue.showModel = false;
            } else {
               this.smokerTongue.showModel = true;
            }
         }

         this.bipedHead.render(scale);
         this.bipedBody.render(scale);
         this.bipedRightArm.render(scale);
         this.bipedLeftArm.render(scale);
         this.bipedRightLeg.render(scale);
         this.bipedLeftLeg.render(scale);
      }

      GlStateManager.popMatrix();
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      boolean flag = entityIn instanceof EntityLivingBase && ((EntityLivingBase)entityIn).getTicksElytraFlying() > 4;
      this.bipedHead.rotateAngleY = netHeadYaw * 0.017453292F;
      if (flag) {
         this.bipedHead.rotateAngleX = -0.7853982F;
      } else {
         this.bipedHead.rotateAngleX = headPitch * 0.017453292F;
      }

      this.bipedBody.rotateAngleY = 0.0F;
      this.bipedRightArm.rotationPointZ = 0.0F;
      this.bipedRightArm.rotationPointX = -5.0F;
      this.bipedLeftArm.rotationPointZ = 0.0F;
      this.bipedLeftArm.rotationPointX = 5.0F;
      float f = 1.0F;
      if (flag) {
         f = (float)(entityIn.motionX * entityIn.motionX + entityIn.motionY * entityIn.motionY + entityIn.motionZ * entityIn.motionZ);
         f /= 0.2F;
         f = f * f * f;
      }

      if (f < 1.0F) {
         f = 1.0F;
      }

      this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 2.0F * limbSwingAmount * 0.5F / f;
      this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
      this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount / f;
      this.bipedRightLeg.rotateAngleY = 0.0F;
      this.bipedLeftLeg.rotateAngleY = 0.0F;
      this.bipedRightLeg.rotateAngleZ = 0.0F;
      this.bipedLeftLeg.rotateAngleZ = 0.0F;
      ModelRenderer var10000;
      if (this.isRiding) {
         var10000 = this.bipedRightArm;
         var10000.rotateAngleX += -0.62831855F;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleX += -0.62831855F;
         this.bipedRightLeg.rotateAngleX = -1.4137167F;
         this.bipedRightLeg.rotateAngleY = 0.31415927F;
         this.bipedRightLeg.rotateAngleZ = 0.07853982F;
         this.bipedLeftLeg.rotateAngleX = -1.4137167F;
         this.bipedLeftLeg.rotateAngleY = -0.31415927F;
         this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
      }

      this.bipedRightArm.rotateAngleY = 0.0F;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      switch(this.leftArmPose) {
      case EMPTY:
         this.bipedLeftArm.rotateAngleY = 0.0F;
         break;
      case BLOCK:
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
         this.bipedLeftArm.rotateAngleY = 0.5235988F;
         break;
      case ITEM:
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.31415927F;
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      switch(this.rightArmPose) {
      case EMPTY:
         this.bipedRightArm.rotateAngleY = 0.0F;
         break;
      case BLOCK:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
         this.bipedRightArm.rotateAngleY = -0.5235988F;
         break;
      case ITEM:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.31415927F;
         this.bipedRightArm.rotateAngleY = 0.0F;
      }

      if (this.swingProgress > 0.0F) {
         EnumHandSide enumhandside = this.getMainHand(entityIn);
         ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
         float f1 = this.swingProgress;
         this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * 6.2831855F) * 0.2F;
         if (enumhandside == EnumHandSide.LEFT) {
            var10000 = this.bipedBody;
            var10000.rotateAngleY *= -1.0F;
         }

         this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         var10000 = this.bipedRightArm;
         var10000.rotateAngleY += this.bipedBody.rotateAngleY;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleY += this.bipedBody.rotateAngleY;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleX += this.bipedBody.rotateAngleY;
         f1 = 1.0F - this.swingProgress;
         f1 *= f1;
         f1 *= f1;
         f1 = 1.0F - f1;
         float f2 = MathHelper.sin(f1 * 3.1415927F);
         float f3 = MathHelper.sin(this.swingProgress * 3.1415927F) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
         modelrenderer.rotateAngleX = (float)((double)modelrenderer.rotateAngleX - ((double)f2 * 1.2D + (double)f3));
         modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
         modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * 3.1415927F) * -0.4F;
      }

      if (this.isSneak) {
         this.bipedBody.rotateAngleX = 0.5F;
         var10000 = this.bipedRightArm;
         var10000.rotateAngleX += 0.4F;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleX += 0.4F;
         this.bipedRightLeg.rotationPointZ = 4.0F;
         this.bipedLeftLeg.rotationPointZ = 4.0F;
         this.bipedRightLeg.rotationPointY = 9.0F;
         this.bipedLeftLeg.rotationPointY = 9.0F;
         this.bipedHead.rotationPointY = 1.0F;
      } else {
         this.bipedBody.rotateAngleX = 0.0F;
         this.bipedRightLeg.rotationPointZ = 0.1F;
         this.bipedLeftLeg.rotationPointZ = 0.1F;
         this.bipedRightLeg.rotationPointY = 12.0F;
         this.bipedLeftLeg.rotationPointY = 12.0F;
         this.bipedHead.rotationPointY = 0.0F;
      }

      var10000 = this.bipedRightArm;
      var10000.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
      var10000 = this.bipedRightArm;
      var10000.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      if (this.rightArmPose == ModelSmoker.ArmPose.BOW_AND_ARROW) {
         this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
         this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
      } else if (this.leftArmPose == ModelSmoker.ArmPose.BOW_AND_ARROW) {
         this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY - 0.4F;
         this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
         this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
      }

   }

   public void setModelAttributes(ModelBase model) {
      super.setModelAttributes(model);
      if (model instanceof ModelSmoker) {
         ModelSmoker modelbiped = (ModelSmoker)model;
         this.leftArmPose = modelbiped.leftArmPose;
         this.rightArmPose = modelbiped.rightArmPose;
         this.isSneak = modelbiped.isSneak;
      }

   }

   public void setVisible(boolean visible) {
      this.bipedHead.showModel = visible;
      this.bipedBody.showModel = visible;
      this.bipedRightArm.showModel = visible;
      this.bipedLeftArm.showModel = visible;
      this.bipedRightLeg.showModel = visible;
      this.bipedLeftLeg.showModel = visible;
   }

   public void postRenderArm(float scale, EnumHandSide side) {
      this.getArmForSide(side).postRender(scale);
   }

   protected ModelRenderer getArmForSide(EnumHandSide side) {
      return side == EnumHandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
   }

   protected EnumHandSide getMainHand(Entity entityIn) {
      if (entityIn instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)entityIn;
         EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
         return entitylivingbase.swingingHand == EnumHand.MAIN_HAND ? enumhandside : enumhandside.opposite();
      } else {
         return EnumHandSide.RIGHT;
      }
   }

   @SideOnly(Side.CLIENT)
   public static enum ArmPose {
      EMPTY,
      ITEM,
      BLOCK,
      BOW_AND_ARROW;
   }
}
