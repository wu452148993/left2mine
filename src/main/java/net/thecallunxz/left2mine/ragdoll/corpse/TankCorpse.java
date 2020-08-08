package net.thecallunxz.left2mine.ragdoll.corpse;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.thecallunxz.left2mine.models.ModelTank;
import net.thecallunxz.left2mine.ragdoll.physics.RagdollBase;
import net.thecallunxz.left2mine.ragdoll.physics.RagdollLimb;

public class TankCorpse extends CustomCorpse {
   public TankCorpse() {
      super(ModelTank.class);
   }

   public RagdollBase getSkeleton(Entity argEntity, ModelBase argModel) {
      float s = 0.0625F;
      RagdollLimb nodeHead = new RagdollLimb(1.5F, 0.0F, 0.0F + 52.0F * s, 0.0F);
      RagdollLimb nodeNeck = new RagdollLimb(1.0F, 0.0F, 0.0F + 44.0F * s, -0.2F);
      RagdollLimb nodeRightShoulder = new RagdollLimb(1.0F, 0.0F - 12.0F * s, 0.0F + 44.0F * s, 0.0F);
      RagdollLimb nodeLeftShoulder = new RagdollLimb(1.0F, 0.0F + 12.0F * s, 0.0F + 44.0F * s, 0.0F);
      RagdollLimb nodeRightArm = new RagdollLimb(2.0F, 0.0F - 12.0F * s - 0.1F, 0.0F + 14.0F * s, 0.0F);
      RagdollLimb nodeLeftArm = new RagdollLimb(2.0F, 0.0F + 12.0F * s + 0.1F, 0.0F + 14.0F * s, 0.0F);
      RagdollLimb nodePivot1 = new RagdollLimb(3.0F, 0.0F, 0.0F + 32.0F * s, -0.1F);
      RagdollLimb nodePivot2 = new RagdollLimb(3.0F, 0.0F, 0.0F + 16.0F * s, 0.0F);
      RagdollLimb nodeRightHip = new RagdollLimb(1.0F, 0.0F - 4.9F * s, 0.0F + 16.0F * s, 0.0F);
      RagdollLimb nodeLeftHip = new RagdollLimb(1.0F, 0.0F + 4.9F * s, 0.0F + 16.0F * s, 0.0F);
      RagdollLimb nodeRightLeg = new RagdollLimb(1.0F, 0.0F - 5.0F * s, 0.1F, 0.0F);
      RagdollLimb nodeLeftLeg = new RagdollLimb(1.0F, 0.0F + 5.0F * s, 0.1F, 0.0F);
      RagdollBase skeleton = new RagdollBase();
      nodeRightHip.attachTo(nodeRightLeg, 16.0F * s, 0.5F).addBox(4).setOffset(0.0F, 0.0F, -0.2F);
      nodeLeftHip.attachTo(nodeLeftLeg, 16.0F * s, 0.5F).addBox(5).setOffset(0.0F, 0.0F, -0.2F);
      nodeRightHip.attachTo(nodePivot2, 4.9F * s, 0.5F).setVisible(false);
      nodeLeftHip.attachTo(nodePivot2, 4.9F * s, 0.5F).setVisible(false);
      nodePivot1.attachTo(nodeNeck, 12.0F * s, 1.0F).addBox(6).alignToSideNode(nodeRightHip).setInvert(true).setOffset(0.0F, 0.75F, -0.2F);
      nodeNeck.attachTo(nodeRightShoulder, 12.0F * s, 1.0F).setVisible(false);
      nodeNeck.attachTo(nodeLeftShoulder, 12.0F * s, 1.0F).setVisible(false);
      nodeRightArm.attachTo(nodeRightShoulder, 30.0F * s, 1.0F).addBox(2).setInvert(true).setOffset(0.0F, 0.0F, -0.2F);
      nodeLeftArm.attachTo(nodeLeftShoulder, 30.0F * s, 1.0F).addBox(3).setInvert(true).setOffset(0.0F, 0.0F, -0.2F);
      nodeHead.attachTo(nodeNeck, 8.0F * s, 1.0F).addBox(0).setOffset(0.0F, 0.0F, -0.2F).setUpsidedown(false).setInvert(true).setRotation(0.0F, 0.0F, 180.0F);
      nodePivot1.attachTo(nodePivot2, 16.0F * s, 1.0F).addBox(1).alignToSideNode(nodeRightHip).setOffset(0.0F, 1.0F, -0.2F);
      nodePivot1.attachTo(nodeRightHip, 16.7335F * s, 1.0F).setVisible(false);
      nodePivot1.attachTo(nodeLeftHip, 16.7335F * s, 1.0F).setVisible(false);
      nodeRightHip.attachTo(nodeLeftHip, 9.8F * s, 1.0F).setVisible(false);
      nodeNeck.attachTo(nodeRightHip, 28.425516F * s, 1.0F).setVisible(false);
      nodeNeck.attachTo(nodeLeftHip, 28.425516F * s, 1.0F).setVisible(false);
      nodeRightShoulder.attachTo(nodeLeftShoulder, 24.0F * s, 1.0F).setVisible(false);
      nodeRightShoulder.attachTo(nodeRightHip, 28.886156F * s, 1.0F).setVisible(false);
      nodeLeftShoulder.attachTo(nodeLeftHip, 28.886156F * s, 1.0F).setVisible(false);
      nodeRightShoulder.attachTo(nodeRightLeg, 36.0F * s, 0.01F).setVisible(false);
      nodeLeftShoulder.attachTo(nodeLeftLeg, 36.0F * s, 0.01F).setVisible(false);
      nodeLeftShoulder.attachTo(nodePivot1, 16.970562F * s, 1.0F).setVisible(false);
      nodeRightShoulder.attachTo(nodePivot1, 16.970562F * s, 1.0F).setVisible(false);
      nodeHead.attachTo(nodePivot1, 20.0F * s, 0.1F).setVisible(false);
      skeleton.nodeList.add(nodeHead);
      skeleton.nodeList.add(nodeNeck);
      skeleton.nodeList.add(nodeRightShoulder);
      skeleton.nodeList.add(nodeLeftShoulder);
      skeleton.nodeList.add(nodeRightArm);
      skeleton.nodeList.add(nodeLeftArm);
      skeleton.nodeList.add(nodePivot1);
      skeleton.nodeList.add(nodePivot2);
      skeleton.nodeList.add(nodeRightHip);
      skeleton.nodeList.add(nodeLeftHip);
      skeleton.nodeList.add(nodeRightLeg);
      skeleton.nodeList.add(nodeLeftLeg);
      return skeleton;
   }
}
