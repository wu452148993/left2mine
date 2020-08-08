package net.thecallunxz.left2mine.ragdoll.corpse;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;
import net.thecallunxz.left2mine.models.ModelHunter;
import net.thecallunxz.left2mine.ragdoll.physics.RagdollBase;
import net.thecallunxz.left2mine.ragdoll.physics.RagdollLimb;

public class HunterCorpse extends CustomCorpse {
   public HunterCorpse() {
      super(ModelHunter.class);
   }

   public RagdollBase getSkeleton(Entity argEntity, ModelBase argModel) {
      float s = 0.0625F;
      RagdollLimb nodeHead;
      RagdollLimb nodeNeck;
      RagdollLimb nodeRightShoulder;
      RagdollLimb nodeLeftShoulder;
      RagdollLimb nodeRightArm;
      RagdollLimb nodeLeftArm;
      RagdollLimb nodePivot;
      RagdollLimb nodeRightHip;
      RagdollLimb nodeLeftHip;
      RagdollLimb nodeRightLeg;
      RagdollLimb nodeLeftLeg;
      RagdollBase skeleton;
      if (((EntityHunter)argEntity).isPouncing()) {
         nodeHead = new RagdollLimb(1.5F, 0.0F, 0.0F + 32.0F * s, 0.0F);
         nodeNeck = new RagdollLimb(1.0F, 0.0F, 0.0F + 24.0F * s, -0.1F);
         nodeRightShoulder = new RagdollLimb(1.0F, 0.0F - 5.0F * s, 0.0F + 24.0F * s, 0.0F);
         nodeLeftShoulder = new RagdollLimb(1.0F, 0.0F + 5.0F * s, 0.0F + 24.0F * s, 0.0F);
         nodeRightArm = new RagdollLimb(1.0F, 0.0F - 5.0F * s - 0.1F, 0.0F + 24.0F * s, 12.0F * s);
         nodeLeftArm = new RagdollLimb(1.0F, 0.0F + 5.0F * s + 0.1F, 0.0F + 24.0F * s, 12.0F * s);
         nodePivot = new RagdollLimb(3.0F, 0.0F, 0.0F + 24.0F * s, 0.0F - 12.0F * s);
         nodeRightHip = new RagdollLimb(1.0F, 0.0F - 1.9F * s, 0.0F + 24.0F * s, -12.0F * s);
         nodeLeftHip = new RagdollLimb(1.0F, 0.0F + 1.9F * s, 0.0F + 24.0F * s, -12.0F * s);
         nodeRightLeg = new RagdollLimb(1.0F, 0.0F - 2.0F * s, 0.0F + 24.0F * s, 0.0F - 24.0F * s);
         nodeLeftLeg = new RagdollLimb(1.0F, 0.0F + 2.0F * s, 0.0F + 24.0F * s, 0.0F - 24.0F * s);
         skeleton = new RagdollBase();
         nodeRightHip.attachTo(nodeRightLeg, 12.0F * s, 0.5F).addBox(5);
         nodeLeftHip.attachTo(nodeLeftLeg, 12.0F * s, 0.5F).addBox(6);
         nodeRightHip.attachTo(nodePivot, 1.9F * s, 0.5F).setVisible(false);
         nodeLeftHip.attachTo(nodePivot, 1.9F * s, 0.5F).setVisible(false);
         nodePivot.attachTo(nodeNeck, 12.0F * s, 1.0F).addBox(2).alignToSideNode(nodeRightHip).setInvert(true).setOffset(0.0F, 0.0F, -0.01F);
         nodeNeck.attachTo(nodeRightShoulder, 5.0F * s, 1.0F).setVisible(false);
         nodeNeck.attachTo(nodeLeftShoulder, 5.0F * s, 1.0F).setVisible(false);
         nodeRightArm.attachTo(nodeRightShoulder, 12.0F * s, 1.0F).addBox(3).setInvert(true).setOffset(0.0F, 0.0F, 0.01F);
         nodeLeftArm.attachTo(nodeLeftShoulder, 12.0F * s, 1.0F).addBox(4).setInvert(true).setOffset(0.0F, 0.0F, 0.01F);
         nodeHead.attachTo(nodeNeck, 8.0F * s, 1.0F).addBox(0).setUpsidedown(false).setInvert(true).setRotation(180.0F, 0.0F, 0.0F);
         nodeRightHip.attachTo(nodeLeftHip, 3.8F * s, 1.0F).setVisible(false);
         nodeNeck.attachTo(nodeRightHip, (float)Math.sqrt(147.61000061035156D) * s, 1.0F).setVisible(false);
         nodeNeck.attachTo(nodeLeftHip, (float)Math.sqrt(147.61000061035156D) * s, 1.0F).setVisible(false);
         nodeRightShoulder.attachTo(nodeLeftShoulder, 10.0F * s, 1.0F).setVisible(false);
         nodeRightShoulder.attachTo(nodeRightHip, (float)Math.sqrt(153.61000061035156D) * s, 1.0F).setVisible(false);
         nodeLeftShoulder.attachTo(nodeLeftHip, (float)Math.sqrt(153.61000061035156D) * s, 1.0F).setVisible(false);
         nodeRightShoulder.attachTo(nodeRightLeg, 20.0F * s, 0.01F).setVisible(false);
         nodeLeftShoulder.attachTo(nodeLeftLeg, 20.0F * s, 0.01F).setVisible(false);
         nodeHead.attachTo(nodePivot, 20.0F * s, 0.1F).setVisible(false);
         skeleton.nodeList.add(nodeHead);
         skeleton.nodeList.add(nodeNeck);
         skeleton.nodeList.add(nodeRightShoulder);
         skeleton.nodeList.add(nodeLeftShoulder);
         skeleton.nodeList.add(nodeRightArm);
         skeleton.nodeList.add(nodeLeftArm);
         skeleton.nodeList.add(nodePivot);
         skeleton.nodeList.add(nodeRightHip);
         skeleton.nodeList.add(nodeLeftHip);
         skeleton.nodeList.add(nodeRightLeg);
         skeleton.nodeList.add(nodeLeftLeg);
         return skeleton;
      } else {
         nodeHead = new RagdollLimb(1.5F, 0.0F, 0.0F + 32.0F * s, 0.0F);
         nodeNeck = new RagdollLimb(1.0F, 0.0F, 0.0F + 24.0F * s, -0.1F);
         nodeRightShoulder = new RagdollLimb(1.0F, 0.0F - 5.0F * s, 0.0F + 24.0F * s, 0.0F);
         nodeLeftShoulder = new RagdollLimb(1.0F, 0.0F + 5.0F * s, 0.0F + 24.0F * s, 0.0F);
         nodeRightArm = new RagdollLimb(1.0F, 0.0F - 5.0F * s - 0.1F, 0.0F + 12.0F * s, 0.0F);
         nodeLeftArm = new RagdollLimb(1.0F, 0.0F + 5.0F * s + 0.1F, 0.0F + 12.0F * s, 0.0F);
         nodePivot = new RagdollLimb(3.0F, 0.0F, 0.0F + 12.0F * s, 0.0F);
         nodeRightHip = new RagdollLimb(1.0F, 0.0F - 1.9F * s, 0.0F + 12.0F * s, 0.0F);
         nodeLeftHip = new RagdollLimb(1.0F, 0.0F + 1.9F * s, 0.0F + 12.0F * s, 0.0F);
         nodeRightLeg = new RagdollLimb(1.0F, 0.0F - 2.0F * s, 0.1F, 0.0F);
         nodeLeftLeg = new RagdollLimb(1.0F, 0.0F + 2.0F * s, 0.1F, 0.0F);
         skeleton = new RagdollBase();
         nodeRightHip.attachTo(nodeRightLeg, 12.0F * s, 0.5F).addBox(5);
         nodeLeftHip.attachTo(nodeLeftLeg, 12.0F * s, 0.5F).addBox(6);
         nodeRightHip.attachTo(nodePivot, 1.9F * s, 0.5F).setVisible(false);
         nodeLeftHip.attachTo(nodePivot, 1.9F * s, 0.5F).setVisible(false);
         nodePivot.attachTo(nodeNeck, 12.0F * s, 1.0F).addBox(2).alignToSideNode(nodeRightHip).setInvert(true).setOffset(0.0F, 0.0F, -0.01F);
         nodeNeck.attachTo(nodeRightShoulder, 5.0F * s, 1.0F).setVisible(false);
         nodeNeck.attachTo(nodeLeftShoulder, 5.0F * s, 1.0F).setVisible(false);
         nodeRightArm.attachTo(nodeRightShoulder, 12.0F * s, 1.0F).addBox(3).setInvert(true).setOffset(0.0F, 0.0F, 0.01F);
         nodeLeftArm.attachTo(nodeLeftShoulder, 12.0F * s, 1.0F).addBox(4).setInvert(true).setOffset(0.0F, 0.0F, 0.01F);
         nodeHead.attachTo(nodeNeck, 8.0F * s, 1.0F).addBox(0).setUpsidedown(false).setInvert(true).setRotation(0.0F, 0.0F, 180.0F);
         nodeRightHip.attachTo(nodeLeftHip, 3.8F * s, 1.0F).setVisible(false);
         nodeNeck.attachTo(nodeRightHip, (float)Math.sqrt(147.61000061035156D) * s, 1.0F).setVisible(false);
         nodeNeck.attachTo(nodeLeftHip, (float)Math.sqrt(147.61000061035156D) * s, 1.0F).setVisible(false);
         nodeRightShoulder.attachTo(nodeLeftShoulder, 10.0F * s, 1.0F).setVisible(false);
         nodeRightShoulder.attachTo(nodeRightHip, (float)Math.sqrt(153.61000061035156D) * s, 1.0F).setVisible(false);
         nodeLeftShoulder.attachTo(nodeLeftHip, (float)Math.sqrt(153.61000061035156D) * s, 1.0F).setVisible(false);
         nodeRightShoulder.attachTo(nodeRightLeg, 20.0F * s, 0.01F).setVisible(false);
         nodeLeftShoulder.attachTo(nodeLeftLeg, 20.0F * s, 0.01F).setVisible(false);
         nodeHead.attachTo(nodePivot, 20.0F * s, 0.1F).setVisible(false);
         skeleton.nodeList.add(nodeHead);
         skeleton.nodeList.add(nodeNeck);
         skeleton.nodeList.add(nodeRightShoulder);
         skeleton.nodeList.add(nodeLeftShoulder);
         skeleton.nodeList.add(nodeRightArm);
         skeleton.nodeList.add(nodeLeftArm);
         skeleton.nodeList.add(nodePivot);
         skeleton.nodeList.add(nodeRightHip);
         skeleton.nodeList.add(nodeLeftHip);
         skeleton.nodeList.add(nodeRightLeg);
         skeleton.nodeList.add(nodeLeftLeg);
         return skeleton;
      }
   }
}
