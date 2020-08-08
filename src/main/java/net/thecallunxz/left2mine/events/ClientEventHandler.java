package net.thecallunxz.left2mine.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.config.Left2MineConfig;
import net.thecallunxz.left2mine.entities.mobs.IRagdollEntities;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntitySmoker;
import net.thecallunxz.left2mine.entities.particle.EntitySmokerFX;
import net.thecallunxz.left2mine.gui.GuiCustomMainMenu;
import net.thecallunxz.left2mine.init.InitKeybinding;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.models.ModelFakeWall;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.server.FlashlightMessage;
import net.thecallunxz.left2mine.ragdoll.Ragdoll;
import net.thecallunxz.left2mine.render.RenderCustomPlayer;
import net.thecallunxz.left2mine.render.RenderSmokerTongue;
import net.thecallunxz.left2mine.util.Left2MineClientUtilities;
import net.thecallunxz.left2mine.util.Left2MineUtilities;
import org.lwjgl.opengl.GL11;

public class ClientEventHandler {
   private ArrayList<Ragdoll> corpseList = new ArrayList();
   private ISound menuMusic = null;
   private long menuTime = 0L;
   private Date date = new Date();

   public void addCorpse(Ragdoll r) {
      this.corpseList.add(r);
   }

   public int corpseListSize() {
      return this.corpseList.size();
   }

   public void removeCorpse(int id) {
      this.corpseList.remove(id);
   }

   public void corpseListClear() {
      this.corpseList.clear();
   }

