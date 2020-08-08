package net.thecallunxz.left2mine.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;

@SideOnly(Side.CLIENT)
public class ModelHunter extends ModelBiped {
   public ModelHunter() {
      this(0.0F, false);
   }

   public ModelHunter(float modelSize, boolean child) {
      super(modelSize, 0.0F, 64, child ? 32 : 64);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      GlStateManager.pushMatrix();
      if (entityIn instanceof EntityHunter) {
         EntityHunter hunter = (EntityHunter)entityIn;
         if (hunter.isPouncing()) {
            GlStateManager.translate(0.0F, 1.1F, 0.0F);
            this.bipedBody.rotateAngleX = 1.5F;
            this.bipedRightLeg.rotationPointZ = 12.0F;
            this.bipedLeftLeg.rotationPointZ = 12.0F;
            this.bipedRightLeg.rotationPointY = 1.0F;
            this.bipedLeftLeg.rotationPointY = 1.0F;
            this.bipedRightLeg.rotateAngleX = 1.5F;
            this.bipedLeftLeg.rotateAngleX = 1.5F;
            this.bipedLeftArm.rotationPointZ = 3.0F;
            this.bipedRightArm.rotationPointZ = 3.0F;
            this.bipedLeftArm.rotateAngleX = -1.5F;
            this.bipedRightArm.rotateAngleX = -1.5F;
            this.bipedLeftArm.rotateAngleY = -0.2F;
            this.bipedRightArm.rotateAngleY = 0.2F;
            this.bipedHead.rotationPointY = 1.0F;
         } else {
            ModelRenderer var10000;
            if (entityIn.isSneaking()) {
               GlStateManager.translate(0.0F, 1.1F, 0.0F);
               this.bipedBody.rotateAngleX = 1.5F;
               this.bipedRightLeg.rotationPointZ = 12.0F;
               this.bipedLeftLeg.rotationPointZ = 12.0F;
               this.bipedRightLeg.rotationPointY = 1.0F;
               this.bipedLeftLeg.rotationPointY = 1.0F;
               ++this.bipedRightLeg.rotateAngleX;
               ++this.bipedLeftLeg.rotateAngleX;
               this.bipedLeftArm.rotationPointZ = 3.0F;
               this.bipedRightArm.rotationPointZ = 3.0F;
               var10000 = this.bipedLeftArm;
               var10000.rotateAngleX -= 0.9F;
               var10000 = this.bipedRightArm;
               var10000.rotateAngleX -= 0.9F;
               this.bipedHead.rotationPointY = 1.0F;
            } else if (hunter.isPinned()) {
               GlStateManager.translate(0.0F, 0.95F, 0.0F);
               this.bipedBody.rotateAngleX = 1.5F;
               this.bipedRightLeg.rotationPointZ = 12.0F;
               this.bipedLeftLeg.rotationPointZ = 12.0F;
               this.bipedRightLeg.rotationPointY = 1.0F;
               this.bipedLeftLeg.rotationPointY = 1.0F;
               var10000 = this.bipedRightLeg;
               var10000.rotateAngleX += 0.9F;
               var10000 = this.bipedLeftLeg;
               var10000.rotateAngleX += 0.8F;
               var10000 = this.bipedRightLeg;
               var10000.rotateAngleZ += 0.8F;
               var10000 = this.bipedLeftLeg;
               var10000.rotateAngleZ -= 0.9F;
               this.bipedLeftArm.rotationPointZ = 3.0F;
               this.bipedRightArm.rotationPointZ = 3.0F;
               var10000 = this.bipedLeftArm;
               var10000.rotateAngleX -= 0.9F;
               var10000 = this.bipedRightArm;
               var10000.rotateAngleX -= 0.9F;
               this.bipedHead.rotationPointY = 1.0F;
               this.bipedHead.rotateAngleX = 0.75F;
            }
         }
      }

      this.bipedHead.render(scale);
      this.bipedBody.render(scale);
      this.bipedRightArm.render(scale);
      this.bipedLeftArm.render(scale);
      this.bipedRightLeg.render(scale);
      this.bipedLeftLeg.render(scale);
      this.bipedHeadwear.render(scale);
      GlStateManager.popMatrix();
   }
}
