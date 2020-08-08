package net.thecallunxz.left2mine.ragdoll;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.ragdoll.corpse.CustomCorpse;
import net.thecallunxz.left2mine.ragdoll.physics.RagdollBase;
import net.thecallunxz.left2mine.ragdoll.physics.RagdollJoint;
import net.thecallunxz.left2mine.ragdoll.physics.RagdollLimb;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Ragdoll {
   public RagdollBase skeleton;
   public ModelBase model;
   public ResourceLocation texture;
   public static int displayRange = 50;
   public static int despawnRange = 50;
   public float ticksExisted;

   public Ragdoll(Entity argEntity) {
      this((float)argEntity.posX, (float)argEntity.posY, (float)argEntity.posZ, argEntity);
   }

   public Ragdoll(float argX, float argY, float argZ, Entity argEntity) {
      this.ticksExisted = 0.0F;
      this.texture = null;
      if (argEntity != null) {
         RenderLivingBase render = (RenderLivingBase)Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(argEntity);
         Method textureField = null;

         try {
            textureField = Render.class.getDeclaredMethod("func_110775_a", Entity.class);
         } catch (NoSuchMethodException var15) {
            try {
               textureField = Render.class.getDeclaredMethod("getEntityTexture", Entity.class);
            } catch (NoSuchMethodException var14) {
               var14.printStackTrace();
            }
         }

         if (textureField != null) {
            try {
               textureField.setAccessible(true);

               try {
                  this.texture = (ResourceLocation)textureField.invoke(render, argEntity);
               } catch (IllegalAccessException var10) {
                  var10.printStackTrace();
               } catch (IllegalArgumentException var11) {
                  var11.printStackTrace();
               } catch (InvocationTargetException var12) {
                  var12.printStackTrace();
               }
            } catch (SecurityException var13) {
               var13.printStackTrace();
            }
         }

         this.model = render.getMainModel();
         this.skeleton = CustomCorpse.getSkeletonForModel(argEntity, this.model);
         this.orient(argEntity);
      } else {
         this.skeleton = CustomCorpse.getSkeletonForModel((Entity)null, (ModelBase)null);
         this.skeleton.move(argX, argY, argZ);
      }

      this.skeleton.resetMotion();
   }

   public void orient(Entity argEntity) {
      this.skeleton.rotateY(argEntity.prevRotationYaw);
      this.skeleton.move((float)argEntity.posX, (float)argEntity.posY, (float)argEntity.posZ);
   }

   public Vector3f getAveragePosition() {
      return this.skeleton.getAveragePosition();
   }

   public float getDistanceToPlayer() {
      Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
      if (entity == null) {
         return 0.0F;
      } else {
         Vector3f viewLoc = new Vector3f((float)entity.posX, (float)entity.posY, (float)entity.posZ);
         viewLoc.x -= this.getAveragePosition().x;
         viewLoc.y -= this.getAveragePosition().y;
         viewLoc.z -= this.getAveragePosition().z;
         return viewLoc.length();
      }
   }

   public void display(float partialTicks) {
      if (this.getDistanceToPlayer() <= (float)displayRange) {
         GlStateManager.enableBlend();
         GL11.glEnable(3042);
         GlStateManager.blendFunc(770, 771);
         GlStateManager.alphaFunc(516, 0.003921569F);
         GL11.glBlendFunc(770, 771);
         GL11.glPushMatrix();
         GL11.glDisable(2884);
         GlStateManager.enableRescaleNormal();

         for(int i = 0; i < this.skeleton.nodeList.size(); ++i) {
            RagdollLimb node = (RagdollLimb)this.skeleton.nodeList.get(i);
            GL11.glColor4f(this.getBrightness(node), this.getBrightness(node), this.getBrightness(node), 1.0F);

            for(int l = 0; l < node.links.size(); ++l) {
               RagdollJoint link = (RagdollJoint)node.links.get(l);
               Vector3f dir = new Vector3f(link.node2.getRenderX(partialTicks) - link.node1.getRenderX(partialTicks), link.node2.getRenderY(partialTicks) - link.node1.getRenderY(partialTicks), link.node2.getRenderZ(partialTicks) - link.node1.getRenderZ(partialTicks));
               if (link.invert) {
                  dir = (Vector3f)dir.negate();
               }

               if (dir.length() != 0.0F) {
                  dir = (Vector3f)dir.normalise();
               }

               float rotY = (float)(Math.atan2((double)dir.x, (double)dir.z) / 3.141592653589793D * 180.0D);
               float rotX = (float)(Math.atan2(Math.sqrt((double)(dir.x * dir.x + dir.z * dir.z)), (double)dir.y) / 3.141592653589793D * 180.0D);
               if (link.show) {
                  GL11.glPushMatrix();
                  if (link.invert) {
                     GL11.glTranslatef(link.node2.getRenderX(partialTicks), link.node2.getRenderY(partialTicks), link.node2.getRenderZ(partialTicks));
                  } else {
                     GL11.glTranslatef(link.node1.getRenderX(partialTicks), link.node1.getRenderY(partialTicks), link.node1.getRenderZ(partialTicks));
                  }

                  GL11.glRotatef(rotY + link.rotationY, 0.0F, 1.0F, 0.0F);
                  GL11.glRotatef(rotX + link.rotationX, 1.0F, 0.0F, 0.0F);
                  GL11.glRotatef(link.rotationZ, 0.0F, 0.0F, 1.0F);
                  GL11.glTranslatef(link.offsetX, link.offsetY, link.offsetZ);
                  if (link.upsidedown) {
                     GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                  }

                  float length = 1.0F;
                  float width = 0.2F;
                  float depth = 0.2F;
                  float offsetX = -0.1F;
                  float offsetY = 0.0F;
                  float offsetZ = -0.1F;
                  if (link.boxList.size() >= 0) {
                     if (this.texture != null) {
                        GL11.glEnable(3553);
                        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
                     }

                     for(int j = 0; j < link.boxList.size(); ++j) {
                        ModelRenderer box = (ModelRenderer)this.model.boxList.get(((RagdollJoint.DisplayBox)link.boxList.get(j)).id);
                        GlStateManager.callList(link.getDisplayList(box, j));
                     }
                  }

                  GL11.glPopMatrix();
               }
            }
         }

         GL11.glEnable(3553);
         GL11.glEnable(2884);
         GlStateManager.disableRescaleNormal();
         GlStateManager.enableCull();
         GL11.glPopMatrix();
         GL11.glColor3f(1.0F, 1.0F, 1.0F);
      }

   }

   public float getBrightness(RagdollLimb node) {
      BlockPos blockpos = new BlockPos((double)node.x, (double)Math.round(node.y), (double)node.z);
      World world = Minecraft.getMinecraft().world;
      int skyLightSub = world.calculateSkylightSubtracted(1.0F);
      int blockLight = world.getLightFor(EnumSkyBlock.BLOCK, blockpos);
      int skyLight = world.getLightFor(EnumSkyBlock.SKY, blockpos) - skyLightSub;
      if (skyLight == 0) {
         skyLight = world.getLightFor(EnumSkyBlock.SKY, blockpos.down()) - skyLightSub;
      }

      if (blockLight == 0) {
         blockLight = world.getLightFor(EnumSkyBlock.BLOCK, blockpos.down());
      }

      int mixedLight = Math.max(blockLight, skyLight);
      switch(mixedLight) {
      case 0:
         return 0.1F;
      case 1:
         return 0.17F;
      case 2:
         return 0.25F;
      case 3:
         return 0.3F;
      case 4:
         return 0.35F;
      case 5:
         return 0.37F;
      case 6:
         return 0.39F;
      case 7:
         return 0.41F;
      case 8:
         return 0.43F;
      case 9:
         return 0.45F;
      case 10:
         return 0.47F;
      case 11:
         return 0.49F;
      case 12:
         return 0.51F;
      case 13:
         return 0.53F;
      case 14:
         return 0.57F;
      case 15:
         return 0.75F;
      default:
         return 0.0F;
      }
   }

   public void update() {
      ++this.ticksExisted;
      this.skeleton.update();
   }
}
