package net.thecallunxz.left2mine.render;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.Profile;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerEntityOnShoulder;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.items.usable.ItemFirstAid;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.util.Left2MineClientUtilities;

@SideOnly(Side.CLIENT)
public class RenderCustomPlayer extends RenderLivingBase<AbstractClientPlayer> {
   public RenderCustomPlayer(RenderManager renderManager, RenderPlayer player) {
      super(renderManager, player.getMainModel(), 0.5F);
      this.addLayer(new LayerBipedArmor(this));
      this.addLayer(new LayerHeldItem(this));
      this.addLayer(new LayerArrow(this));
      this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
      this.addLayer(new LayerElytra(this));
      this.addLayer(new LayerEntityOnShoulder(renderManager));
      this.addLayer(new LayerEquip());
   }

   public ModelPlayer getMainModel() {
      return (ModelPlayer)super.getMainModel();
   }

   public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!entity.isUser() || this.renderManager.renderViewEntity == entity) {
         double d0 = y;
         if (entity.isSneaking()) {
            d0 = y - 0.125D;
         }

         this.setModelVisibilities(entity);
         GlStateManager.enableBlendProfile(Profile.PLAYER_SKIN);
         this.myRender(entity, x, d0, z, entityYaw, partialTicks);
         GlStateManager.disableBlendProfile(Profile.PLAYER_SKIN);
      }

   }

   private void setModelVisibilities(AbstractClientPlayer clientPlayer) {
      ModelPlayer modelplayer = this.getMainModel();
      if (clientPlayer.isSpectator()) {
         modelplayer.setVisible(false);
      } else {
         ItemStack itemstack = clientPlayer.getHeldItemMainhand();
         ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
         modelplayer.setVisible(true);
         modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
         modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
         modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
         modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
         modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
         modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
         modelplayer.isSneak = clientPlayer.isSneaking();
         ArmPose modelbiped$armpose = ArmPose.EMPTY;
         ArmPose modelbiped$armpose1 = ArmPose.EMPTY;
         EnumAction enumaction1;
         if (!itemstack.isEmpty()) {
            modelbiped$armpose = ArmPose.ITEM;
            if (itemstack.getItem() instanceof ItemGun) {
               modelbiped$armpose = ArmPose.BOW_AND_ARROW;
            }

            if (clientPlayer.getItemInUseCount() > 0) {
               enumaction1 = itemstack.getItemUseAction();
               if (itemstack.getItem() instanceof ItemFirstAid) {
                  modelbiped$armpose = ArmPose.BLOCK;
               }

               if (enumaction1 == EnumAction.BLOCK) {
                  modelbiped$armpose = ArmPose.BLOCK;
               } else if (enumaction1 == EnumAction.BOW) {
                  modelbiped$armpose = ArmPose.BOW_AND_ARROW;
               }
            }

            if (itemstack.getItem() instanceof ItemGun && itemstack.hasTagCompound() && itemstack.getTagCompound().getInteger("reloadTime") > 0) {
               modelbiped$armpose = ArmPose.BLOCK;
            }
         }

         if (!itemstack1.isEmpty()) {
            modelbiped$armpose1 = ArmPose.ITEM;
            if (itemstack1.getItem() instanceof ItemGun) {
               modelbiped$armpose1 = ArmPose.BOW_AND_ARROW;
            }

            if (clientPlayer.getItemInUseCount() > 0) {
               enumaction1 = itemstack1.getItemUseAction();
               if (enumaction1 == EnumAction.BLOCK) {
                  modelbiped$armpose1 = ArmPose.BLOCK;
               } else if (enumaction1 == EnumAction.BOW) {
                  modelbiped$armpose1 = ArmPose.BOW_AND_ARROW;
               }
            }
         }

         if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT) {
            modelplayer.rightArmPose = modelbiped$armpose;
            modelplayer.leftArmPose = modelbiped$armpose1;
         } else {
            modelplayer.rightArmPose = modelbiped$armpose1;
            modelplayer.leftArmPose = modelbiped$armpose;
         }
      }

   }

   public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
      return entity.getLocationSkin();
   }

   public void transformHeldFull3DItemLayer() {
      GlStateManager.translate(0.0F, 0.1875F, 0.0F);
   }

   protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime) {
      float f = 0.9375F;
      GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
   }

   protected void renderEntityName(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq) {
      if (distanceSq < 100.0D) {
         Scoreboard scoreboard = entityIn.getWorldScoreboard();
         ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
         if (scoreobjective != null) {
            Score score = scoreboard.getOrCreateScore(entityIn.getName(), scoreobjective);
            this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreobjective.getDisplayName(), x, y, z, 64);
            y += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * 0.025F);
         }

         float div = 1.0F;
         IEquip equip = (IEquip)entityIn.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         String healthString = "[";
         if (equip.getLying()) {
            div = 3.0F;
         }

         float realHP = entityIn.getHealth() / div;
         float tempHP = entityIn.getAbsorptionAmount() / div;
         float emptyHP = (float)(20 - ((int)realHP + (int)tempHP + 1));
         float healthScale = (realHP + tempHP) / 20.0F;
         int i;
         if (equip.getLying()) {
            for(i = 0; i < (int)realHP + (int)tempHP + 1; ++i) {
               healthString = healthString + "||";
            }

            for(i = 0; i < (int)emptyHP; ++i) {
               healthString = healthString + " ";
            }
         } else {
            for(i = 0; i < (int)realHP; ++i) {
               healthString = healthString + "||";
            }

            for(i = 0; i < (int)tempHP; ++i) {
               healthString = healthString + "..";
            }

            if ((int)tempHP + (int)realHP == 0) {
               healthString = healthString + "||";
            }

            for(i = 0; i < (int)emptyHP; ++i) {
               healthString = healthString + " ";
            }

            if ((int)realHP + (int)tempHP < 20) {
               healthString = healthString + " ";
            }
         }

         healthString = healthString + "]";
         if (!Minecraft.getMinecraft().player.capabilities.allowEdit) {
            this.depthTextRender(entityIn, healthString, x, y, z, 100, healthScale);
            y += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * 0.025F);
         }
      }

      super.renderEntityName(entityIn, x, y, z, name, distanceSq);
   }

   private void depthTextRender(Entity entityIn, String str, double x, double y, double z, int maxDistanceSq, float healthScale) {
      double d0 = entityIn.getDistanceSq(this.renderManager.renderViewEntity);
      if (d0 <= (double)maxDistanceSq) {
         boolean flag = entityIn.isSneaking();
         float f = this.renderManager.playerViewY;
         float f1 = this.renderManager.playerViewX;
         boolean flag1 = this.renderManager.options.thirdPersonView == 2;
         float f2 = entityIn.height + 0.5F - (flag ? 0.25F : 0.0F);
         int i = "deadmau5".equals(str) ? -10 : 0;
         this.drawDepthNameplate(this.getFontRendererFromRenderManager(), str, (float)x, (float)y + f2, (float)z, i, f, f1, flag1, flag, healthScale);
      }

   }

   private void drawDepthNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, float healthScale) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(-0.025F, -0.025F, 0.025F);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      int i = fontRendererIn.getStringWidth(str) / 2;
      GlStateManager.disableTexture2D();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      Color drawColour = Left2MineClientUtilities.getColour((double)healthScale);
      GlStateManager.depthMask(true);
      fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, drawColour.getRGB());
      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   public void renderRightArm(AbstractClientPlayer clientPlayer) {
      float f = 1.0F;
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      float f1 = 0.0625F;
      ModelPlayer modelplayer = this.getMainModel();
      this.setModelVisibilities(clientPlayer);
      GlStateManager.enableBlend();
      modelplayer.swingProgress = 0.0F;
      modelplayer.isSneak = false;
      modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
      modelplayer.bipedRightArm.rotateAngleX = 0.0F;
      modelplayer.bipedRightArm.render(0.0625F);
      modelplayer.bipedRightArmwear.rotateAngleX = 0.0F;
      modelplayer.bipedRightArmwear.render(0.0625F);
      GlStateManager.disableBlend();
   }

   public void renderLeftArm(AbstractClientPlayer clientPlayer) {
      float f = 1.0F;
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      float f1 = 0.0625F;
      ModelPlayer modelplayer = this.getMainModel();
      this.setModelVisibilities(clientPlayer);
      GlStateManager.enableBlend();
      modelplayer.isSneak = false;
      modelplayer.swingProgress = 0.0F;
      modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
      modelplayer.bipedLeftArm.rotateAngleX = 0.0F;
      modelplayer.bipedLeftArm.render(0.0625F);
      modelplayer.bipedLeftArmwear.rotateAngleX = 0.0F;
      modelplayer.bipedLeftArmwear.render(0.0625F);
      GlStateManager.disableBlend();
   }

   protected void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z) {
      if (entityLivingBaseIn.isEntityAlive() && entityLivingBaseIn.isPlayerSleeping()) {
         super.renderLivingAt(entityLivingBaseIn, x + (double)entityLivingBaseIn.renderOffsetX, y + (double)entityLivingBaseIn.renderOffsetY, z + (double)entityLivingBaseIn.renderOffsetZ);
      } else {
         super.renderLivingAt(entityLivingBaseIn, x, y, z);
      }

   }

   protected void applyRotations(AbstractClientPlayer entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
      if (entityLiving.isEntityAlive() && entityLiving.isPlayerSleeping()) {
         GlStateManager.rotate(entityLiving.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
      }

      IEquip equip = (IEquip)entityLiving.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
      if (entityLiving.isEntityAlive() && equip.getLying()) {
         if (equip.getPinned()) {
            super.applyRotations(entityLiving, p_77043_2_, MathHelper.wrapDegrees(equip.getAnimationAngle() + 180.0F), partialTicks);
         } else {
            super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
         }

         GlStateManager.translate(0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -1.0F, 0.0F);
      } else if (entityLiving.isEntityAlive() && equip.getPinned()) {
         super.applyRotations(entityLiving, p_77043_2_, MathHelper.wrapDegrees(equip.getAnimationAngle() + 180.0F), partialTicks);
         GlStateManager.translate(0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -1.0F, 0.0F);
         GlStateManager.translate(0.0F, 0.0F, 1.0F);
      } else if (entityLiving.isElytraFlying()) {
         super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
         float f = (float)entityLiving.getTicksElytraFlying() + partialTicks;
         float f1 = MathHelper.clamp(f * f / 100.0F, 0.0F, 1.0F);
         GlStateManager.rotate(f1 * (-90.0F - entityLiving.rotationPitch), 1.0F, 0.0F, 0.0F);
         Vec3d vec3d = entityLiving.getLook(partialTicks);
         double d0 = entityLiving.motionX * entityLiving.motionX + entityLiving.motionZ * entityLiving.motionZ;
         double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
         if (d0 > 0.0D && d1 > 0.0D) {
            double d2 = (entityLiving.motionX * vec3d.x + entityLiving.motionZ * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
            double d3 = entityLiving.motionX * vec3d.z - entityLiving.motionZ * vec3d.x;
            GlStateManager.rotate((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / 3.1415927F, 0.0F, 1.0F, 0.0F);
         }
      } else {
         super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
      }

   }

   public void myRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!MinecraftForge.EVENT_BUS.post(new Pre(entity, this, partialTicks, x, y, z))) {
         IEquip equip = (IEquip)entity.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         GlStateManager.pushMatrix();
         GlStateManager.disableCull();
         if (equip.isPuked()) {
            float tintAdj = 0.2F;
            GlStateManager.color(tintAdj, 0.8F, tintAdj - 0.15F, 1.0F);
         }

         this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
         boolean shouldSit = entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit();
         this.mainModel.isRiding = shouldSit;
         this.mainModel.isChild = entity.isChild();

         try {
            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float f2 = f1 - f;
            float f8;
            if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase) {
               EntityLivingBase entitylivingbase = (EntityLivingBase)entity.getRidingEntity();
               f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
               f2 = f1 - f;
               f8 = MathHelper.wrapDegrees(f2);
               if (f8 < -85.0F) {
                  f8 = -85.0F;
               }

               if (f8 >= 85.0F) {
                  f8 = 85.0F;
               }

               f = f1 - f8;
               if (f8 * f8 > 2500.0F) {
                  f += f8 * 0.2F;
               }

               f2 = f1 - f;
            }

            float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            this.renderLivingAt(entity, x, y, z);
            f8 = this.handleRotationFloat(entity, partialTicks);
            this.applyRotations(entity, f8, f, partialTicks);
            float f4 = this.prepareScale(entity, partialTicks);
            float f5 = 0.0F;
            float f6 = 0.0F;
            if (!entity.isRiding()) {
               f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
               f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
               if (entity.isChild()) {
                  f6 *= 3.0F;
               }

               if (f5 > 1.0F) {
                  f5 = 1.0F;
               }

               f2 = f1 - f;
            }

            if (entity.isEntityAlive() && equip.getLying() && !equip.getPinned()) {
               f7 += 75.0F;
            }

            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
            this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
            boolean flag1;
            if (this.renderOutlines) {
               flag1 = this.setScoreTeamColor(entity);
               GlStateManager.enableColorMaterial();
               GlStateManager.enableOutlineMode(this.getFontRendererFromRenderManager().getColorCode("c".charAt(0)));
               if (!this.renderMarker) {
                  this.renderModel(entity, f6, f5, f8, f2, f7, f4);
               }

               if (!(entity instanceof EntityPlayer) || !entity.isSpectator()) {
                  this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
               }

               GlStateManager.disableOutlineMode();
               GlStateManager.disableColorMaterial();
               if (flag1) {
                  this.unsetScoreTeamColor();
               }
            } else {
               flag1 = this.setDoRenderBrightness(entity, partialTicks);
               this.renderModel(entity, f6, f5, f8, f2, f7, f4);
               if (flag1) {
                  this.unsetBrightness();
               }

               GlStateManager.depthMask(true);
               if (!(entity instanceof EntityPlayer) || !entity.isSpectator()) {
                  this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
               }
            }

            GlStateManager.disableRescaleNormal();
         } catch (Exception var21) {
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GlStateManager.enableTexture2D();
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
         GlStateManager.enableCull();
         GlStateManager.popMatrix();
         if (!this.renderOutlines) {
            this.renderName(entity, x, y, z);
         }

         MinecraftForge.EVENT_BUS.post(new Post(entity, this, partialTicks, x, y, z));
      }
   }
}
