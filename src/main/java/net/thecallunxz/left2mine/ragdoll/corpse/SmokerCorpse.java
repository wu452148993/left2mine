package net.thecallunxz.left2mine.ragdoll.corpse;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.thecallunxz.left2mine.models.ModelSmoker;
import net.thecallunxz.left2mine.ragdoll.physics.RagdollBase;
import net.thecallunxz.left2mine.ragdoll.physics.RagdollLimb;

public class SmokerCorpse extends CustomCorpse {
   public SmokerCorpse() {
      super(ModelSmoker.class);
   }

   public RagdollBase getSkeleton(Entity argEntity, ModelBase argModel) {
      float s = 0.0625F;
      RagdollLimb nodeHead = new RagdollLimb(1.5F, 0.0F, 0.0F + 32.0F * s, 0.0F);
      RagdollLimb nodeNeck = new RagdollLimb(1.0F, 0.0F, 0.0F + 24.0F * s, -0.1F);
      RagdollLimb nodeRightShoulder = new RagdollLimb(1.0F, 0.0F - 5.0F * s, 0.0F + 24.0F * s, 0.0F);
      RagdollLimb nodeLeftShoulder = new RagdollLimb(1.0F, 0.0F + 5.0F * s, 0.0F + 24.0F * s, 0.0F);
      RagdollLimb nodeRightArm = new RagdollLimb(1.0F, 0.0F - 5.0F * s - 0.1F, 0.0F + 12.0F * s, 0.0F);
      RagdollLimb nodeLeftArm = new RagdollLimb(1.0F, 0.0F + 5.0F * s + 0.1F, 0.0F + 12.0F * s, 0.0F);
      RagdollLimb nodePivot = new RagdollLimb(3.0F, 0.0F, 0.0F + 12.0F * s, 0.0F);
      RagdollLimb nodeRightHip = new RagdollLimb(1.0F, 0.0F - 1.9F * s, 0.0F + 12.0F * s, 0.0F);
      RagdollLimb nodeLeftHip = new RagdollLimb(1.0F, 0.0F + 1.9F * s, 0.0F + 12.0F * s, 0.0F);
      RagdollLimb nodeRightLeg = new RagdollLimb(1.0F, 0.0F - 2.0F * s, 0.1F, 0.0F);
      RagdollLimb nodeLeftLeg = new RagdollLimb(1.0F, 0.0F + 2.0F * s, 0.1F, 0.0F);
      RagdollLimb nodeTongue = new RagdollLimb(0.5F, 0.0F, 0.0F + 25.0F * s, 0.0F);
      RagdollLimb nodeHeadBoil1 = new RagdollLimb(0.5F, 0.0F, 0.0F + 25.0F * s, 0.0F);
      RagdollLimb nodeHeadBoil2 = new RagdollLimb(0.5F, 0.0F, 0.0F + 25.0F * s, 0.0F);
      RagdollLimb nodeHeadBoil3 = new RagdollLimb(0.5F, 0.0F, 0.0F + 25.0F * s, 0.0F);
      RagdollLimb nodeHeadBoil4 = new RagdollLimb(0.5F, 0.0F, 0.0F + 25.0F * s, 0.0F);
      RagdollBase skeleton = new RagdollBase();
      nodeRightHip.attachTo(nodeRightLeg, 12.0F * s, 0.5F).addBox(4);
      nodeLeftHip.attachTo(nodeLeftLeg, 12.0F * s, 0.5F).addBox(5);
      nodeRightHip.attachTo(nodePivot, 1.9F * s, 0.5F).setVisible(false);
      nodeLeftHip.attachTo(nodePivot, 1.9F * s, 0.5F).setVisible(false);
      nodePivot.attachTo(nodeNeck, 12.0F * s, 1.0F).addBox(1).alignToSideNode(nodeRightHip).setInvert(true).setOffset(0.0F, 0.0F, -0.01F);
      nodeNeck.attachTo(nodeRightShoulder, 5.0F * s, 1.0F).setVisible(false);
      nodeNeck.attachTo(nodeLeftShoulder, 5.0F * s, 1.0F).setVisible(false);
      nodeRightArm.attachTo(nodeRightShoulder, 12.0F * s, 1.0F).addBox(2).setInvert(true).setOffset(0.1F, 0.0F, 0.01F);
      nodeLeftArm.attachTo(nodeLeftShoulder, 12.0F * s, 1.0F).addBox(3).setInvert(true).setOffset(0.0F, 0.0F, 0.01F);
      nodeHead.attachTo(nodeNeck, 8.0F * s, 1.0F).addBox(0).setUpsidedown(false).setInvert(true).setRotation(0.0F, 0.0F, 180.0F);
      nodeTongue.attachTo(nodeNeck, 8.0F * s, 1.0F).addBox(6).setOffset(0.0F, 0.425F, -0.25F);
      nodeHeadBoil1.attachTo(nodeNeck, 8.0F * s, 1.0F).addBox(7).setOffset(0.1F, 0.1F, -0.3F);
      nodeHeadBoil2.attachTo(nodeNeck, 8.0F * s, 1.0F).addBox(8).setOffset(0.02F, 0.2F, -0.27F);
      nodeHeadBoil3.attachTo(nodeNeck, 8.0F * s, 1.0F).addBox(9).setOffset(0.05F, 0.37F, -0.3F);
      nodeHeadBoil4.attachTo(nodeNeck, 8.0F * s, 1.0F).addBox(10).setOffset(0.1F, 0.24F, -0.3F);
      nodeRightHip.attachTo(nodeLeftHip, 3.8F * s, 1.0F).setVisible(false);
      nodeNeck.attachTo(nodeRightHip, (float)Math.sqrt(147.61000061035156D) * s, 1.0F).setVisible(false);
      nodeNeck.attachTo(nodeLeftHip, (float)Math.sqrt(147.61000061035156D) * s, 1.0F).setVisible(false);
      nodeRightShoulder.attachTo(nodeLeftShoulder, 10.0F * s, 1.0F).setVisible(false);
      nodeRightShoulder.attachTo(nodeRightHip, (float)Math.sqrt(153.61000061035156D) * s, 1.0F).setVisible(false);
      nodeLeftShoulder.attachTo(nodeLeftHip, (float)Math.sqrt(153.61000061035156D) * s, 1.0F).setVisible(false);
      nodeRightShoulder.attachTo(nodeRightLeg, 20.0F * s, 0.01F).setVisible(false);
      nodeLeftShoulder.attachTo(nodeLeftLeg, 20.0F * s, 0.01F).setVisible(false);
      nodeHead.attachTo(nodePivot, 20.0F * s, 0.5F).setVisible(false);
      nodeTongue.attachTo(nodePivot, 20.0F * s, 1.0F).setOffset(0.0F, 0.425F, -0.25F).setVisible(false);
      nodeTongue.attachTo(nodeHead, 10.0F * s, 1.0F).setSticky(true).setVisible(false);
      nodeHeadBoil1.attachTo(nodePivot, 20.0F * s, 1.0F).setOffset(0.1F, 0.1F, -0.3F).setVisible(false);
      nodeHeadBoil1.attachTo(nodeHead, 10.0F * s, 1.0F).setSticky(true).setVisible(false);
      nodeHeadBoil2.attachTo(nodePivot, 20.0F * s, 1.0F).setOffset(0.02F, 0.2F, -0.27F).setVisible(false);
      nodeHeadBoil2.attachTo(nodeHead, 10.0F * s, 1.0F).setSticky(true).setVisible(false);
      nodeHeadBoil3.attachTo(nodePivot, 20.0F * s, 1.0F).setOffset(0.05F, 0.37F, -0.3F).setVisible(false);
      nodeHeadBoil3.attachTo(nodeHead, 10.0F * s, 1.0F).setSticky(true).setVisible(false);
      nodeHeadBoil4.attachTo(nodePivot, 20.0F * s, 1.0F).setOffset(0.1F, 0.24F, -0.3F).setVisible(false);
      nodeHeadBoil4.attachTo(nodeHead, 10.0F * s, 1.0F).setSticky(true).setVisible(false);
      skeleton.nodeList.add(nodeHead);
      skeleton.nodeList.add(nodeNeck);
      skeleton.nodeList.add(nodeRightShoulder);
      skeleton.nodeList.add(nodeLeftShoulder);
      skeleton.nodeList.add(nodeRightArm);
      skeleton.nodeList.add(nodeLeftArm);
      skeleton.nodeList.add(nodeTongue);
      skeleton.nodeList.add(nodeHeadBoil1);
      skeleton.nodeList.add(nodeHeadBoil2);
      skeleton.nodeList.add(nodeHeadBoil3);
      skeleton.nodeList.add(nodeHeadBoil4);
      skeleton.nodeList.add(nodePivot);
      skeleton.nodeList.add(nodeRightHip);
      skeleton.nodeList.add(nodeLeftHip);
      skeleton.nodeList.add(nodeRightLeg);
      skeleton.nodeList.add(nodeLeftLeg);
      return skeleton;
   }
}
