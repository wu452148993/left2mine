package net.thecallunxz.left2mine.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.util.Left2MineClientUtilities;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

@SideOnly(Side.CLIENT)
public class GuiCustomMainMenu extends GuiScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Random RANDOM = new Random();
   private final float minceraftRoll;
   private String splashText;
   private GuiButton buttonResetDemo;
   private float panoramaTimer;
   private DynamicTexture viewportTexture;
   private final Object threadLock = new Object();
   public static final String MORE_INFO_TEXT;
   private int openGLWarning2Width;
   private int openGLWarning1Width;
   private int openGLWarningX1;
   private int openGLWarningY1;
   private int openGLWarningX2;
   private int openGLWarningY2;
   private String openGLWarning1;
   private String openGLWarning2;
   private String openGLWarningLink;
   private static final ResourceLocation SPLASH_TEXTS;
   private static final ResourceLocation TITLE_LOGO;
   private static final ResourceLocation[] TITLE_PANORAMA_PATHS;
   private ResourceLocation backgroundTexture;
   private GuiButton realmsButton;
   private boolean hasCheckedForRealmsNotification;
   private GuiScreen realmsNotification;
   private int widthCopyright;
   private int widthCopyrightRest;
   private GuiButton modButton;
   private NotificationModUpdateScreen modUpdateNotification;

   public GuiCustomMainMenu() {
      this.openGLWarning2 = MORE_INFO_TEXT;
      this.splashText = "missingno";
      IResource iresource = null;

      try {
         List<String> list = Lists.newArrayList();
         iresource = Minecraft.getMinecraft().getResourceManager().getResource(SPLASH_TEXTS);
         BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));

         String s;
         while((s = bufferedreader.readLine()) != null) {
            s = s.trim();
            if (!s.isEmpty()) {
               list.add(s);
            }
         }

         if (!list.isEmpty()) {
            do {
               this.splashText = (String)list.get(RANDOM.nextInt(list.size()));
            } while(this.splashText.hashCode() == 125780783);
         }
      } catch (IOException var8) {
      } finally {
         IOUtils.closeQuietly(iresource);
      }

      this.minceraftRoll = RANDOM.nextFloat();
      this.openGLWarning1 = "";
      if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
         this.openGLWarning1 = I18n.format("title.oldgl1", new Object[0]);
         this.openGLWarning2 = I18n.format("title.oldgl2", new Object[0]);
         this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
      }

   }

   private boolean areRealmsNotificationsEnabled() {
      return Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(Options.REALMS_NOTIFICATIONS) && this.realmsNotification != null;
   }

   public void updateScreen() {
      if (this.areRealmsNotificationsEnabled()) {
         this.realmsNotification.updateScreen();
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   public void initGui() {
      this.viewportTexture = new DynamicTexture(256, 256);
      this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
      this.widthCopyright = this.fontRenderer.getStringWidth("Copyright Mojang AB. Do not distribute!");
      this.widthCopyrightRest = this.width - this.widthCopyright - 2;
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
         this.splashText = "Merry X-mas!";
      } else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
         this.splashText = "Happy new year!";
      } else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
         this.splashText = "OOoooOOOoooo! Spooky!";
      }

      int i = true;
      int j = this.height / 4 + 48;
      if (this.mc.isDemo()) {
         this.addDemoButtons(j, 12);
      } else {
         this.addSingleplayerMultiplayerButtons(j, 12);
      }

      this.buttonList.add(new GuiCustomButton(0, 5, j + 60 + 12, I18n.format("menu.options", new Object[0])));
      this.buttonList.add(new GuiCustomButton(4, 5, j + 60 + 24, I18n.format("menu.quit", new Object[0])));
      Status status = ForgeVersion.getStatus();
      if (status != Status.OUTDATED && status != Status.BETA_OUTDATED) {
         this.buttonList.add(new GuiButtonLanguage(5, this.width - 25, this.height - 35));
      } else {
         this.buttonList.add(new GuiButtonLanguage(5, this.width - 25, this.height - 45));
      }

      synchronized(this.threadLock) {
         this.openGLWarning1Width = this.fontRenderer.getStringWidth(this.openGLWarning1);
         this.openGLWarning2Width = this.fontRenderer.getStringWidth(this.openGLWarning2);
         int k = Math.max(this.openGLWarning1Width, this.openGLWarning2Width);
         this.openGLWarningX1 = (this.width - k) / 2;
         this.openGLWarningY1 = ((GuiButton)this.buttonList.get(0)).y - 24;
         this.openGLWarningX2 = this.openGLWarningX1 + k;
         this.openGLWarningY2 = this.openGLWarningY1 + 24;
      }

      this.mc.setConnectedToRealms(false);
      if (Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(Options.REALMS_NOTIFICATIONS) && !this.hasCheckedForRealmsNotification) {
         RealmsBridge realmsbridge = new RealmsBridge();
         this.realmsNotification = realmsbridge.getNotificationScreen(this);
         this.hasCheckedForRealmsNotification = true;
      }

      if (this.areRealmsNotificationsEnabled()) {
         this.realmsNotification.setGuiSize(this.width, this.height);
         this.realmsNotification.initGui();
      }

      this.modUpdateNotification = Left2MineClientUtilities.initMenu(this, this.modButton);
   }

   private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
      this.buttonList.add(new GuiCustomButton(1, 5, p_73969_1_, I18n.format("menu.singleplayer", new Object[0])));
      this.buttonList.add(new GuiCustomButton(2, 5, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer", new Object[0])));
      this.buttonList.add(this.modButton = new GuiCustomButton(6, 5, p_73969_1_ + p_73969_2_ * 2, 100, 10, I18n.format("fml.menu.mods", new Object[0])));
      this.buttonList.add(new GuiCustomButton(3, 5, p_73969_1_ + p_73969_2_ * 4, I18n.format("menu.downloadmaps", new Object[0])));
   }

   private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
      this.buttonList.add(new GuiCustomButton(11, 5, p_73972_1_, I18n.format("menu.playdemo", new Object[0])));
      this.buttonResetDemo = this.addButton(new GuiCustomButton(12, 5, p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo", new Object[0])));
      ISaveFormat isaveformat = this.mc.getSaveLoader();
      WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
      if (worldinfo == null) {
         this.buttonResetDemo.enabled = false;
      }

   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
      }

      if (button.id == 5) {
         this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
      }

      if (button.id == 1) {
         this.mc.displayGuiScreen(new GuiWorldSelection(this));
      }

      if (button.id == 2) {
         this.mc.displayGuiScreen(new GuiMultiplayer(this));
      }

      if (button.id == 3) {
         try {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop").invoke((Object)null);
            oclass.getMethod("browse", URI.class).invoke(object, new URI("https://github.com/TheCallunxz/Left2Mine/wiki/Maps"));
         } catch (Throwable var4) {
            LOGGER.error("Couldn't open link", var4);
         }

         this.mc.displayGuiScreen(this);
      }

      if (button.id == 14 && this.realmsButton.visible) {
         this.switchToRealms();
      }

      if (button.id == 4) {
         this.mc.shutdown();
      }

      if (button.id == 6) {
         this.mc.displayGuiScreen(new GuiModList(this));
      }

      if (button.id == 7) {
         this.mc.displayGuiScreen(new GuiModList(this));
      }

      if (button.id == 11) {
         this.mc.launchIntegratedServer("Demo_World", "Demo_World", WorldServerDemo.DEMO_WORLD_SETTINGS);
      }

      if (button.id == 12) {
         ISaveFormat isaveformat = this.mc.getSaveLoader();
         WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
         if (worldinfo != null) {
            this.mc.displayGuiScreen(new GuiYesNo(this, I18n.format("selectWorld.deleteQuestion", new Object[0]), "'" + worldinfo.getWorldName() + "' " + I18n.format("selectWorld.deleteWarning", new Object[0]), I18n.format("selectWorld.deleteButton", new Object[0]), I18n.format("gui.cancel", new Object[0]), 12));
         }
      }

   }

   private void switchToRealms() {
      RealmsBridge realmsbridge = new RealmsBridge();
      realmsbridge.switchToRealms(this);
   }

   public void confirmClicked(boolean result, int id) {
      if (result && id == 12) {
         ISaveFormat isaveformat = this.mc.getSaveLoader();
         isaveformat.flushCache();
         isaveformat.deleteWorldDirectory("Demo_World");
         this.mc.displayGuiScreen(this);
      } else if (id == 12) {
         this.mc.displayGuiScreen(this);
      } else if (id == 13) {
         if (result) {
            try {
               Class<?> oclass = Class.forName("java.awt.Desktop");
               Object object = oclass.getMethod("getDesktop").invoke((Object)null);
               oclass.getMethod("browse", URI.class).invoke(object, new URI(this.openGLWarningLink));
            } catch (Throwable var5) {
               LOGGER.error("Couldn't open link", var5);
            }
         }

         this.mc.displayGuiScreen(this);
      }

   }

   private void drawPanorama(int mouseX, int mouseY, float partialTicks) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.disableCull();
      GlStateManager.depthMask(false);
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      int i = true;

      for(int j = 0; j < 64; ++j) {
         GlStateManager.pushMatrix();
         float f = ((float)(j % 8) / 8.0F - 0.5F) / 64.0F;
         float f1 = ((float)(j / 8) / 8.0F - 0.5F) / 64.0F;
         float f2 = 0.0F;
         GlStateManager.translate(f, f1, 0.0F);
         GlStateManager.rotate(MathHelper.sin(this.panoramaTimer / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(-this.panoramaTimer * 0.1F, 0.0F, 1.0F, 0.0F);

         for(int k = 0; k < 6; ++k) {
            GlStateManager.pushMatrix();
            if (k == 1) {
               GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (k == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (k == 3) {
               GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (k == 4) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (k == 5) {
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            this.mc.getTextureManager().bindTexture(TITLE_PANORAMA_PATHS[k]);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            int l = 255 / (j + 1);
            float f3 = 0.0F;
            bufferbuilder.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
            bufferbuilder.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
            bufferbuilder.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
            bufferbuilder.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
         }

         GlStateManager.popMatrix();
         GlStateManager.colorMask(true, true, true, false);
      }

      bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.enableDepth();
   }

   private void rotateAndBlurSkybox() {
      this.mc.getTextureManager().bindTexture(this.backgroundTexture);
      GlStateManager.glTexParameteri(3553, 10241, 9729);
      GlStateManager.glTexParameteri(3553, 10240, 9729);
      GlStateManager.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.colorMask(true, true, true, false);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      GlStateManager.disableAlpha();
      int i = true;

      for(int j = 0; j < 3; ++j) {
         float f = 1.0F / (float)(j + 1);
         int k = this.width;
         int l = this.height;
         float f1 = (float)(j - 1) / 256.0F;
         bufferbuilder.pos((double)k, (double)l, (double)this.zLevel).tex((double)(0.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
         bufferbuilder.pos((double)k, 0.0D, (double)this.zLevel).tex((double)(1.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
         bufferbuilder.pos(0.0D, 0.0D, (double)this.zLevel).tex((double)(1.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
         bufferbuilder.pos(0.0D, (double)l, (double)this.zLevel).tex((double)(0.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
      }

      tessellator.draw();
      GlStateManager.enableAlpha();
      GlStateManager.colorMask(true, true, true, true);
   }

   private void renderSkybox(int mouseX, int mouseY, float partialTicks) {
      this.mc.getFramebuffer().unbindFramebuffer();
      GlStateManager.viewport(0, 0, 256, 256);
      this.drawPanorama(mouseX, mouseY, partialTicks);
      this.rotateAndBlurSkybox();
      this.rotateAndBlurSkybox();
      this.rotateAndBlurSkybox();
      this.rotateAndBlurSkybox();
      this.rotateAndBlurSkybox();
      this.rotateAndBlurSkybox();
      this.rotateAndBlurSkybox();
      this.mc.getFramebuffer().bindFramebuffer(true);
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      float f = 120.0F / (float)(this.width > this.height ? this.width : this.height);
      float f1 = (float)this.height * f / 256.0F;
      float f2 = (float)this.width * f / 256.0F;
      int i = this.width;
      int j = this.height;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos(0.0D, (double)j, (double)this.zLevel).tex((double)(0.5F - f1), (double)(0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      bufferbuilder.pos((double)i, (double)j, (double)this.zLevel).tex((double)(0.5F - f1), (double)(0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      bufferbuilder.pos((double)i, 0.0D, (double)this.zLevel).tex((double)(0.5F + f1), (double)(0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, 0.0D, (double)this.zLevel).tex((double)(0.5F + f1), (double)(0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      tessellator.draw();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.panoramaTimer += partialTicks;
      GlStateManager.disableAlpha();
      this.renderSkybox(mouseX, mouseY, partialTicks);
      GlStateManager.enableAlpha();
      int i = true;
      int j = this.width / 2 - 137;
      int k = true;
      this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
      this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableAlpha();
      this.mc.getTextureManager().bindTexture(TITLE_LOGO);
      drawModalRectWithCustomSizedTexture(j - 63, -30, 0.0F, 0.0F, 400, 200, 400.0F, 200.0F);
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.disableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      this.splashText = Left2MineClientUtilities.renderMainMenu(this, this.fontRenderer, this.width, this.height, this.splashText);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)(this.width / 2 + 90), 70.0F, 0.0F);
      GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
      float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
      f = f * 100.0F / (float)(this.fontRenderer.getStringWidth(this.splashText) + 32);
      GlStateManager.scale(f, f, f);
      GlStateManager.popMatrix();
      String s = "Minecraft 1.12.2";
      if (this.mc.isDemo()) {
         s = s + " Demo";
      } else {
         s = s + ("release".equalsIgnoreCase(this.mc.getVersionType()) ? "" : "/" + this.mc.getVersionType());
      }

      List<String> brandings = new ArrayList();
      brandings.add("Left 2 Mine -> v1.0.4");

      for(int brdline = 0; brdline < brandings.size(); ++brdline) {
         String brd = (String)brandings.get(brdline);
         if (!Strings.isNullOrEmpty(brd)) {
            this.drawString(this.fontRenderer, brd, 2, this.height - (10 + brdline * (this.fontRenderer.FONT_HEIGHT + 1)), 16777215);
         }
      }

      this.drawString(this.fontRenderer, "Copyright Mojang AB. Do not distribute!", this.widthCopyrightRest, this.height - 10, -1);
      if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height && Mouse.isInsideWindow()) {
         drawRect(this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, -1);
      }

      if (this.openGLWarning1 != null && !this.openGLWarning1.isEmpty()) {
         drawRect(this.openGLWarningX1 - 2, this.openGLWarningY1 - 2, this.openGLWarningX2 + 2, this.openGLWarningY2 - 1, 1428160512);
         this.drawString(this.fontRenderer, this.openGLWarning1, this.openGLWarningX1, this.openGLWarningY1, -1);
         this.drawString(this.fontRenderer, this.openGLWarning2, (this.width - this.openGLWarning2Width) / 2, ((GuiButton)this.buttonList.get(0)).y - 12, -1);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.areRealmsNotificationsEnabled()) {
         this.realmsNotification.drawScreen(mouseX, mouseY, partialTicks);
      }

      this.modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      synchronized(this.threadLock) {
         if (!this.openGLWarning1.isEmpty() && !StringUtils.isNullOrEmpty(this.openGLWarningLink) && mouseX >= this.openGLWarningX1 && mouseX <= this.openGLWarningX2 && mouseY >= this.openGLWarningY1 && mouseY <= this.openGLWarningY2) {
            GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
            guiconfirmopenlink.disableSecurityWarning();
            this.mc.displayGuiScreen(guiconfirmopenlink);
         }
      }

      if (this.areRealmsNotificationsEnabled()) {
      }

      if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height) {
         this.mc.displayGuiScreen(new GuiWinGame(false, Runnables.doNothing()));
      }

   }

   public void onGuiClosed() {
      if (this.realmsNotification != null) {
         this.realmsNotification.onGuiClosed();
      }

   }

   static {
      MORE_INFO_TEXT = "Please click " + TextFormatting.UNDERLINE + "here" + TextFormatting.RESET + " for more information.";
      SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
      TITLE_LOGO = new ResourceLocation("left2mine:textures/gui/left2mine.png");
      TITLE_PANORAMA_PATHS = new ResourceLocation[]{new ResourceLocation("left2mine:textures/gui/background/panorama_0.png"), new ResourceLocation("left2mine:textures/gui/background/panorama_1.png"), new ResourceLocation("left2mine:textures/gui/background/panorama_2.png"), new ResourceLocation("left2mine:textures/gui/background/panorama_3.png"), new ResourceLocation("left2mine:textures/gui/background/panorama_4.png"), new ResourceLocation("left2mine:textures/gui/background/panorama_5.png")};
   }
}
