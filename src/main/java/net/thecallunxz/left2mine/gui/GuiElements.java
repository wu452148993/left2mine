package net.thecallunxz.left2mine.gui;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.init.InitItems;
import net.thecallunxz.left2mine.items.usable.ItemFirstAid;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;
import net.thecallunxz.left2mine.util.Left2MineClientUtilities;
import org.lwjgl.opengl.GL11;

public class GuiElements extends Gui {
   private static final double MOVE_TICKS = 4.0D;
   private int reloadProgress;
   private int lastReloadProgress;
   private double realReloadProgress;
   public static ArrayList<GuiHurtArrow> arrows = new ArrayList();
   public static ArrayList<GuiTipHandler> tips = new ArrayList();
   private ItemStack flash = null;
   private ItemStack flashlight = null;
   private float slowDiff = 0.0F;
   private static final ResourceLocation crosshairGui = new ResourceLocation("left2mine:textures/gui/crosshair.png");
   private static final ResourceLocation hurtarrowGui = new ResourceLocation("left2mine:textures/gui/hurtarrow.png");
   private static final ResourceLocation warniconGui = new ResourceLocation("left2mine:textures/gui/warnicon.png");
   private final Minecraft mc = Minecraft.getMinecraft();

   public static boolean isReloading(World world, NBTTagCompound nbt) {
      return nbt.getLong("reloadTime") >= world.getTotalWorldTime();
   }

