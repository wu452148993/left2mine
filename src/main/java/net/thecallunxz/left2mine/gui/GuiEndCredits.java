package net.thecallunxz.left2mine.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.capabilities.IStats;
import net.thecallunxz.left2mine.capabilities.StatsProvider;
import net.thecallunxz.left2mine.events.BacteriaMusicDirector;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.server.VoteServerMessage;
import net.thecallunxz.left2mine.util.VotingUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class GuiEndCredits extends GuiScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation field_194401_g = new ResourceLocation("left2mine:textures/gui/creditstitle.png");
   private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
   private static final ResourceLocation creditText = new ResourceLocation("left2mine:texts/credits.txt");
   private float time;
   private List<String> lines;
   private int totalScrollLength;
   private float scrollSpeed = 1.25F;

   public void updateScreen() {
      float f = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
      if (this.time > f) {
         this.sendRespawnPacket();
      }

   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1) {
         Left2MinePacket.INSTANCE.sendToServer(new VoteServerMessage());
      }

   }

   private void sendRespawnPacket() {
      VotingUtil.resetVoting();
      Minecraft.getMinecraft().getSoundHandler().stopSounds();
      BacteriaMusicDirector.playingTankMusic = false;
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void initGui() {
      if (this.lines == null) {
         this.lines = Lists.newArrayList();
         UUID clientID = this.mc.player.getUniqueID();
         IStats clientStats = (IStats)this.mc.player.getCapability(StatsProvider.STATS, (EnumFacing)null);
         Object iresource = null;

         try {
            String s = "" + TextFormatting.WHITE + TextFormatting.OBFUSCATED + TextFormatting.GREEN + TextFormatting.AQUA;
            InputStream inputstream1 = this.mc.getResourceManager().getResource(creditText).getInputStream();
            BufferedReader bufferedreader1 = new BufferedReader(new InputStreamReader(inputstream1, StandardCharsets.UTF_8));

            String s4;
            while((s4 = bufferedreader1.readLine()) != null) {
               ArrayList<String> deceasedPlayers = new ArrayList();
               ArrayList<String> survivorPlayers = new ArrayList();
               Iterator var10;
               EntityPlayer player;
               if (s4.startsWith("[DECEASEDPLAYERS]")) {
                  s4 = "";
                  var10 = Minecraft.getMinecraft().world.playerEntities.iterator();

                  label402:
                  while(true) {
                     IEquip equip;
                     do {
                        if (!var10.hasNext()) {
                           if (deceasedPlayers.size() > 0) {
                              s4 = "[C]" + I18n.format("gui.inmemoryof", new Object[0]);
                           }
                           break label402;
                        }

                        player = (EntityPlayer)var10.next();
                        equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
                     } while(!equip.getLying() && !Main.deadPlayerList.contains(player.getDisplayNameString()));

                     deceasedPlayers.add("[C]" + player.getDisplayNameString());
                  }
               }

               if (s4.startsWith("[SURVIVORS]")) {
                  s4 = "";
                  var10 = Minecraft.getMinecraft().world.playerEntities.iterator();

                  label419:
                  while(true) {
                     do {
                        if (!var10.hasNext()) {
                           break label419;
                        }

                        player = (EntityPlayer)var10.next();
                     } while(player.isCreative());

                     String strAdd = "";
                     IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
                     if (equip.getLying() || Main.deadPlayerList.contains(player.getDisplayNameString())) {
                        strAdd = I18n.format("gui.deceased", new Object[0]);
                     }

                     survivorPlayers.add(player.getDisplayNameString() + "[S]" + I18n.format("gui.asthemselves", new Object[0]) + " " + strAdd);
                  }
               }

               if (s4.startsWith("[GAMEPLAYSTATS]")) {
                  s4 = "";
                  this.lines.add(I18n.format("gui.totalmissiontime", new Object[0]) + "[S]" + this.getTimeFromLong(this.mc.world.getTotalWorldTime() - Main.gameStartTime));
                  this.lines.add(I18n.format("gui.setdifficulty", new Object[0]) + "[S]" + this.getDifficultyName(Main.difficulty));
                  this.lines.add(I18n.format("gui.timesrestarted", new Object[0]) + "[S]" + Main.timesRestarted);
                  this.lines.add("");
                  this.lines.add(I18n.format("gui.deaths", new Object[0]) + "[S]" + clientStats.getDeaths() + " - " + this.mc.player.getDisplayNameString());
                  var10 = this.mc.world.playerEntities.iterator();

                  IStats stats;
                  while(var10.hasNext()) {
                     player = (EntityPlayer)var10.next();
                     if (!player.isCreative() && player.getUniqueID() != clientID) {
                        stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                        this.lines.add("[R]" + stats.getDeaths() + " - " + player.getDisplayNameString());
                     }
                  }

                  this.lines.add("");
                  this.lines.add(I18n.format("gui.timesincapacitated", new Object[0]) + "[S]" + clientStats.getIncapacitations() + " - " + this.mc.player.getDisplayNameString());
                  var10 = this.mc.world.playerEntities.iterator();

                  while(var10.hasNext()) {
                     player = (EntityPlayer)var10.next();
                     if (!player.isCreative() && player.getUniqueID() != clientID) {
                        stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                        this.lines.add("[R]" + stats.getIncapacitations() + " - " + player.getDisplayNameString());
                     }
                  }

                  this.lines.add("");
                  this.lines.add(I18n.format("gui.firstaidused", new Object[0]) + "[S]" + clientStats.getMedpacksUsed() + " - " + this.mc.player.getDisplayNameString());
                  var10 = this.mc.world.playerEntities.iterator();

                  while(var10.hasNext()) {
                     player = (EntityPlayer)var10.next();
                     if (!player.isCreative() && player.getUniqueID() != clientID) {
                        stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                        this.lines.add("[R]" + stats.getMedpacksUsed() + " - " + player.getDisplayNameString());
                     }
                  }

                  this.lines.add("");
                  this.lines.add(I18n.format("gui.pillsused", new Object[0]) + "[S]" + clientStats.getPillsUsed() + " - " + this.mc.player.getDisplayNameString());
                  var10 = this.mc.world.playerEntities.iterator();

                  while(var10.hasNext()) {
                     player = (EntityPlayer)var10.next();
                     if (!player.isCreative() && player.getUniqueID() != clientID) {
                        stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                        this.lines.add("[R]" + stats.getPillsUsed() + " - " + player.getDisplayNameString());
                     }
                  }

                  this.lines.add("");
                  this.lines.add(I18n.format("gui.pipebombsused", new Object[0]) + "[S]" + clientStats.getPipebombsUsed() + " - " + this.mc.player.getDisplayNameString());
                  var10 = this.mc.world.playerEntities.iterator();

                  while(var10.hasNext()) {
                     player = (EntityPlayer)var10.next();
                     if (!player.isCreative() && player.getUniqueID() != clientID) {
                        stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                        this.lines.add("[R]" + stats.getPipebombsUsed() + " - " + player.getDisplayNameString());
                     }
                  }

                  this.lines.add("");
                  this.lines.add(I18n.format("gui.commonkilled", new Object[0]) + "[S]" + clientStats.getCommonKilled() + " - " + this.mc.player.getDisplayNameString());
                  var10 = this.mc.world.playerEntities.iterator();

                  while(var10.hasNext()) {
                     player = (EntityPlayer)var10.next();
                     if (!player.isCreative() && player.getUniqueID() != clientID) {
                        stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                        this.lines.add("[R]" + stats.getCommonKilled() + " - " + player.getDisplayNameString());
                     }
                  }

                  this.lines.add("");
                  this.lines.add(I18n.format("gui.hunterskilled", new Object[0]) + "[S]" + clientStats.getHuntersKilled() + " - " + this.mc.player.getDisplayNameString());
                  var10 = this.mc.world.playerEntities.iterator();

                  while(var10.hasNext()) {
                     player = (EntityPlayer)var10.next();
                     if (!player.isCreative() && player.getUniqueID() != clientID) {
                        stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                        this.lines.add("[R]" + stats.getHuntersKilled() + " - " + player.getDisplayNameString());
                     }
                  }

                  this.lines.add("");
                  this.lines.add(I18n.format("gui.boomerskilled", new Object[0]) + "[S]" + clientStats.getBoomersKilled() + " - " + this.mc.player.getDisplayNameString());
                  var10 = this.mc.world.playerEntities.iterator();

                  while(var10.hasNext()) {
                     player = (EntityPlayer)var10.next();
                     if (!player.isCreative() && player.getUniqueID() != clientID) {
                        stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                        this.lines.add("[R]" + stats.getBoomersKilled() + " - " + player.getDisplayNameString());
                     }
                  }

                  this.lines.add("");
                  this.lines.add(I18n.format("gui.smokerskilled", new Object[0]) + "[S]" + clientStats.getSmokersKilled() + " - " + this.mc.player.getDisplayNameString());
                  var10 = this.mc.world.playerEntities.iterator();

                  while(var10.hasNext()) {
                     player = (EntityPlayer)var10.next();
                     if (!player.isCreative() && player.getUniqueID() != clientID) {
                        stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                        this.lines.add("[R]" + stats.getSmokersKilled() + " - " + player.getDisplayNameString());
                     }
                  }

                  this.lines.add("");
                  this.lines.add(I18n.format("gui.tankdamage", new Object[0]) + "[S]" + clientStats.getTankDamage() + " - " + this.mc.player.getDisplayNameString());
                  var10 = this.mc.world.playerEntities.iterator();

                  while(var10.hasNext()) {
                     player = (EntityPlayer)var10.next();
                     if (!player.isCreative() && player.getUniqueID() != clientID) {
                        stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                        this.lines.add("[R]" + stats.getTankDamage() + " - " + player.getDisplayNameString());
                     }
                  }

                  this.lines.add("");
                  this.lines.add(I18n.format("gui.witchdamage", new Object[0]) + "[S]" + clientStats.getWitchDamage() + " - " + this.mc.player.getDisplayNameString());
                  var10 = this.mc.world.playerEntities.iterator();

                  while(var10.hasNext()) {
                     player = (EntityPlayer)var10.next();
                     if (!player.isCreative() && player.getUniqueID() != clientID) {
                        stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                        this.lines.add("[R]" + stats.getWitchDamage() + " - " + player.getDisplayNameString());
                     }
                  }
               }

               this.lines.addAll(this.mc.fontRenderer.listFormattedStringToWidth(s4, 374));
               if (deceasedPlayers.size() > 0) {
                  this.lines.add("");
               }

               int i;
               for(i = 0; i < deceasedPlayers.size(); ++i) {
                  this.lines.addAll(this.mc.fontRenderer.listFormattedStringToWidth((String)deceasedPlayers.get(i), 274));
               }

               for(i = 0; i < survivorPlayers.size(); ++i) {
                  this.lines.addAll(this.mc.fontRenderer.listFormattedStringToWidth((String)survivorPlayers.get(i), 274));
               }
            }

            inputstream1.close();
            this.totalScrollLength = this.lines.size() * 12;
         } catch (Exception var17) {
            LOGGER.error("Couldn't load credits", var17);
         } finally {
            IOUtils.closeQuietly((Closeable)iresource);
         }
      }

   }

   private String getDifficultyName(int difficulty) {
      switch(difficulty) {
      case 1:
         return "Easy";
      case 2:
         return "Normal";
      case 3:
         return "Hard";
      case 4:
         return "Expert";
      case 5:
         return "Survival";
      default:
         return "ERROR";
      }
   }

   private String getTimeFromLong(long time) {
      long totalSeconds = (long)Math.floor((double)(time / 20L));
      int hours = (int)totalSeconds / 3600;
      int remainder = (int)totalSeconds - hours * 3600;
      int mins = remainder / 60;
      remainder -= mins * 60;
      String hourSuffix = "s";
      String minSuffix = "s";
      String secSuffix = "s";
      if (hours == 1) {
         hourSuffix = "";
      }

      if (mins == 1) {
         minSuffix = "";
      }

      if (remainder == 1) {
         secSuffix = "";
      }

      if (hours != 0) {
         return hours + " hour" + hourSuffix + ", " + mins + " minute" + minSuffix + ", " + remainder + " second" + secSuffix;
      } else {
         return mins != 0 ? mins + " minute" + minSuffix + ", " + remainder + " second" + secSuffix : remainder + " second" + secSuffix;
      }
   }

   private void drawWinGameScreen(int p_146575_1_, int p_146575_2_, float p_146575_3_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      this.mc.getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      int i = this.width;
      float f = -this.time * 0.5F * this.scrollSpeed;
      float f1 = (float)this.height - this.time * 0.5F * this.scrollSpeed;
      float f2 = 0.015625F;
      float f3 = this.time * 0.02F;
      float f4 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
      float f5 = (f4 - 20.0F - this.time) * 0.005F;
      if (f5 < f3) {
         f3 = f5;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      f3 *= f3;
      f3 = f3 * 96.0F / 255.0F;
      bufferbuilder.pos(0.0D, (double)this.height, (double)this.zLevel).tex(0.0D, (double)(f * 0.015625F)).color(f3, f3, f3, 1.0F).endVertex();
      bufferbuilder.pos((double)i, (double)this.height, (double)this.zLevel).tex((double)((float)i * 0.015625F), (double)(f * 0.015625F)).color(f3, f3, f3, 1.0F).endVertex();
      bufferbuilder.pos((double)i, 0.0D, (double)this.zLevel).tex((double)((float)i * 0.015625F), (double)(f1 * 0.015625F)).color(f3, f3, f3, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, 0.0D, (double)this.zLevel).tex(0.0D, (double)(f1 * 0.015625F)).color(f3, f3, f3, 1.0F).endVertex();
      tessellator.draw();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawWinGameScreen(mouseX, mouseY, partialTicks);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      int i = true;
      int j = this.width / 2 - 137;
      int k = this.height + 50;
      this.time += partialTicks;
      float f = -this.time * this.scrollSpeed;
      this.drawStringRight(this.fontRenderer, I18n.format("gui.votestoskip", new Object[0]) + " " + VotingUtil.voting + " / " + VotingUtil.maxVoters + " ", this.width - 5, this.height - 15, 16777215);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, f, 0.0F);
      this.mc.getTextureManager().bindTexture(field_194401_g);
      drawModalRectWithCustomSizedTexture(this.width / 2 - 130, k - 75, 0.0F, 0.0F, 270, 14, 270.0F, 14.0F);
      GlStateManager.disableAlpha();
      int l = k;

      int i1;
      for(i1 = 0; i1 < this.lines.size(); ++i1) {
         if (i1 == this.lines.size() - 1) {
            float f1 = (float)l + f - (float)(this.height / 2 - 6);
            if (f1 < 0.0F) {
               GlStateManager.translate(0.0F, -f1, 0.0F);
            }
         }

         if ((float)l + f + 12.0F + 8.0F > 0.0F && (float)l + f < (float)this.height) {
            String s = (String)this.lines.get(i1);
            if (s.startsWith("[C]")) {
               this.fontRenderer.drawStringWithShadow(s.substring(3), (float)(j + (274 - this.fontRenderer.getStringWidth(s.substring(3))) / 2), (float)l, 16777215);
            } else if (s.startsWith("[R]")) {
               this.fontRenderer.drawStringWithShadow(s.substring(3), (float)(j + (274 - this.fontRenderer.getStringWidth(s.substring(3))) / 2) + (float)(this.fontRenderer.getStringWidth(s.substring(3)) / 2) + 6.0F, (float)l, 16777215);
            } else {
               int zombieCount;
               String stringNumber;
               if (!s.startsWith("[T]")) {
                  if (s.contains("[S]")) {
                     zombieCount = s.indexOf("[S]");
                     stringNumber = s.substring(0, zombieCount);
                     String str2 = s.substring(zombieCount + 3);
                     this.fontRenderer.drawStringWithShadow(stringNumber, (float)(j + (274 - this.fontRenderer.getStringWidth(stringNumber)) / 2) - (float)(this.fontRenderer.getStringWidth(stringNumber) / 2) - 6.0F, (float)l, 16777215);
                     this.fontRenderer.drawStringWithShadow(str2, (float)(j + (274 - this.fontRenderer.getStringWidth(stringNumber)) / 2) + (float)(this.fontRenderer.getStringWidth(stringNumber) / 2) + 6.0F, (float)l, 16777215);
                  } else {
                     this.fontRenderer.fontRandom.setSeed((long)((float)((long)i1 * 4238972211L) + this.time / 4.0F));
                     this.fontRenderer.drawStringWithShadow(s, (float)j, (float)l, 16777215);
                  }
               } else {
                  zombieCount = 0;

                  IStats stats;
                  for(Iterator var14 = this.mc.world.playerEntities.iterator(); var14.hasNext(); zombieCount += stats.getSmokersKilled()) {
                     EntityPlayer player = (EntityPlayer)var14.next();
                     stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
                     zombieCount += stats.getCommonKilled();
                     zombieCount += stats.getHuntersKilled();
                     zombieCount += stats.getBoomersKilled();
                  }

                  if (zombieCount != 1) {
                     stringNumber = (new DecimalFormat("#,###.##")).format((long)zombieCount);
                     this.fontRenderer.drawStringWithShadow(stringNumber + s.substring(3), (float)(j + (274 - this.fontRenderer.getStringWidth(stringNumber + s.substring(3))) / 2), (float)l, 16777215);
                  } else {
                     this.fontRenderer.drawStringWithShadow("1 zombie was harmed in the making of this film.", (float)(j + (274 - this.fontRenderer.getStringWidth("1 zombie was harmed in the making of this film.")) / 2), (float)l, 16777215);
                  }
               }
            }
         }

         l += 12;
      }

      GlStateManager.popMatrix();
      this.mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_COLOR);
      i1 = this.width;
      int k1 = this.height;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.pos(0.0D, (double)k1, (double)this.zLevel).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      bufferbuilder.pos((double)i1, (double)k1, (double)this.zLevel).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      bufferbuilder.pos((double)i1, 0.0D, (double)this.zLevel).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      bufferbuilder.pos(0.0D, 0.0D, (double)this.zLevel).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      tessellator.draw();
      GlStateManager.disableBlend();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void drawStringRight(FontRenderer fontRendererIn, String text, int x, int y, int color) {
      fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text)), (float)y, color);
   }
}
