package net.thecallunxz.left2mine.ragdoll.physics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.thecallunxz.left2mine.util.Left2MineClientUtilities;
import net.thecallunxz.left2mine.util.Left2MineUtilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class RagdollLimb {
   public static final float gravity = -50.0F;
   public static boolean COLLIDE = true;
   public float x;
   public float y;
   public float z;
   public float lastX;
   public float lastY;
   public float lastZ;
   public float accX;
   public float accY;
   public float accZ;
   public boolean onGround = false;
   public float mass = 1.0F;
   public float damping = 20.0F;
   public boolean pinned = false;
   public float pinX;
   public float pinY;
   public float pinZ;
   public List<RagdollJoint> links = new ArrayList();

   public RagdollLimb(float argMass, float argX, float argY, float argZ) {
      this.x = argX;
      this.y = argY;
      this.z = argZ;
      this.lastX = this.x;
      this.lastY = this.y;
      this.lastZ = this.z;
      this.accX = 0.0F;
      this.accY = 0.0F;
      this.accZ = 0.0F;
      this.mass = argMass;
   }

   public void resetMotion() {
      this.lastX = this.x;
      this.lastY = this.y;
      this.lastZ = this.z;
      this.accX = 0.0F;
      this.accY = 0.0F;
      this.accZ = 0.0F;
   }

   public void update(float timeStep) {
      this.applyForce(0.0F, this.mass * -50.0F, 0.0F);
      float velX = this.x - this.lastX;
      float velY = this.y - this.lastY;
      float velZ = this.z - this.lastZ;
      velX = (float)((double)velX * 0.99D);
      velY = (float)((double)velY * 0.99D);
      velZ = (float)((double)velZ * 0.99D);
      if (this.onGround) {
         velX *= 0.5F;
         velZ *= 0.5F;
      }

      float timeStepSq = timeStep * timeStep;
      float nextX = (float)((double)(this.x + velX) + 0.5D * (double)this.accX * (double)timeStepSq);
      float nextY = (float)((double)(this.y + velY) + 0.5D * (double)this.accY * (double)timeStepSq);
      float nextZ = (float)((double)(this.z + velZ) + 0.5D * (double)this.accZ * (double)timeStepSq);
      this.lastX = this.x;
      this.lastY = this.y;
      this.lastZ = this.z;
      this.x = nextX;
      this.y = nextY;
      this.z = nextZ;
      this.accX = 0.0F;
      this.accY = 0.0F;
      this.accZ = 0.0F;
   }

   public void solveConstraints() {
      WorldClient world = Minecraft.getMinecraft().world;

      for(int i = 0; i < this.links.size(); ++i) {
         RagdollJoint currentLink = (RagdollJoint)this.links.get(i);
         currentLink.solve();
      }

      if (this.pinned) {
         this.x = this.pinX;
         this.y = this.pinY;
         this.z = this.pinZ;
      }

   }

   public void collide() {
      WorldClient world = Minecraft.getMinecraft().world;
      BlockPos pos = new BlockPos((double)this.x, (double)this.y, (double)this.z);
      world.getBlockState(pos);
      AxisAlignedBB nodeBounds = this.getBoundingBox();
      float velX = this.x - this.lastX;
      float velY = this.y - this.lastY;
      float velZ = this.z - this.lastZ;
      double d3 = (double)this.x;
      double d4 = (double)this.y;
      double d5 = (double)this.z;
      double d6 = (double)velX;
      double d7 = (double)velY;
      double d8 = (double)velZ;
      this.x = this.lastX;
      this.y = this.lastY;
      this.z = this.lastZ;
      COLLIDE = true;
      if (COLLIDE) {
         List<AxisAlignedBB> collidingEntityBoxes = Left2MineUtilities.getCollidingEntities(world, this.getBoundingBox().expand((double)velX, (double)velY, (double)velZ));

         for(int i = 0; i < collidingEntityBoxes.size(); ++i) {
            float xCenter = (float)(((AxisAlignedBB)collidingEntityBoxes.get(i)).minX + (((AxisAlignedBB)collidingEntityBoxes.get(i)).maxX - ((AxisAlignedBB)collidingEntityBoxes.get(i)).minX) / 2.0D);
            float yCenter = (float)(((AxisAlignedBB)collidingEntityBoxes.get(i)).minY + (((AxisAlignedBB)collidingEntityBoxes.get(i)).maxY - ((AxisAlignedBB)collidingEntityBoxes.get(i)).minY) / 2.0D);
            float zCenter = (float)(((AxisAlignedBB)collidingEntityBoxes.get(i)).minZ + (((AxisAlignedBB)collidingEntityBoxes.get(i)).maxZ - ((AxisAlignedBB)collidingEntityBoxes.get(i)).minZ) / 2.0D);
            Vector3f vec = new Vector3f(this.x - xCenter, this.y - yCenter, this.z - zCenter);
            vec = (Vector3f)vec.normalise();
            float speed = 0.04F;
            velX += vec.x * speed;
            velY += vec.y * speed;
            velZ += vec.z * speed;
         }

         collidingEntityBoxes = null;
         this.onGround = false;
         List<AxisAlignedBB> list = Left2MineUtilities.getCollidingBoundingBoxes(world, this.getBoundingBox().expand((double)velX, (double)velY, (double)velZ));
         if (list.size() > 0) {
            this.onGround = true;
         }

         AxisAlignedBB axisalignedbb = this.getBoundingBox();

         AxisAlignedBB axisalignedbb1;
         for(Iterator iterator = list.iterator(); iterator.hasNext(); velY = (float)axisalignedbb1.calculateYOffset(this.getBoundingBox(), (double)velY)) {
            axisalignedbb1 = (AxisAlignedBB)iterator.next();
         }

         this.y += velY;
         boolean var10000;
         if (!this.onGround && (d7 == (double)this.y || d7 >= 0.0D)) {
            var10000 = false;
         } else {
            var10000 = true;
         }

         AxisAlignedBB axisalignedbb2;
         for(Iterator iterator8 = list.iterator(); iterator8.hasNext(); velX = (float)axisalignedbb2.calculateXOffset(this.getBoundingBox(), (double)velX)) {
            axisalignedbb2 = (AxisAlignedBB)iterator8.next();
         }

         this.x += velX;

         AxisAlignedBB axisalignedbb3;
         for(Iterator iterator8 = list.iterator(); iterator8.hasNext(); velZ = (float)axisalignedbb3.calculateZOffset(this.getBoundingBox(), (double)velZ)) {
            axisalignedbb3 = (AxisAlignedBB)iterator8.next();
         }

         this.z += velZ;
         Left2MineUtilities.getCollidingBoundingBoxes(world, this.getBoundingBox().expand((double)velX, (double)velY, (double)velZ));
      }

   }

   public RagdollJoint attachTo(RagdollLimb P, float restingDist, float stiff) {
      RagdollJoint lnk = new RagdollJoint(this, P, restingDist, stiff);
      this.links.add(lnk);
      return lnk;
   }

   public void removeLink(RagdollJoint lnk) {
      this.links.remove(lnk);
   }

   public void applyForce(float fX, float fY, float fZ) {
      this.accX += fX / this.mass;
      this.accY += fY / this.mass;
      this.accZ += fZ / this.mass;
   }

   public void pinTo(float pX, float pY, float pZ) {
      this.pinned = true;
      this.pinX = pX;
      this.pinY = pY;
      this.pinZ = pZ;
   }

   public void display() {
      GL11.glPushMatrix();
      GL11.glDisable(3553);
      GL11.glDepthFunc(519);
      GL11.glTranslatef(this.x, this.y, this.z);
      GL11.glColor3f(1.0F, 0.0F, 0.0F);
      Left2MineClientUtilities.renderBox(7);
      GL11.glDepthFunc(515);
      GL11.glEnable(3553);
      GL11.glPopMatrix();

      for(int i = 0; i < this.links.size(); ++i) {
         ((RagdollJoint)this.links.get(i)).display();
      }

   }

   public AxisAlignedBB getBoundingBox() {
      float boundWidth = 0.1F;
      return new AxisAlignedBB((double)(this.x - boundWidth), (double)(this.y - boundWidth), (double)(this.z - boundWidth), (double)(this.x + boundWidth), (double)(this.y + boundWidth), (double)(this.z + boundWidth));
   }

   public float getRenderX(float partialTicks) {
      return this.lastX + (this.x - this.lastX) * partialTicks;
   }

   public float getRenderY(float partialTicks) {
      return this.lastY + (this.y - this.lastY) * partialTicks;
   }

   public float getRenderZ(float partialTicks) {
      return this.lastZ + (this.z - this.lastZ) * partialTicks;
   }
}