   @SubscribeEvent
   public void displayGui(RenderGameOverlayEvent event) {
      ScaledResolution sr = new ScaledResolution(this.mc);
      int xPos = sr.getScaledWidth() / 2;
      int yPos = sr.getScaledHeight() / 2;
      FontRenderer fontRender = this.mc.fontRenderer;
      if (this.mc.player.getHeldItemMainhand().getItem() instanceof ItemGun && event.getType() == ElementType.CROSSHAIRS) {
         event.setCanceled(true);
      }

      if (!this.mc.player.capabilities.allowEdit && (event.getType() == ElementType.HEALTH || event.getType() == ElementType.FOOD)) {
         event.setCanceled(true);
      }

      if (!event.isCancelable() && event.getType() == ElementType.EXPERIENCE) {
         float realHP;
         int colour;
         if (!this.mc.player.capabilities.allowEdit && !this.mc.player.isSpectator() && !this.mc.player.isDead) {
            float div = 1.0F;
            IEquip equip = (IEquip)this.mc.player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            String healthString = "[";
            if (equip.getLying()) {
               div = 3.0F;
            }

            realHP = this.mc.player.getHealth() / div;
            float tempHP = this.mc.player.getAbsorptionAmount() / div;
            float emptyHP = (float)(20 - ((int)realHP + (int)tempHP + 1));
            float healthScale = (realHP + tempHP) / 20.0F;
            if (equip.getLying()) {
               for(colour = 0; colour < (int)realHP + (int)tempHP + 1; ++colour) {
                  healthString = healthString + "||";
               }

               for(colour = 0; colour < (int)emptyHP; ++colour) {
                  healthString = healthString + " ";
               }
            } else {
               for(colour = 0; colour < (int)realHP; ++colour) {
                  healthString = healthString + "||";
               }

               for(colour = 0; colour < (int)tempHP; ++colour) {
                  healthString = healthString + "..";
               }

               for(colour = 0; colour < (int)emptyHP; ++colour) {
                  healthString = healthString + " ";
               }

               if ((int)realHP + (int)tempHP < 20) {
                  healthString = healthString + " ";
               }
            }

            healthString = healthString + "]";
            colour = ((int)realHP + (int)tempHP) * 5;
            if (equip.getLying()) {
               colour += 5;
            }

            if (colour == 0) {
               colour = 1;
            }

            this.drawStringRight(fontRender, healthString, xPos - 2, sr.getScaledHeight() - 40, Left2MineClientUtilities.getColour((double)healthScale).getRGB());
            this.drawString(fontRender, "+ " + colour, xPos - 90, sr.getScaledHeight() - 50, Left2MineClientUtilities.getColour((double)healthScale).getRGB());
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

         int percentDone;
         if (!arrows.isEmpty()) {
            for(percentDone = 0; percentDone < arrows.size(); ++percentDone) {
               GuiHurtArrow arrow = (GuiHurtArrow)arrows.get(percentDone);
               this.mc.renderEngine.bindTexture(hurtarrowGui);
               EntityPlayer player = this.mc.player;
               double hitX = arrow.getX();
               double hitY = arrow.getY();
               double hitZ = arrow.getZ();
               double dZ = hitZ - player.posZ;
               double dX = hitX - player.posX;
               double degrees = Math.atan(dZ / dX) * 180.0D / 3.141592653589793D;
               if (dX < 0.0D) {
                  degrees = 180.0D - Math.atan(dZ / -dX) * 180.0D / 3.141592653589793D;
               }

               float playerRotation = player.rotationYaw % 360.0F - 90.0F;
               float rotation = (float)(degrees - (double)playerRotation + 180.0D);
               GlStateManager.pushMatrix();
               GlStateManager.translate((float)(xPos - 128), (float)(yPos - 128), 0.0F);
               GlStateManager.translate(128.0F, 128.0F, 0.0F);
               GlStateManager.rotate(rotation, 0.0F, 0.0F, 1.0F);
               GlStateManager.translate(-128.0F, -128.0F, 0.0F);
               this.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
               GlStateManager.popMatrix();
            }
         }

         int storage;
         if (this.mc.gameSettings.thirdPersonView == 0 && this.mc.player.getHeldItemMainhand().getItem() instanceof ItemGun) {
            ItemGun item = (ItemGun)this.mc.player.getHeldItemMainhand().getItem();
            EntityPlayer player = this.mc.player;
            NBTTagCompound nbt = player.getHeldItemMainhand().getTagCompound();
            if (player.getHeldItemMainhand().hasTagCompound()) {
               if (isReloading(player.getEntityWorld(), player.getHeldItemMainhand().getTagCompound())) {
                  this.drawStringRight(fontRender, I18n.format("gui.reloading", new Object[0]), xPos + 90, sr.getScaledHeight() - 50, 16777215);
               }

               realHP = Left2MineClientUtilities.calculateAccuracyClient(item, player, player.getHeldItemMainhand());
               int move = Math.max(0, (int)(realHP * 3.0F));
               if (nbt.getBoolean("justShot")) {
                  ++move;
               }

               this.mc.renderEngine.bindTexture(crosshairGui);
               drawModalRectWithCustomSizedTexture(xPos, yPos, 1.0F, 1.0F, 1, 1, 16.0F, 16.0F);
               drawModalRectWithCustomSizedTexture(xPos, yPos + move, 1.0F, 1.0F, 1, 4, 16.0F, 16.0F);
               drawModalRectWithCustomSizedTexture(xPos, yPos - move - 3, 1.0F, 1.0F, 1, 4, 16.0F, 16.0F);
               drawModalRectWithCustomSizedTexture(xPos + move, yPos, 1.0F, 1.0F, 4, 1, 16.0F, 16.0F);
               drawModalRectWithCustomSizedTexture(xPos - move - 3, yPos, 1.0F, 1.0F, 4, 1, 16.0F, 16.0F);
               int ammo = Math.max(0, player.getHeldItemMainhand().getTagCompound().getInteger("ammo"));
               storage = Math.max(0, player.getHeldItemMainhand().getTagCompound().getInteger("storage"));
               colour = 16777215;
               if (ammo == 0 && storage == 0 && !item.getGun().infinite) {
                  colour = 13369344;
               }

               if (!item.getGun().infinite) {
                  this.drawStringRight(fontRender, "[ " + ammo + " | " + storage + " ]", xPos + 90, sr.getScaledHeight() - 40, colour);
               } else {
                  this.drawStringRight(fontRender, "[ " + ammo + " ]", xPos + 90, sr.getScaledHeight() - 40, colour);
               }
            }
         }

         if (Main.inSurvivalGame) {
            long currentTime = Minecraft.getMinecraft().world.getTotalWorldTime() - Main.gameStartTime;
            long bestTime = Main.bestSurvivalTime;
            String currentTimeStr = this.getTimeFromLong(currentTime);
            String bestTimeStr = this.getTimeFromLong(bestTime);
            storage = this.getMedalFromTime(currentTime);
            colour = this.getMedalFromTime(bestTime);
            String medalName = this.getMedalNameFromTime(bestTime);
            if (!Main.survivalStarted) {
               currentTimeStr = "00:00.00";
               bestTimeStr = this.getTimeFromLong(bestTime);
               storage = this.getMedalFromTime(0L);
               colour = this.getMedalFromTime(bestTime);
               medalName = this.getMedalNameFromTime(bestTime);
            } else if (currentTime > bestTime) {
               bestTimeStr = currentTimeStr;
               colour = this.getMedalFromTime(currentTime);
               medalName = this.getMedalNameFromTime(currentTime);
            }

            if (Main.lastSurvivalTime != 0L) {
               currentTimeStr = this.getTimeFromLong(Main.lastSurvivalTime);
               storage = this.getMedalFromTime(Main.lastSurvivalTime);
               if (Main.lastSurvivalTime > bestTime) {
                  bestTimeStr = currentTimeStr;
                  colour = this.getMedalFromTime(Main.lastSurvivalTime);
                  medalName = this.getMedalNameFromTime(Main.lastSurvivalTime);
               }
            }

            GlStateManager.pushMatrix();
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            this.drawString(fontRender, "  [" + currentTimeStr + "]", 10, 10, storage);
            this.drawString(fontRender, "  [" + bestTimeStr + "]", 10, 25, colour);
            GlStateManager.scale(1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
            this.drawString(fontRender, I18n.format("gui.currentTime", new Object[0]), 10, 10, storage);
            this.drawString(fontRender, I18n.format("gui.worldBest", new Object[0]), 10, 40, colour);
            this.drawCenteredString(fontRender, medalName, 85, 70, colour);
         }

         if (this.mc.player.getHeldItemMainhand().getItem() instanceof ItemFirstAid && this.mc.player.getItemInUseCount() > 0) {
            percentDone = 100 - this.mc.player.getItemInUseCount();
            this.drawCenteredString(fontRender, I18n.format("gui.healing", new Object[0]) + " - " + percentDone + "%", xPos, yPos + 10, 65280);
            String bar = "[";
            int barCount = (int)Math.floor((double)(percentDone / 2));

            for(int i = 0; i < barCount; ++i) {
               bar = bar + "|";
            }

            this.drawCenteredString(fontRender, bar + "]", xPos, yPos + 20, 65280);
         }

      }
   }

   @SubscribeEvent
   public void renderWorldTip(RenderWorldLastEvent event) {
      FontRenderer fontRender = this.mc.fontRenderer;
      if (!tips.isEmpty()) {
         for(int i = 0; i < tips.size(); ++i) {
            GuiTipHandler tip = (GuiTipHandler)tips.get(i);
            RenderManager renderer = this.mc.getRenderManager();
            float f = renderer.playerViewY;
            float f1 = renderer.playerViewX;
            this.drawDepthNameplate(fontRender, tip.getText(), tip.getX() + 0.5F, tip.getY() + 0.5F, tip.getZ() + 0.5F, 0, f, f1, false, false, renderer.viewerPosX, renderer.viewerPosY, renderer.viewerPosZ);
         }
      }

   }

   private void drawDepthNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, double playerX, double playerY, double playerZ) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(-playerX, -playerY, -playerZ);
      GlStateManager.translate(x, y, z);
      GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(-0.025F, -0.025F, 0.025F);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      GlStateManager.disableDepth();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.depthMask(true);
      ArrayList<String> strList = new ArrayList();
      strList = this.splitString(strList, str);

      for(int i = 0; i < strList.size(); ++i) {
         fontRendererIn.drawString((String)strList.get(i), -fontRendererIn.getStringWidth((String)strList.get(i)) / 2, verticalShift + 10 * i, 16777215);
      }

      this.mc.renderEngine.bindTexture(warniconGui);
      GlStateManager.scale(0.1F, 0.1F, 0.1F);
      this.drawTexturedModalRect(-135, -250, 0, 0, 256, 256);
      GlStateManager.enableDepth();
      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   private ArrayList<String> splitString(ArrayList<String> strList, String str) {
      if (str.contains(";;") && str.length() > 2) {
         String newStr1 = str.substring(0, str.indexOf(";;"));
         String newStr2 = str.substring(str.indexOf(";;") + 2);
         strList.add(newStr1);
         strList.add(newStr2);
      } else {
         strList.add(str);
      }

      return strList;
   }

   private int getMedalFromTime(long time) {
      if (time < 2400L) {
         return 16777215;
      } else if (time < 4800L) {
         return 13467442;
      } else if (time < 7200L) {
         return 12632256;
      } else if (time < 9600L) {
         return 14329120;
      } else {
         return time < 12000L ? 15066338 : 12186367;
      }
   }

   private String getMedalNameFromTime(long time) {
      if (time < 2400L) {
         return "";
      } else if (time < 4800L) {
         return I18n.format("gui.bronze", new Object[0]);
      } else if (time < 7200L) {
         return I18n.format("gui.silver", new Object[0]);
      } else if (time < 9600L) {
         return I18n.format("gui.gold", new Object[0]);
      } else {
         return time < 12000L ? I18n.format("gui.platinum", new Object[0]) : I18n.format("gui.diamond", new Object[0]);
      }
   }

   private String getTimeFromLong(long time) {
      long totalSeconds = (long)Math.floor((double)(time / 20L));
      int miliSeconds = (int)(time % 20L) * 5;
      int mins = (int)totalSeconds / 60;
      int remainder = (int)totalSeconds - mins * 60;
      String strMins = (mins < 10 ? "0" : "") + mins;
      String strSecs = (remainder < 10 ? "0" : "") + remainder;
      String strMili = (miliSeconds < 10 ? "0" : "") + miliSeconds;
      return "" + strMins + ":" + strSecs + "." + strMili;
   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      this.lastReloadProgress = this.reloadProgress;
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (player != null) {
         ItemStack heldItem = player.getHeldItemMainhand();
         boolean hasGun = heldItem.getItem() instanceof ItemGun;
         if (hasGun && heldItem.hasTagCompound()) {
            if (isReloading(player.getEntityWorld(), player.getHeldItemMainhand().getTagCompound())) {
               if ((double)this.reloadProgress < 4.0D) {
                  ++this.reloadProgress;
               }
            } else if (this.reloadProgress > 0) {
               --this.reloadProgress;
            }
         }

         int i;
         if (!arrows.isEmpty()) {
            for(i = 0; i < arrows.size(); ++i) {
               GuiHurtArrow arrow = (GuiHurtArrow)arrows.get(i);
               arrow.tick();
               if (arrow.getLifeLeft() <= 0) {
                  arrows.remove(i);
               }
            }
         }

         if (!tips.isEmpty()) {
            for(i = 0; i < tips.size(); ++i) {
               GuiTipHandler tip = (GuiTipHandler)tips.get(i);
               tip.tick();
               if (tip.getLifeLeft() == 0) {
                  tips.remove(i);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onRenderHand(RenderHandEvent event) {
      if (this.mc.gameSettings.thirdPersonView == 0) {
         if (this.flashlight == null) {
            this.flashlight = new ItemStack(InitItems.flashlight, 1);
         }

         IBakedModel lightmodel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(this.flashlight);
         if (this.flash == null) {
            this.flash = new ItemStack(InitItems.flash, 1);
         }

         IBakedModel flashmodel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(this.flash);
         this.realReloadProgress = (double)((float)this.lastReloadProgress + (float)(this.reloadProgress - this.lastReloadProgress) * (this.lastReloadProgress != 0 && (double)this.lastReloadProgress != 4.0D ? event.getPartialTicks() : 0.0F)) / 4.0D;
         ItemStack heldItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
         boolean hasGun = heldItem.getItem() instanceof ItemGun;
         if (hasGun && Minecraft.getMinecraft().player.swingProgressInt == 0 && heldItem.hasTagCompound()) {
            GlStateManager.rotate((float)(45.0D * this.realReloadProgress), 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0D, -0.1D * this.realReloadProgress, 0.5D * this.realReloadProgress);
            GunProperties gun = ((ItemGun)heldItem.getItem()).getGun();
            if (heldItem.getTagCompound().getBoolean("justShot")) {
               GlStateManager.translate(0.0D, 0.0D, (double)(gun.recoil / 50.0F));
               GlStateManager.pushMatrix();
               GlStateManager.disableLighting();
               GlStateManager.translate(1.0D, -0.87D, -3.72D);
               GlStateManager.translate(gun.flashXOffset, gun.flashYOffset, gun.flashZOffset);
               GlStateManager.scale(2.0F, 2.0F, 1.0F);
               this.rotateArm(event.getPartialTicks());
               Left2MineClientUtilities.renderModel(flashmodel);
               GlStateManager.enableLighting();
               GlStateManager.popMatrix();
            }
         }

         int lightVar = Left2MineClientUtilities.getBrightness(Minecraft.getMinecraft().player);
         float wantedDiff = (float)(15 - lightVar);
         if (wantedDiff > this.slowDiff) {
            this.slowDiff = Math.min(this.slowDiff + 0.06F, wantedDiff);
         }

         if (wantedDiff < this.slowDiff) {
            this.slowDiff = Math.max(this.slowDiff - 0.06F, wantedDiff);
         }

         if (Main.flashlight) {
            float alpha = 0.25F + this.slowDiff * 0.05F;
            GL11.glDisable(2896);
            Minecraft.getMinecraft().entityRenderer.disableLightmap();
            GL11.glDisable(3042);
            GL11.glPushMatrix();
            GL11.glPushAttrib(16384);
            if (hasGun && Minecraft.getMinecraft().player.swingProgressInt != 0 && heldItem.hasTagCompound()) {
               GlStateManager.rotate((float)(45.0D * this.realReloadProgress), 0.0F, 1.0F, 0.0F);
               GlStateManager.translate(0.0D, -0.1D * this.realReloadProgress, 0.5D * this.realReloadProgress);
            }

            GL11.glEnable(3042);
            GL11.glDepthMask(false);
            GL11.glBlendFunc(774, 770);
            GlStateManager.translate(-0.33D, -0.33D, -3.0D);
            GlStateManager.scale(3.5D, 3.5D, 1.0D);
            this.rotateArm(event.getPartialTicks());
            Left2MineClientUtilities.renderLightModel(lightmodel, (int)(alpha * this.slowDiff * 17.0F));
            Left2MineClientUtilities.renderLightModel(lightmodel, (int)(alpha * 255.0F));
            GL11.glBlendFunc(770, 771);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            GL11.glEnable(2896);
         }
      }

   }

   private void rotateArm(float partialTicks) {
      EntityPlayerSP entityplayersp = this.mc.player;
      float f = entityplayersp.prevRenderArmPitch + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * partialTicks;
      float f1 = entityplayersp.prevRenderArmYaw + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * partialTicks;
      GlStateManager.translate(0.0F, (entityplayersp.rotationPitch - f) * 0.003F, 0.0F);
      GlStateManager.translate(-(entityplayersp.rotationYaw - f1) * 0.003F, 0.0F, 0.0F);
   }

   public void drawStringRight(FontRenderer fontRendererIn, String text, int x, int y, int color) {
      fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text)), (float)y, color);
   }
}
