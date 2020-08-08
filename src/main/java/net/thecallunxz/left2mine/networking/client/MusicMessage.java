package net.thecallunxz.left2mine.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.events.BacteriaMusicDirector;
import net.thecallunxz.left2mine.gui.GuiElements;
import net.thecallunxz.left2mine.gui.GuiEndCredits;
import net.thecallunxz.left2mine.gui.GuiGameOver;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.util.VotingUtil;

public class MusicMessage implements IMessage {
   private int musicID;

   public MusicMessage() {
   }

   public MusicMessage(int musicID) {
      this.musicID = musicID;
   }

   public void fromBytes(ByteBuf buf) {
      this.musicID = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.musicID);
   }

   public static PositionedSoundRecord getMusicRecord(SoundEvent soundIn, float volume) {
      return new PositionedSoundRecord(soundIn.getSoundName(), SoundCategory.MUSIC, volume, 1.0F, false, 0, AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
   }

   public static class Handler implements IMessageHandler<MusicMessage, IMessage> {
      public IMessage onMessage(final MusicMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               switch(message.musicID) {
               case 0:
                  Minecraft.getMinecraft().getSoundHandler().stopSounds();
                  BacteriaMusicDirector.playingTankMusic = false;
                  PositionedSoundRecord music = MusicMessage.getMusicRecord(InitSounds.game_start, 0.75F);
                  Main.proxy.getClientHandler().corpseListClear();
                  Minecraft.getMinecraft().getSoundHandler().playSound(music);
                  break;
               case 1:
                  Minecraft.getMinecraft().getSoundHandler().stopSounds();
                  BacteriaMusicDirector.playingTankMusic = false;
                  PositionedSoundRecord music2 = MusicMessage.getMusicRecord(InitSounds.saferoom, 0.75F);
                  GuiElements.tips.clear();
                  Main.proxy.getClientHandler().corpseListClear();
                  Minecraft.getMinecraft().getSoundHandler().playSound(music2);
                  break;
               case 2:
                  Minecraft.getMinecraft().getSoundHandler().stopSounds();
                  BacteriaMusicDirector.playingTankMusic = false;
                  break;
               case 3:
                  Minecraft.getMinecraft().getSoundHandler().stopSounds();
                  BacteriaMusicDirector.playingTankMusic = false;
                  GuiElements.tips.clear();
                  PositionedSoundRecord music3 = MusicMessage.getMusicRecord(InitSounds.game_win, 1.0F);
                  Minecraft.getMinecraft().getSoundHandler().playSound(music3);
                  break;
               case 4:
                  PositionedSoundRecord music4 = MusicMessage.getMusicRecord(InitSounds.game_lose1, 1.0F);
                  Minecraft.getMinecraft().getSoundHandler().playSound(music4);
                  break;
               case 5:
                  Minecraft.getMinecraft().getSoundHandler().stopSounds();
                  BacteriaMusicDirector.playingTankMusic = false;
                  PositionedSoundRecord music6 = MusicMessage.getMusicRecord(InitSounds.game_lose2, 1.0F);
                  Minecraft.getMinecraft().getSoundHandler().playSound(music6);
                  GuiElements.tips.clear();
                  Main.proxy.getClientHandler().corpseListClear();
                  Minecraft.getMinecraft().displayGuiScreen(new GuiGameOver());
                  break;
               case 6:
                  GuiElements.tips.clear();
                  Minecraft.getMinecraft().displayGuiScreen(new GuiEndCredits());
                  break;
               case 7:
                  PositionedSoundRecord music5 = MusicMessage.getMusicRecord(InitSounds.horde_germ, 5.0F);
                  Minecraft.getMinecraft().getSoundHandler().playSound(music5);
                  break;
               case 8:
                  VotingUtil.resetVoting();
                  Minecraft.getMinecraft().getSoundHandler().stopSounds();
                  BacteriaMusicDirector.playingTankMusic = false;
                  Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
               }

            }
         });
         return null;
      }
   }
}
