package net.thecallunxz.left2mine.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelTank extends ModelBase {
   public ModelRenderer head;
   public ModelRenderer pelvis;
   public ModelRenderer rightArm;
   public ModelRenderer leftArm;
   public ModelRenderer rightLeg;
   public ModelRenderer leftLeg;
   public ModelRenderer chest;
   public ModelRenderer body;

   public ModelTank() {
      this.textureWidth = 128;
      this.textureHeight = 128;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.setRotationPoint(0.0F, -11.0F, -4.0F);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
      this.setRotateAngle(this.head, -0.7199483F, 0.0F, 0.0F);
      this.pelvis = new ModelRenderer(this, 0, 95);
      this.pelvis.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.pelvis.addBox(-7.0F, -16.0F, -6.0F, 14, 16, 12, 0.0F);
      this.setRotateAngle(this.pelvis, 0.19634955F, 0.0F, 0.0F);
      this.rightArm = new ModelRenderer(this, 85, 0);
      this.rightArm.setRotationPoint(-14.0F, -8.0F, 2.0F);
      this.rightArm.addBox(-5.0F, 0.0F, -5.0F, 10, 30, 10, 0.0F);
      this.setRotateAngle(this.rightArm, -0.7853982F, 0.0F, 0.0F);
      this.leftArm = new ModelRenderer(this, 85, 0);
      this.leftArm.mirror = true;
      this.leftArm.setRotationPoint(14.0F, -8.0F, 2.0F);
      this.leftArm.addBox(-5.0F, 0.0F, -5.0F, 10, 30, 10, 0.0F);
      this.setRotateAngle(this.leftArm, -0.7853982F, 0.0F, 0.0F);
      this.rightLeg = new ModelRenderer(this, 30, 50);
      this.rightLeg.setRotationPoint(-5.0F, -2.0F, 0.0F);
      this.rightLeg.addBox(-3.0F, 0.0F, -3.0F, 6, 16, 6, 0.0F);
      this.leftLeg = new ModelRenderer(this, 30, 50);
      this.leftLeg.mirror = true;
      this.leftLeg.setRotationPoint(5.0F, -2.0F, 0.0F);
      this.leftLeg.addBox(-3.0F, 0.0F, -3.0F, 6, 16, 6, 0.0F);
      this.chest = new ModelRenderer(this, 0, 16);
      this.chest.setRotationPoint(0.0F, -12.0F, 0.0F);
      this.chest.addBox(-12.0F, -12.0F, -8.0F, 24, 12, 16, 0.0F);
      this.setRotateAngle(this.chest, 0.5235988F, 0.0F, 0.0F);
      this.body = new ModelRenderer(this, 0, 0);
      this.body.setRotationPoint(0.0F, 10.0F, 0.0F);
      this.body.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
      this.chest.addChild(this.leftArm);
      this.body.addChild(this.pelvis);
      this.body.addChild(this.leftLeg);
      this.pelvis.addChild(this.chest);
      this.chest.addChild(this.rightArm);
      this.chest.addChild(this.head);
      this.body.addChild(this.rightLeg);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 0.0F, 0.25F);
      this.body.render(scale);
      GlStateManager.popMatrix();
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      this.head.rotateAngleY = netHeadYaw * 0.007453292F;
      this.head.rotateAngleZ = -(netHeadYaw * 0.007453292F);
      this.head.rotateAngleX = headPitch * 0.017453292F - 0.5199483F;
      float f = 1.0F;
      this.rightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.3062F + 3.1415927F) * 2.0F * limbSwingAmount * 0.3F / f - 0.7853982F;
      this.leftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.3062F) * 2.0F * limbSwingAmount * 0.3F / f - 0.7853982F;
      this.rightArm.rotateAngleZ = 0.0F;
      this.leftArm.rotateAngleZ = 0.0F;
      this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.3062F) * 1.4F * limbSwingAmount * 0.8F / f;
      this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.3062F + 3.1415927F) * 1.4F * limbSwingAmount * 0.8F / f;
      this.rightLeg.rotateAngleY = 0.0F;
      this.leftLeg.rotateAngleY = 0.0F;
      this.rightLeg.rotateAngleZ = 0.0F;
      this.leftLeg.rotateAngleZ = 0.0F;
      if (this.swingProgress > 0.0F) {
         float f1 = this.swingProgress;
         this.leftArm.rotateAngleX = MathHelper.cos(f1 * 0.1062F + 3.1415927F) * 2.0F * limbSwingAmount * 1.0F / f - 0.7853982F;
         this.rightArm.rotateAngleX = MathHelper.cos(f1 * 0.1062F + 3.1415927F) * 2.0F * limbSwingAmount * 1.0F / f - 0.7853982F;
      }

   }

   public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }
}
