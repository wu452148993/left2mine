package net.thecallunxz.left2mine.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;

@SideOnly(Side.CLIENT)
public class ModelWitch extends ModelBiped {
   public ModelWitch() {
      this(0.0F, false);
   }

   public ModelWitch(float modelSize, boolean child) {
      super(modelSize, 0.0F, 64, child ? 32 : 64);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (entityIn instanceof EntityWitch) {
         EntityWitch witch = (EntityWitch)entityIn;
         this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
         GlStateManager.pushMatrix();
         if (witch.getAngerLevel() < 850 && witch.getAttackingState() == 0) {
            GlStateManager.translate(0.0F, 0.65F, 0.0F);
         }

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
            this.bipedHeadwear.render(scale);
         } else {
            if (entityIn.isSneaking()) {
               GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            this.bipedHead.render(scale);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale);
            this.bipedLeftLeg.render(scale);
            this.bipedHeadwear.render(scale);
         }

         GlStateManager.popMatrix();
      }

   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      if (entityIn instanceof EntityWitch) {
         EntityWitch witch = (EntityWitch)entityIn;
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
         if (witch.getAngerLevel() <= 500 && witch.getAttackingState() == 0) {
            var10000 = this.bipedRightArm;
            var10000.rotateAngleX += -2.41661F;
            this.bipedRightArm.rotateAngleZ = 0.5F;
            var10000 = this.bipedLeftArm;
            var10000.rotateAngleX += -2.41661F;
            this.bipedLeftArm.rotateAngleZ = -0.5F;
            this.bipedRightLeg.rotateAngleX = -1.4137167F;
            this.bipedRightLeg.rotateAngleY = 0.31415927F;
            this.bipedRightLeg.rotateAngleZ = 0.07853982F;
            this.bipedLeftLeg.rotateAngleX = -1.4137167F;
            this.bipedLeftLeg.rotateAngleY = -0.31415927F;
            this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
         } else if (witch.getAngerLevel() < 850 && witch.getAttackingState() == 0) {
            var10000 = this.bipedRightArm;
            var10000.rotateAngleX += -0.3926991F;
            var10000 = this.bipedLeftArm;
            var10000.rotateAngleX += -0.3926991F;
            this.bipedRightLeg.rotateAngleX = -1.4137167F;
            this.bipedRightLeg.rotateAngleY = 0.31415927F;
            this.bipedRightLeg.rotateAngleZ = 0.07853982F;
            this.bipedLeftLeg.rotateAngleX = -1.4137167F;
            this.bipedLeftLeg.rotateAngleY = -0.31415927F;
            this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
         }

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
         if (this.rightArmPose == ArmPose.BOW_AND_ARROW) {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
            this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
         } else if (this.leftArmPose == ArmPose.BOW_AND_ARROW) {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY - 0.4F;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
         }

         copyModelAngles(this.bipedHead, this.bipedHeadwear);
      }

   }
}
