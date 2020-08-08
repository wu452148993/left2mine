package net.thecallunxz.left2mine.ragdoll.physics;

import java.util.ArrayList;
import java.util.List;
import net.thecallunxz.left2mine.util.Left2MineUtilities;
import org.lwjgl.util.vector.Vector3f;

public class RagdollBase {
   public List<RagdollLimb> nodeList = new ArrayList();

   public void update() {
      int i;
      for(i = 0; i < RagdollMaths.constraintAccuracy; ++i) {
         int i;
         for(i = 0; i < this.nodeList.size(); ++i) {
            ((RagdollLimb)this.nodeList.get(i)).solveConstraints();
         }

         for(i = 0; i < this.nodeList.size(); ++i) {
            ((RagdollLimb)this.nodeList.get(i)).collide();
         }
      }

      for(i = 0; i < this.nodeList.size(); ++i) {
         ((RagdollLimb)this.nodeList.get(i)).update(RagdollMaths.fixedDeltaTimeSeconds);
      }

   }

   public void display() {
      for(int i = 0; i < this.nodeList.size(); ++i) {
         ((RagdollLimb)this.nodeList.get(i)).display();
      }

   }

   public void resetMotion() {
      for(int i = 0; i < this.nodeList.size(); ++i) {
         ((RagdollLimb)this.nodeList.get(i)).resetMotion();
      }

   }

   public void move(float argX, float argY, float argZ) {
      for(int i = 0; i < this.nodeList.size(); ++i) {
         RagdollLimb var10000 = (RagdollLimb)this.nodeList.get(i);
         var10000.x += argX;
         var10000 = (RagdollLimb)this.nodeList.get(i);
         var10000.y += argY;
         var10000 = (RagdollLimb)this.nodeList.get(i);
         var10000.z += argZ;
      }

   }

   public void rotateY(float argRotY) {
      for(int i = 0; i < this.nodeList.size(); ++i) {
         Vector3f loc = Left2MineUtilities.rotateY(new Vector3f(((RagdollLimb)this.nodeList.get(i)).x, ((RagdollLimb)this.nodeList.get(i)).y, ((RagdollLimb)this.nodeList.get(i)).z), argRotY);
         ((RagdollLimb)this.nodeList.get(i)).x = loc.x;
         ((RagdollLimb)this.nodeList.get(i)).y = loc.y;
         ((RagdollLimb)this.nodeList.get(i)).z = loc.z;
      }

   }

   public void rotateAroundAverageY(float argRotY) {
      for(int i = 0; i < this.nodeList.size(); ++i) {
         float sX = this.getAveragePosition().x;
         float sY = this.getAveragePosition().y;
         float sZ = this.getAveragePosition().z;
         RagdollLimb var10000 = (RagdollLimb)this.nodeList.get(i);
         var10000.x -= sX;
         var10000 = (RagdollLimb)this.nodeList.get(i);
         var10000.y -= sY;
         var10000 = (RagdollLimb)this.nodeList.get(i);
         var10000.z -= sZ;
         Vector3f loc = Left2MineUtilities.rotateY(new Vector3f(((RagdollLimb)this.nodeList.get(i)).x, ((RagdollLimb)this.nodeList.get(i)).y, ((RagdollLimb)this.nodeList.get(i)).z), argRotY);
         ((RagdollLimb)this.nodeList.get(i)).x = loc.x;
         ((RagdollLimb)this.nodeList.get(i)).y = loc.y;
         ((RagdollLimb)this.nodeList.get(i)).z = loc.z;
         var10000 = (RagdollLimb)this.nodeList.get(i);
         var10000.x += sX;
         var10000 = (RagdollLimb)this.nodeList.get(i);
         var10000.y += sY;
         var10000 = (RagdollLimb)this.nodeList.get(i);
         var10000.z += sZ;
      }

   }

   public Vector3f getAveragePosition() {
      Vector3f position = new Vector3f();

      for(int i = 0; i < this.nodeList.size(); ++i) {
         position.x += ((RagdollLimb)this.nodeList.get(i)).x;
         position.y += ((RagdollLimb)this.nodeList.get(i)).y;
         position.z += ((RagdollLimb)this.nodeList.get(i)).z;
      }

      position.x /= (float)this.nodeList.size();
      position.y /= (float)this.nodeList.size();
      position.z /= (float)this.nodeList.size();
      return position;
   }

   public void push(double x, double y, double z) {
      for(int i = 0; i < this.nodeList.size(); ++i) {
         ((RagdollLimb)this.nodeList.get(i)).applyForce((float)x, (float)y, (float)z);
      }

   }
}
