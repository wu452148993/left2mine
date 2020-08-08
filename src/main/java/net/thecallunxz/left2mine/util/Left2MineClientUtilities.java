package net.thecallunxz.left2mine.util;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.Status;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityHunter;
import net.thecallunxz.left2mine.gui.GuiCustomMainMenu;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.items.usable.ItemFirstAid;
import net.thecallunxz.left2mine.items.usable.ItemPills;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.items.weapons.ItemMolotov;
import net.thecallunxz.left2mine.items.weapons.ItemPipebomb;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;
import net.thecallunxz.left2mine.keybinding.KeyBindingDisable;
import net.thecallunxz.left2mine.keybinding.KeyBindingEnable;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.server.FlashlightMessage;
import org.lwjgl.opengl.GL11;

public class Left2MineClientUtilities {
   private static Item oldItem = null;

   public static void renderBox(int mode) {
      float s = 0.1F;
      GL11.glBegin(mode);
      GL11.glVertex3f(-s, -s, -s);
      GL11.glVertex3f(-s, s, -s);
      GL11.glVertex3f(s, s, -s);
      GL11.glVertex3f(s, -s, -s);
      GL11.glEnd();
      GL11.glBegin(mode);
      GL11.glVertex3f(-s, s, s);
      GL11.glVertex3f(-s, -s, s);
      GL11.glVertex3f(s, -s, s);
      GL11.glVertex3f(s, s, s);
      GL11.glEnd();
      GL11.glBegin(mode);
      GL11.glVertex3f(-s, s, -s);
      GL11.glVertex3f(-s, -s, -s);
      GL11.glVertex3f(-s, -s, s);
      GL11.glVertex3f(-s, s, s);
      GL11.glEnd();
      GL11.glBegin(mode);
      GL11.glVertex3f(s, -s, -s);
      GL11.glVertex3f(s, s, -s);
      GL11.glVertex3f(s, s, s);
      GL11.glVertex3f(s, -s, s);
      GL11.glEnd();
      GL11.glBegin(mode);
      GL11.glVertex3f(-s, s, -s);
      GL11.glVertex3f(-s, s, s);
      GL11.glVertex3f(s, s, s);
      GL11.glVertex3f(s, s, -s);
      GL11.glEnd();
      GL11.glBegin(mode);
      GL11.glVertex3f(-s, -s, s);
      GL11.glVertex3f(-s, -s, -s);
      GL11.glVertex3f(s, -s, -s);
      GL11.glVertex3f(s, -s, s);
      GL11.glEnd();
   }

   public static Color getColour(double power) {
      double H = power * 0.3D;
      double S = 1.0D;
      double B = 1.0D;
      return Color.getHSBColor((float)H, (float)S, (float)B);
   }

   public static int getNodeLinkerColour(ItemStack stack) {
      NBTTagCompound nbt = stack.getTagCompound();
      if (stack.hasTagCompound()) {
         int rgb = nbt.getInteger("rgbCustom");
         return rgb;
      } else {
         return Color.black.getRGB();
      }
   }

   public static NotificationModUpdateScreen initMenu(GuiCustomMainMenu guiMainMenu, GuiButton modButton) {
      NotificationModUpdateScreen notificationModUpdateScreen = new NotificationModUpdateScreen(modButton);
      notificationModUpdateScreen.setGuiSize(guiMainMenu.width, guiMainMenu.height);
      notificationModUpdateScreen.initGui();
      return notificationModUpdateScreen;
   }

   public static String renderMainMenu(GuiCustomMainMenu guiCustomMainMenu, FontRenderer font, int width, int height, String splashText) {
      Status status = ForgeVersion.getStatus();
      String line;
      if (status == Status.BETA || status == Status.BETA_OUTDATED) {
         line = I18n.format("forge.update.beta.1", new Object[]{TextFormatting.RED, TextFormatting.RESET});
         guiCustomMainMenu.drawString(font, line, (width - font.getStringWidth(line)) / 2, 4 + 0 * (font.FONT_HEIGHT + 1), -1);
         line = I18n.format("forge.update.beta.2", new Object[0]);
         guiCustomMainMenu.drawString(font, line, (width - font.getStringWidth(line)) / 2, 4 + 1 * (font.FONT_HEIGHT + 1), -1);
      }

      line = null;
      switch(status) {
      case OUTDATED:
      case BETA_OUTDATED:
         line = I18n.format("forge.update.newversion", new Object[]{ForgeVersion.getTarget()});
      default:
         if (line != null) {
            guiCustomMainMenu.drawString(font, line, width - font.getStringWidth(line) - 2, height - 2 * (font.FONT_HEIGHT + 1), -1);
         }

         return splashText;
      }
   }

