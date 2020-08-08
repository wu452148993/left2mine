package net.thecallunxz.left2mine.ragdoll.physics;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RagdollJoint {
   public RagdollLimb node1;
   public RagdollLimb node2;
   float restingDistance;
   float stiffness;
   public boolean show = true;
   public boolean invert = false;
   public boolean upsidedown = false;
   public boolean sticky = false;
   public RagdollLimb alignSideNode = null;
   public float offsetX = 0.0F;
   public float offsetY = 0.0F;
   public float offsetZ = 0.0F;
   public float rotationX = 0.0F;
   public float rotationY = 0.0F;
   public float rotationZ = 0.0F;
   public List<RagdollJoint.DisplayBox> boxList = new ArrayList();

   public RagdollJoint(RagdollLimb argNode1, RagdollLimb argNode2, float argRestingDist, float argStiffness) {
      this.node1 = argNode1;
      this.node2 = argNode2;
      this.restingDistance = argRestingDist;
      this.stiffness = argStiffness;
   }

   void solve() {
      float diffX = this.node1.x - this.node2.x;
      float diffY = this.node1.y - this.node2.y;
      float diffZ = this.node1.z - this.node2.z;
      float d = (float)Math.sqrt((double)(diffX * diffX + diffY * diffY + diffZ * diffZ));
      float difference = (this.restingDistance - d) / d;
      float im1 = 1.0F / this.node1.mass;
      float im2 = 1.0F / this.node2.mass;
      float scalarP1 = im1 / (im1 + im2) * this.stiffness;
      float scalarP2 = this.stiffness - scalarP1;
      if (!this.sticky) {
         RagdollLimb var10000 = this.node1;
         var10000.x += diffX * scalarP1 * difference;
         var10000 = this.node1;
         var10000.y += diffY * scalarP1 * difference;
         var10000 = this.node1;
         var10000.z += diffZ * scalarP1 * difference;
         var10000 = this.node2;
         var10000.x -= diffX * scalarP2 * difference;
         var10000 = this.node2;
         var10000.y -= diffY * scalarP2 * difference;
         var10000 = this.node2;
         var10000.z -= diffZ * scalarP2 * difference;
      } else {
         this.node1.x = this.node2.x;
         this.node1.y = this.node2.y;
         this.node1.z = this.node2.z;
      }

   }

   public void compileDisplayList(ModelRenderer argBox, int id) {
      ((RagdollJoint.DisplayBox)this.boxList.get(id)).displayList = GLAllocation.generateDisplayLists(1);
      GL11.glNewList(((RagdollJoint.DisplayBox)this.boxList.get(id)).displayList, 4864);
      BufferBuilder worldrenderer = Tessellator.getInstance().getBuffer();

      for(int s = 0; s < argBox.cubeList.size(); ++s) {
         int i = this.getBrightnessForRender((ModelBox)argBox.cubeList.get(s));
         int j = i >> 16 & '\uffff';
         int k = i & '\uffff';
         worldrenderer = worldrenderer.lightmap(j, k);
         ((ModelBox)argBox.cubeList.get(s)).render(worldrenderer, 0.0F);
      }

      GL11.glEndList();
      ((RagdollJoint.DisplayBox)this.boxList.get(id)).compiled = true;
   }

   public void display() {
      GL11.glPushMatrix();
      GL11.glDisable(3553);
      GL11.glDepthFunc(519);
      GL11.glTranslatef(this.node1.x, this.node1.y, this.node1.z);
      GL11.glColor3f(0.0F, 1.0F, 0.0F);
      GL11.glBegin(1);
      GL11.glVertex3f(0.0F, 0.0F, 0.0F);
      GL11.glVertex3f(this.node2.x - this.node1.x, this.node2.y - this.node1.y, this.node2.z - this.node1.z);
      GL11.glEnd();
      GL11.glDepthFunc(515);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
   }

   public RagdollJoint setVisible(boolean argShow) {
      this.show = argShow;
      return this;
   }

   public RagdollJoint setSticky(boolean argSticky) {
      this.sticky = argSticky;
      return this;
   }

   public RagdollJoint addBox(int argBoxID) {
      this.boxList.add(new RagdollJoint.DisplayBox(argBoxID));
      return this;
   }

   public RagdollJoint setInvert(boolean argInvert) {
      this.invert = argInvert;
      return this;
   }

   public RagdollJoint alignToSideNode(RagdollLimb argSideNode) {
      this.alignSideNode = argSideNode;
      return this;
   }

   public RagdollJoint setUpsidedown(boolean arg) {
      this.upsidedown = arg;
      return this;
   }

   public RagdollJoint setOffset(float argX, float argY, float argZ) {
      this.offsetX = argX;
      this.offsetY = argY;
      this.offsetZ = argZ;
      return this;
   }

   public RagdollJoint setRotation(float argX, float argY, float argZ) {
      this.rotationX = argX;
      this.rotationY = argY;
      this.rotationZ = argZ;
      return this;
   }

   public int getDisplayList(ModelRenderer argBox, int id) {
      if (!((RagdollJoint.DisplayBox)this.boxList.get(id)).compiled) {
         Field displayListField = null;

         try {
            displayListField = ModelRenderer.class.getDeclaredField("displayList");
         } catch (NoSuchFieldException var11) {
            try {
               displayListField = ModelRenderer.class.getDeclaredField("field_78811_r");
            } catch (NoSuchFieldException var9) {
               var9.printStackTrace();
            } catch (SecurityException var10) {
               var10.printStackTrace();
            }
         }

         if (displayListField != null) {
            try {
               displayListField.setAccessible(true);
               ((RagdollJoint.DisplayBox)this.boxList.get(id)).displayList = displayListField.getInt(argBox);
               if (((RagdollJoint.DisplayBox)this.boxList.get(id)).displayList == 0) {
                  ((RagdollJoint.DisplayBox)this.boxList.get(id)).displayList = GLAllocation.generateDisplayLists(1);
                  GL11.glNewList(((RagdollJoint.DisplayBox)this.boxList.get(id)).displayList, 4864);
                  BufferBuilder worldrenderer = Tessellator.getInstance().getBuffer();

                  for(int s = 0; s < argBox.cubeList.size(); ++s) {
                     int i = this.getBrightnessForRender((ModelBox)argBox.cubeList.get(s));
                     int j = i >> 16 & '\uffff';
                     int k = i & '\uffff';
                     worldrenderer = worldrenderer.lightmap(j, k);
                     ((ModelBox)argBox.cubeList.get(s)).render(worldrenderer, 0.0F);
                  }

                  GL11.glEndList();
               }
            } catch (SecurityException var12) {
               var12.printStackTrace();
            } catch (IllegalArgumentException var13) {
               var13.printStackTrace();
            } catch (IllegalAccessException var14) {
               var14.printStackTrace();
            }
         }

         ((RagdollJoint.DisplayBox)this.boxList.get(id)).compiled = true;
      }

      return ((RagdollJoint.DisplayBox)this.boxList.get(id)).displayList;
   }

   public int getBrightnessForRender(ModelBox modelBox) {
      World world = Minecraft.getMinecraft().world;
      if (world == null) {
         return 0;
      } else {
         BlockPos blockpos = new BlockPos((double)((modelBox.posX1 + modelBox.posX2) / 2.0F), (double)((modelBox.posY1 + modelBox.posY2) / 2.0F), (double)((modelBox.posZ1 + modelBox.posZ2) / 2.0F));
         return world.isBlockLoaded(blockpos) ? world.getCombinedLight(blockpos, 0) : 0;
      }
   }

   public static class DisplayBox {
      public int id;
      public int displayList;
      public boolean compiled;

      public DisplayBox() {
         this.compiled = false;
         this.compiled = false;
      }

      public DisplayBox(int id) {
         this();
         this.id = id;
      }
   }
}
