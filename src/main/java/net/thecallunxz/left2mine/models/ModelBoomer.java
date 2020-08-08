package net.thecallunxz.left2mine.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityBoomer;

public class ModelBoomer extends ModelBiped {
   public ModelRenderer Body;
   public ModelRenderer Head;
   public ModelRenderer BodyFlab1;
   public ModelRenderer BodyFlab2;
   public ModelRenderer BodyFlab3;
   public ModelRenderer LeftArm;
   public ModelRenderer LeftLeg;
   public ModelRenderer RightLeg;
   public ModelRenderer RightArm;
   public ModelRenderer Crotch;
   public ModelRenderer FaceFlab1;
   public ModelRenderer FaceFlab2;
   public ModelRenderer FaceFlab3;

   public ModelBoomer() {
      this.textureWidth = 128;
      this.textureHeight = 64;
      this.LeftLeg = new ModelRenderer(this, 0, 48);
      this.LeftLeg.setRotationPoint(2.0F, 0.0F, 0.0F);
      this.LeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 4, 0.0F);
      this.BodyFlab3 = new ModelRenderer(this, 100, 39);
      this.BodyFlab3.setRotationPoint(-4.0F, -1.0F, -6.0F);
      this.BodyFlab3.addBox(0.0F, 0.0F, 0.0F, 8, 1, 1, 0.0F);
      this.BodyFlab2 = new ModelRenderer(this, 100, 46);
      this.BodyFlab2.setRotationPoint(-4.0F, -8.0F, -6.0F);
      this.BodyFlab2.addBox(0.0F, 0.0F, 0.0F, 8, 1, 1, 0.0F);
      this.FaceFlab1 = new ModelRenderer(this, 0, 22);
      this.FaceFlab1.setRotationPoint(-5.0F, -7.0F, -5.0F);
      this.FaceFlab1.addBox(0.0F, 0.0F, 0.0F, 4, 3, 4, 0.0F);
      this.LeftArm = new ModelRenderer(this, 16, 46);
      this.LeftArm.setRotationPoint(7.0F, -10.0F, 0.0F);
      this.LeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
      this.RightArm = new ModelRenderer(this, 16, 46);
      this.RightArm.mirror = true;
      this.RightArm.setRotationPoint(-7.0F, -10.0F, 0.0F);
      this.RightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
      this.RightLeg = new ModelRenderer(this, 0, 48);
      this.RightLeg.setRotationPoint(-2.0F, 0.0F, 0.0F);
      this.RightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 4, 0.0F);
      this.Body = new ModelRenderer(this, 0, 0);
      this.Body.setRotationPoint(0.0F, 14.0F, 0.0F);
      this.Body.addBox(-6.0F, -12.0F, -5.0F, 12, 12, 9, 0.0F);
      this.Head = new ModelRenderer(this, 44, 0);
      this.Head.setRotationPoint(0.0F, -12.0F, 0.0F);
      this.Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
      this.BodyFlab1 = new ModelRenderer(this, 0, 41);
      this.BodyFlab1.setRotationPoint(-5.0F, -7.0F, -6.0F);
      this.BodyFlab1.addBox(0.0F, 0.0F, 0.0F, 10, 6, 1, 0.0F);
      this.FaceFlab2 = new ModelRenderer(this, 32, 22);
      this.FaceFlab2.setRotationPoint(1.5F, -3.5F, -4.5F);
      this.FaceFlab2.addBox(0.0F, 0.0F, 0.0F, 3, 3, 3, 0.0F);
      this.FaceFlab3 = new ModelRenderer(this, 48, 22);
      this.FaceFlab3.setRotationPoint(-4.5F, -4.3F, -4.5F);
      this.FaceFlab3.addBox(0.0F, 0.0F, 0.0F, 3, 3, 3, 0.0F);
      this.Crotch = new ModelRenderer(this, 46, 38);
      this.Crotch.setRotationPoint(-5.0F, 0.0F, -4.0F);
      this.Crotch.addBox(0.0F, 0.0F, 0.0F, 10, 1, 7, 0.0F);
      this.Body.addChild(this.BodyFlab3);
      this.Body.addChild(this.BodyFlab2);
      this.Head.addChild(this.FaceFlab1);
      this.Body.addChild(this.BodyFlab1);
      this.Head.addChild(this.FaceFlab2);
      this.Head.addChild(this.FaceFlab3);
      this.Body.addChild(this.Crotch);
      this.bipedBody = this.Body;
      this.bipedHead = this.Head;
      this.bipedLeftArm = this.LeftArm;
      this.bipedRightArm = this.RightArm;
      this.bipedLeftLeg = this.LeftLeg;
      this.bipedRightLeg = this.RightLeg;
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0D, 0.1D, 0.0D);
      EntityBoomer boomer = (EntityBoomer)entityIn;
      ModelRenderer var10000;
      if (boomer.getPukeTicks() < 0) {
         this.bipedBody.rotationPointY = 12.0F;
         this.bipedLeftArm.rotationPointY = 2.0F;
         this.bipedRightArm.rotationPointY = 2.0F;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleZ -= 0.1F;
         var10000 = this.bipedRightArm;
         var10000.rotateAngleZ += 0.1F;
         this.bipedBody.rotateAngleX = 0.25F;
         this.bipedHead.rotationPointZ = -5.0F;
         this.bipedHead.rotationPointY = 1.0F;
         this.bipedLeftArm.rotateAngleX = 1.0F;
         this.bipedRightArm.rotateAngleX = 1.0F;
      } else {
         this.bipedBody.rotationPointY = 12.0F;
         this.bipedHead.rotationPointZ = 0.0F;
         this.bipedHead.rotationPointY = 0.0F;
         this.bipedLeftArm.rotationPointY = 2.0F;
         this.bipedRightArm.rotationPointY = 2.0F;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleZ -= 0.1F;
         var10000 = this.bipedRightArm;
         var10000.rotateAngleZ += 0.1F;
      }

      this.bipedHead.render(scale);
      this.bipedBody.render(scale);
      this.bipedRightArm.render(scale);
      this.bipedLeftArm.render(scale);
      this.bipedRightLeg.render(scale);
      this.bipedLeftLeg.render(scale);
      GlStateManager.popMatrix();
   }
}
