package net.thecallunxz.left2mine.ragdoll.corpse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.thecallunxz.left2mine.ragdoll.physics.RagdollBase;
import net.thecallunxz.left2mine.ragdoll.physics.RagdollLimb;

public class CustomCorpse {
   public static CustomCorpse[] mobCorpses = new CustomCorpse[]{new CommonCorpse(), new HunterCorpse(), new SmokerCorpse(), new TankCorpse(), new WitchCorpse()};
   public Class<? extends ModelBase> modelClass;

   public CustomCorpse(Class<? extends ModelBase> argModel) {
      this.modelClass = argModel;
   }

   public RagdollBase getSkeleton(Entity argEntity, ModelBase argModel) {
      return getDefaultSkeleton(argEntity, argModel);
   }

   public static RagdollBase getDefaultSkeleton(Entity argEntity, ModelBase argModel) {
      float s = 0.0625F;
      RagdollLimb nodeNeck = new RagdollLimb(1.0F, 0.0F, 0.0F + 24.0F * s, 0.0F);
      RagdollLimb nodeRightShoulder = new RagdollLimb(1.0F, 0.0F + 5.0F * s, 0.0F + 24.0F * s, 0.0F);
      RagdollLimb nodeLeftShoulder = new RagdollLimb(1.0F, 0.0F - 5.0F * s, 0.0F + 24.0F * s, 0.0F);
      RagdollLimb nodePivot = new RagdollLimb(1.0F, 0.0F, 0.0F + 12.0F * s, 0.0F);
      RagdollLimb nodeRightHip = new RagdollLimb(1.0F, 0.0F + 1.9F * s, 0.0F + 12.0F * s, 0.0F);
      RagdollLimb nodeLeftHip = new RagdollLimb(1.0F, 0.0F - 1.9F * s, 0.0F + 12.0F * s, 0.0F);
      RagdollLimb nodeRightLeg = new RagdollLimb(1.0F, 0.0F + 1.9F * s, 0.0F, -0.01F);
      RagdollLimb nodeLeftLeg = new RagdollLimb(1.0F, 0.0F - 1.9F * s, 0.0F, 0.01F);
      RagdollBase skeleton = new RagdollBase();
      nodeRightLeg.attachTo(nodeRightHip, 12.0F * s, 1.0F);
      nodeLeftLeg.attachTo(nodeLeftHip, 12.0F * s, 1.0F);
      nodeRightHip.attachTo(nodePivot, 1.9F * s, 1.0F).setVisible(false);
      nodeLeftHip.attachTo(nodePivot, 1.9F * s, 1.0F).setVisible(false);
      nodeNeck.attachTo(nodePivot, 12.0F * s, 1.0F);
      nodeRightShoulder.attachTo(nodeNeck, 5.0F * s, 1.0F).setVisible(false);
      nodeLeftShoulder.attachTo(nodeNeck, 5.0F * s, 1.0F).setVisible(false);
      nodeRightHip.attachTo(nodeLeftHip, 3.8F * s, 1.0F).setVisible(false);
      nodeNeck.attachTo(nodeRightHip, (float)Math.sqrt(147.61000061035156D) * s, 1.0F).setVisible(false);
      nodeNeck.attachTo(nodeLeftHip, (float)Math.sqrt(147.61000061035156D) * s, 1.0F).setVisible(false);
      nodeRightShoulder.attachTo(nodeLeftShoulder, 10.0F * s, 1.0F).setVisible(false);
      nodeRightShoulder.attachTo(nodeRightHip, 12.0F * s, 1.0F).setVisible(false);
      nodeLeftShoulder.attachTo(nodeLeftHip, 12.0F * s, 1.0F).setVisible(false);
      skeleton.nodeList.add(nodeNeck);
      skeleton.nodeList.add(nodeRightShoulder);
      skeleton.nodeList.add(nodeLeftShoulder);
      skeleton.nodeList.add(nodePivot);
      skeleton.nodeList.add(nodeRightHip);
      skeleton.nodeList.add(nodeLeftHip);
      skeleton.nodeList.add(nodeRightLeg);
      skeleton.nodeList.add(nodeLeftLeg);
      return skeleton;
   }

   public static RagdollBase getSkeletonForModel(Entity argEntity, ModelBase argModel) {
      for(int i = 0; i < mobCorpses.length; ++i) {
         if (mobCorpses[i].modelClass.isInstance(argModel)) {
            return mobCorpses[i].getSkeleton(argEntity, argModel);
         }
      }

      return getDefaultSkeleton(argEntity, argModel);
   }

   public static boolean isEntityRagdolled(EntityLivingBase argEntity) {
      if (argEntity != null && Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(argEntity) instanceof RenderLivingBase) {
         ModelBase model = ((RenderLivingBase)Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(argEntity)).getMainModel();

         for(int i = 0; i < mobCorpses.length; ++i) {
            if (mobCorpses[i].modelClass.isInstance(model)) {
               return true;
            }
         }
      }

      return false;
   }
}