   public static double toDegrees(double paramDouble) {
      return paramDouble * 180.0D / 3.141592653589793D;
   }

   public static IBakedModel getModel(ResourceLocation resource, int meta) {
      return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(new ItemStack(Item.getByNameOrId(resource.toString()), 1, meta));
   }

   public static void renderModel(IBakedModel model) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(-0.4F, -0.4F, -0.4F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.pushMatrix();
      ItemCameraTransforms.applyTransformSide(model.getItemCameraTransforms().getTransform(TransformType.FIRST_PERSON_RIGHT_HAND), false);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
      EnumFacing[] var3 = EnumFacing.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumFacing enumfacing = var3[var5];
         renderQuads(vertexbuffer, model.getQuads((IBlockState)null, enumfacing, 0L));
      }

      renderQuads(vertexbuffer, model.getQuads((IBlockState)null, (EnumFacing)null, 0L));
      tessellator.draw();
      GlStateManager.cullFace(CullFace.BACK);
      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      GlStateManager.popMatrix();
   }

   public static void renderLightModel(IBakedModel model, int alpha) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(-0.4F, -0.4F, -0.4F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.pushMatrix();
      ItemCameraTransforms.applyTransformSide(model.getItemCameraTransforms().getTransform(TransformType.FIRST_PERSON_RIGHT_HAND), false);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
      EnumFacing[] var4 = EnumFacing.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EnumFacing enumfacing = var4[var6];
         renderLightQuads(vertexbuffer, model.getQuads((IBlockState)null, enumfacing, 0L), alpha);
      }

      renderLightQuads(vertexbuffer, model.getQuads((IBlockState)null, (EnumFacing)null, 0L), alpha);
      tessellator.draw();
      GlStateManager.cullFace(CullFace.BACK);
      GlStateManager.popMatrix();
      Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.popMatrix();
   }

   public static int getBrightness(Entity ent) {
      BlockPos blockpos = new BlockPos(Math.floor(ent.posX), ent.posY, Math.floor(ent.posZ));
      World world = Minecraft.getMinecraft().world;
      int skyLightSub = world.calculateSkylightSubtracted(1.0F);
      int blockLight = world.getLightFor(EnumSkyBlock.BLOCK, blockpos);
      int skyLight = world.getLightFor(EnumSkyBlock.SKY, blockpos) - skyLightSub;
      return Math.max(blockLight, skyLight);
   }

   private static void renderQuads(BufferBuilder renderer, List<BakedQuad> quads) {
      int i = 0;

      for(int j = quads.size(); i < j; ++i) {
         LightUtil.renderQuadColor(renderer, (BakedQuad)quads.get(i), -1);
      }

   }

   private static void renderLightQuads(BufferBuilder renderer, List<BakedQuad> quads, int alpha) {
      int i = 0;
      int argb = (new Color(255, 255, 255, alpha)).getRGB();

      for(int j = quads.size(); i < j; ++i) {
         LightUtil.renderQuadColor(renderer, (BakedQuad)quads.get(i), argb);
      }

   }

   public static void disableMovement(boolean bool) {
      if (bool) {
         if (!(Minecraft.getMinecraft().gameSettings.keyBindForward instanceof KeyBindingDisable)) {
            Minecraft.getMinecraft().gameSettings.keyBindForward = new KeyBindingDisable(Minecraft.getMinecraft().gameSettings.keyBindForward);
         }

         if (!(Minecraft.getMinecraft().gameSettings.keyBindRight instanceof KeyBindingDisable)) {
            Minecraft.getMinecraft().gameSettings.keyBindRight = new KeyBindingDisable(Minecraft.getMinecraft().gameSettings.keyBindRight);
         }

         if (!(Minecraft.getMinecraft().gameSettings.keyBindBack instanceof KeyBindingDisable)) {
            Minecraft.getMinecraft().gameSettings.keyBindBack = new KeyBindingDisable(Minecraft.getMinecraft().gameSettings.keyBindBack);
         }

         if (!(Minecraft.getMinecraft().gameSettings.keyBindLeft instanceof KeyBindingDisable)) {
            Minecraft.getMinecraft().gameSettings.keyBindLeft = new KeyBindingDisable(Minecraft.getMinecraft().gameSettings.keyBindLeft);
         }

         if (!(Minecraft.getMinecraft().gameSettings.keyBindJump instanceof KeyBindingDisable)) {
            Minecraft.getMinecraft().gameSettings.keyBindJump = new KeyBindingDisable(Minecraft.getMinecraft().gameSettings.keyBindJump);
         }

         if (!(Minecraft.getMinecraft().gameSettings.keyBindSneak instanceof KeyBindingDisable)) {
            Minecraft.getMinecraft().gameSettings.keyBindSneak = new KeyBindingDisable(Minecraft.getMinecraft().gameSettings.keyBindSneak);
         }
      } else {
         if (Minecraft.getMinecraft().gameSettings.keyBindForward instanceof KeyBindingDisable) {
            Minecraft.getMinecraft().gameSettings.keyBindForward = new KeyBindingEnable(Minecraft.getMinecraft().gameSettings.keyBindForward);
         }

         if (Minecraft.getMinecraft().gameSettings.keyBindRight instanceof KeyBindingDisable) {
            Minecraft.getMinecraft().gameSettings.keyBindRight = new KeyBindingEnable(Minecraft.getMinecraft().gameSettings.keyBindRight);
         }

         if (Minecraft.getMinecraft().gameSettings.keyBindBack instanceof KeyBindingDisable) {
            Minecraft.getMinecraft().gameSettings.keyBindBack = new KeyBindingEnable(Minecraft.getMinecraft().gameSettings.keyBindBack);
         }

         if (Minecraft.getMinecraft().gameSettings.keyBindLeft instanceof KeyBindingDisable) {
            Minecraft.getMinecraft().gameSettings.keyBindLeft = new KeyBindingEnable(Minecraft.getMinecraft().gameSettings.keyBindLeft);
         }

         if (Minecraft.getMinecraft().gameSettings.keyBindJump instanceof KeyBindingDisable) {
            Minecraft.getMinecraft().gameSettings.keyBindJump = new KeyBindingEnable(Minecraft.getMinecraft().gameSettings.keyBindJump);
         }

         if (Minecraft.getMinecraft().gameSettings.keyBindSneak instanceof KeyBindingDisable) {
            Minecraft.getMinecraft().gameSettings.keyBindSneak = new KeyBindingEnable(Minecraft.getMinecraft().gameSettings.keyBindSneak);
         }
      }

   }

   public static void disableSneak(boolean bool) {
      if (bool) {
         if (!(Minecraft.getMinecraft().gameSettings.keyBindSneak instanceof KeyBindingDisable)) {
            Minecraft.getMinecraft().gameSettings.keyBindSneak = new KeyBindingDisable(Minecraft.getMinecraft().gameSettings.keyBindSneak);
         }
      } else if (Minecraft.getMinecraft().gameSettings.keyBindSneak instanceof KeyBindingDisable) {
         Minecraft.getMinecraft().gameSettings.keyBindSneak = new KeyBindingEnable(Minecraft.getMinecraft().gameSettings.keyBindSneak);
      }

   }

   public static void disableMouse(boolean bool) {
      if (bool) {
         if (!(Minecraft.getMinecraft().gameSettings.keyBindAttack instanceof KeyBindingDisable)) {
            Minecraft.getMinecraft().gameSettings.keyBindAttack = new KeyBindingDisable(Minecraft.getMinecraft().gameSettings.keyBindAttack);
         }

         if (!(Minecraft.getMinecraft().gameSettings.keyBindUseItem instanceof KeyBindingDisable)) {
            Minecraft.getMinecraft().gameSettings.keyBindUseItem = new KeyBindingDisable(Minecraft.getMinecraft().gameSettings.keyBindUseItem);
         }
      } else {
         if (Minecraft.getMinecraft().gameSettings.keyBindAttack instanceof KeyBindingDisable) {
            Minecraft.getMinecraft().gameSettings.keyBindAttack = new KeyBindingEnable(Minecraft.getMinecraft().gameSettings.keyBindAttack);
         }

         if (Minecraft.getMinecraft().gameSettings.keyBindUseItem instanceof KeyBindingDisable) {
            Minecraft.getMinecraft().gameSettings.keyBindUseItem = new KeyBindingEnable(Minecraft.getMinecraft().gameSettings.keyBindUseItem);
         }
      }

   }

   public static void forceCamera(boolean bool) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (!player.capabilities.allowEdit) {
         if (bool) {
            if (Main.flashlight) {
               Left2MinePacket.INSTANCE.sendToServer(new FlashlightMessage(0));
               Main.flashlight = false;
            }

            Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
         } else {
            Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
         }
      }

   }

   public static float calculateAccuracyClient(ItemGun item, EntityPlayer player, ItemStack stack) {
      GunProperties gun = item.getGun();
      float acc = gun.accuracy;
      GameSettings settings = Minecraft.getMinecraft().gameSettings;
      if (settings.keyBindForward.isKeyDown() || settings.keyBindLeft.isKeyDown() || settings.keyBindBack.isKeyDown() || settings.keyBindRight.isKeyDown()) {
         acc += 3.0F;
      }

      if (stack.hasTagCompound()) {
         acc += (float)stack.getTagCompound().getInteger("bloom") / 100.0F;
      }

      if (!player.onGround) {
         acc += 5.0F;
      }

      if (player.isSprinting()) {
         acc += 3.0F;
      }

      if (player.isSneaking()) {
         acc *= 0.7F;
      }

      return acc;
   }

   public static void playEquipSound() {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (player.getHeldItemMainhand().getItem() != oldItem) {
         if (player.getHeldItemMainhand().getItem() instanceof ItemGun) {
            ItemGun gun = (ItemGun)player.getHeldItemMainhand().getItem();
            gun.setClientNextShot(0L);
            player.world.playSound(player, player.posX, player.posY, player.posZ, gun.getGun().equipSound, SoundCategory.PLAYERS, 0.5F, 1.0F);
         }

         if (player.getHeldItemMainhand().getItem() instanceof ItemPills) {
            player.world.playSound(player, player.posX, player.posY, player.posZ, InitSounds.pills_deploy, SoundCategory.PLAYERS, 0.75F, 1.0F);
         }

         if (player.getHeldItemMainhand().getItem() instanceof ItemFirstAid) {
            player.world.playSound(player, player.posX, player.posY, player.posZ, InitSounds.misc_deploy, SoundCategory.PLAYERS, 0.75F, 1.0F);
         }

         if (player.getHeldItemMainhand().getItem() instanceof ItemPipebomb) {
            player.world.playSound(player, player.posX, player.posY, player.posZ, InitSounds.misc_deploy, SoundCategory.PLAYERS, 0.75F, 1.0F);
         }

         if (player.getHeldItemMainhand().getItem() instanceof ItemMolotov) {
            player.world.playSound(player, player.posX, player.posY, player.posZ, InitSounds.misc_deploy, SoundCategory.PLAYERS, 0.75F, 1.0F);
         }
      }

      oldItem = player.getHeldItemMainhand().getItem();
   }

   public static void disableHandSwap(boolean bool) {
      if (bool) {
         if (!(Minecraft.getMinecraft().gameSettings.keyBindSwapHands instanceof KeyBindingDisable)) {
            Minecraft.getMinecraft().gameSettings.keyBindSwapHands = new KeyBindingDisable(Minecraft.getMinecraft().gameSettings.keyBindSwapHands);
         }
      } else if (Minecraft.getMinecraft().gameSettings.keyBindSwapHands instanceof KeyBindingDisable) {
         Minecraft.getMinecraft().gameSettings.keyBindSwapHands = new KeyBindingEnable(Minecraft.getMinecraft().gameSettings.keyBindSwapHands);
      }

   }

   public static void updateCorpsePositions(WorldClient world, EntityLivingBase entityLiving) {
      ClientCorpseUtil.saveEntityPos(world, entityLiving.getEntityId());
   }

   public static void updateCorpsePositions(WorldClient world) {
      Iterator var1 = world.loadedEntityList.iterator();

      while(true) {
         Entity ent;
         do {
            if (!var1.hasNext()) {
               return;
            }

            ent = (Entity)var1.next();
         } while(!(ent instanceof EntityCommonInfected) && !(ent instanceof EntityHunter));

         ClientCorpseUtil.saveEntityPos(world, ent.getEntityId());
      }
   }
}