   @SubscribeEvent
   public void onWorldRenderPre(RenderWorldLastEvent event) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
      double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)event.getPartialTicks();
      double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)event.getPartialTicks();
      double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)event.getPartialTicks();
      GlStateManager.pushMatrix();
      GL11.glPushMatrix();
      RenderHelper.enableStandardItemLighting();
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      GL11.glTranslated(-x, -y, -z);

      for(int i = 0; i < this.corpseList.size(); ++i) {
         Ragdoll corpse = (Ragdoll)this.corpseList.get(i);
         corpse.display(event.getPartialTicks());
      }

      RenderHelper.disableStandardItemLighting();
      GlStateManager.popMatrix();
      GL11.glPopMatrix();
   }

   @SubscribeEvent
   public void onTextureStitch(Pre event) {
      event.getMap().registerSprite(Main.tongueParticle);
      event.getMap().registerSprite(Main.tickParticle);
      event.getMap().registerSprite(Main.crossParticle);
   }

   @SubscribeEvent
   public void onFOVUpdate(FOVUpdateEvent event) {
      if (!event.getEntity().capabilities.allowEdit) {
         if (event.getEntity().isSprinting()) {
            event.setNewfov(1.15F);
         } else {
            event.setNewfov(1.0F);
         }
      }

   }

   @SubscribeEvent
   public void onVanillaMusic(PlaySoundEvent event) {
      if (event.getSound().getSoundLocation().toString().length() >= 9 && event.getSound().getCategory() == SoundCategory.MUSIC && event.getSound().getSoundLocation().toString().contains("minecraft")) {
         event.setResultSound((ISound)null);
      }

   }

   @SubscribeEvent
   public void onGuiOpen(GuiOpenEvent event) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (event.getGui() instanceof GuiContainer && !player.capabilities.allowEdit) {
         event.setCanceled(true);
      }

      if (event.getGui() instanceof GuiMainMenu) {
         event.setGui(new GuiCustomMainMenu());
         this.date = new Date();
         this.menuTime = this.date.getTime() + 100L;
      }

      if (event.getGui() instanceof GuiGameOver) {
         Minecraft.getMinecraft().player.respawnPlayer();
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      if (this.menuTime != 0L && this.menuTime <= this.date.getTime()) {
         Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(InitSounds.menu_music.getSoundName(), SoundCategory.MUSIC, 1.0F, 1.0F, true, 1, AttenuationType.NONE, 0.0F, 0.0F, 0.0F));
         this.menuTime = 0L;
      } else if (this.menuTime != 0L) {
         this.date = new Date();
      }

      if (Minecraft.getMinecraft().getRenderViewEntity() != null) {
         Minecraft.getMinecraft().gameSettings.gammaSetting = 0.75F;

         for(int i = 0; i < this.corpseList.size(); ++i) {
            Ragdoll corpse = (Ragdoll)this.corpseList.get(i);
            corpse.update();
            if (corpse.getDistanceToPlayer() > (float)Ragdoll.despawnRange || corpse.ticksExisted > Left2MineConfig.corpseDespawnAge) {
               this.corpseList.remove(i);
               --i;
            }
         }

         EntityPlayer player = Minecraft.getMinecraft().player;
         if (!player.capabilities.allowEdit && !player.isSpectator()) {
            float playerHP = Math.min(player.getHealth() + player.getAbsorptionAmount(), 20.0F);
            double motionPlus;
            if (playerHP < 5.0F && player.onGround) {
               motionPlus = (double)(playerHP / 20.0F) * 0.35D;
               player.motionX *= 0.65D + motionPlus;
               player.motionZ *= 0.65D + motionPlus;
            } else if (playerHP < 10.0F && player.onGround) {
               motionPlus = (double)(playerHP / 20.0F) * 0.1D;
               player.motionX *= 0.9D + motionPlus;
               player.motionZ *= 0.9D + motionPlus;
            }
         }

         if (!player.isPotionActive(InitPotions.smoker_tongued) && player.stepHeight == 1.1F) {
            player.stepHeight = 0.6F;
         }

         Left2MineClientUtilities.playEquipSound();
         Left2MineClientUtilities.disableMovement(player.isPotionActive(InitPotions.incapacitated) || Left2MineUtilities.isPinnedPotionActive(player));
         Left2MineClientUtilities.disableHandSwap(!player.capabilities.allowEdit);
         Left2MineClientUtilities.disableSneak(player.isSpectator() || Left2MineUtilities.isPinnedPotionActive(player));
         Left2MineClientUtilities.forceCamera(player.isSpectator() || Left2MineUtilities.isPinnedPotionActive(player));
         if (InitKeybinding.flashlight.isPressed() && !player.isSpectator()) {
            if (Main.flashlight) {
               Left2MinePacket.INSTANCE.sendToServer(new FlashlightMessage(0));
            } else {
               Left2MinePacket.INSTANCE.sendToServer(new FlashlightMessage(1));
            }

            Main.flashlight = !Main.flashlight;
         }

      }
   }

   @SubscribeEvent
   public void onRenderLiving(net.minecraftforge.client.event.RenderLivingEvent.Pre event) {
      if (event.getEntity() instanceof IRagdollEntities && event.getEntity().deathTime > 0) {
         event.setCanceled(true);
         event.getEntity().setDead();
      }

      if (event.getEntity() instanceof EntitySmoker && !event.getEntity().isDead && event.getEntity().world.getTotalWorldTime() % 5L == 0L) {
         Particle origBile = new EntitySmokerFX(event.getEntity().world, event.getEntity().posX, event.getEntity().posY + 1.5D, event.getEntity().posZ, 0.0D, 0.01D, 0.0D, 25.0D);
         Minecraft.getMinecraft().effectRenderer.addEffect(origBile);
      }

   }

   @SubscribeEvent
   public void tooltipEvent(ItemTooltipEvent event) {
      if (event.getItemStack().getItem() instanceof ItemGun) {
         List<String> tooltip = event.getToolTip();
         tooltip.clear();
         tooltip.add(event.getItemStack().getDisplayName());
      }

   }

   @SubscribeEvent
   public void onPlayerRender(net.minecraftforge.client.event.RenderPlayerEvent.Pre event) {
      event.setCanceled(true);
      RenderCustomPlayer customplayer = new RenderCustomPlayer(Minecraft.getMinecraft().getRenderManager(), event.getRenderer());
      customplayer.doRender((AbstractClientPlayer)event.getEntity(), event.getX(), event.getY(), event.getZ(), event.getEntity().getRotationYawHead(), event.getPartialRenderTick());
   }

   @SubscribeEvent
   public void onRenderLiving(net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre event) {
      if (event.getEntity() instanceof IRagdollEntities && event.getEntity().deathTime > 0) {
         event.setCanceled(true);
         event.getEntity().setDead();
      }

   }

   @SubscribeEvent
   public void onLivingUpdate(LivingUpdateEvent event) {
      if (event.getEntity().getEntityWorld().isRemote && event.getEntity() instanceof EntitySmoker) {
         EntitySmoker smoker = (EntitySmoker)event.getEntity();
         if (smoker.isSmoked()) {
            EntityPlayer tetheredPlayer = smoker.world.getPlayerEntityByUUID(smoker.getSmokedId());
            if (tetheredPlayer != null) {
               IEquip equip = (IEquip)tetheredPlayer.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
               Vec3d smokerVec = smoker.getPositionVector().addVector(0.0D, (double)smoker.getEyeHeight() + 0.1D, 0.0D);
               Vec3d victimVec = tetheredPlayer.getPositionVector();
               if (equip.getLying()) {
                  victimVec = victimVec.addVector(0.0D, 1.0D, 0.0D);
               }

               double gap = 0.1D;
               double distance = smokerVec.distanceTo(victimVec);
               Vec3d newVec = victimVec.subtract(smokerVec).normalize().scale(gap);

               for(double covered = 0.0D; covered < distance; smokerVec = smokerVec.add(newVec)) {
                  Particle particle = new RenderSmokerTongue(smoker.world, smokerVec.x, smokerVec.y, smokerVec.z);
                  Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                  covered += gap;
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onModelBakeEvent(ModelBakeEvent event) {
      Object object = event.getModelRegistry().getObject(ModelFakeWall.variantTagNorth);
      IBakedModel existingModel;
      ModelFakeWall customModel;
      if (object instanceof IBakedModel) {
         existingModel = (IBakedModel)object;
         customModel = new ModelFakeWall(existingModel);
         event.getModelRegistry().putObject(ModelFakeWall.variantTagNorth, customModel);
      }

      object = event.getModelRegistry().getObject(ModelFakeWall.variantTagEast);
      if (object instanceof IBakedModel) {
         existingModel = (IBakedModel)object;
         customModel = new ModelFakeWall(existingModel);
         event.getModelRegistry().putObject(ModelFakeWall.variantTagEast, customModel);
      }

      object = event.getModelRegistry().getObject(ModelFakeWall.variantTagSouth);
      if (object instanceof IBakedModel) {
         existingModel = (IBakedModel)object;
         customModel = new ModelFakeWall(existingModel);
         event.getModelRegistry().putObject(ModelFakeWall.variantTagSouth, customModel);
      }

      object = event.getModelRegistry().getObject(ModelFakeWall.variantTagWest);
      if (object instanceof IBakedModel) {
         existingModel = (IBakedModel)object;
         customModel = new ModelFakeWall(existingModel);
         event.getModelRegistry().putObject(ModelFakeWall.variantTagWest, customModel);
      }

   }
}
