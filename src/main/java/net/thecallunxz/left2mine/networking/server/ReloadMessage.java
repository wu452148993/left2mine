package net.thecallunxz.left2mine.networking.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;
import net.thecallunxz.left2mine.items.weapons.properties.ProjectileProperties;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;

public class ReloadMessage implements IMessage {
   private int currentSlot;

   public ReloadMessage() {
   }

   public ReloadMessage(int currentSlot) {
      this.currentSlot = currentSlot;
   }

   public void fromBytes(ByteBuf buf) {
      this.currentSlot = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.currentSlot);
   }

   public static boolean isReloading(World world, NBTTagCompound nbt) {
      return nbt.getLong("reloadTime") >= world.getTotalWorldTime();
   }

   public static class Handler implements IMessageHandler<ReloadMessage, IMessage> {
      public IMessage onMessage(final ReloadMessage message, final MessageContext ctx) {
         IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.world;
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               EntityPlayerMP player = ctx.getServerHandler().player;
               ItemStack stack = player.inventory.getStackInSlot(message.currentSlot);
               GunProperties gun = ((ItemGun)stack.getItem()).getGun();
               ProjectileProperties projectile = ((ItemGun)stack.getItem()).getProjectile();
               NBTTagCompound nbt = stack.getTagCompound();
               if (!ReloadMessage.isReloading(player.getEntityWorld(), nbt) && nbt.getInteger("ammo") != gun.magazineSize && (nbt.getInteger("storage") > 0 || gun.infinite)) {
                  if (gun.shellreload && nbt.getInteger("ammo") == 0) {
                     nbt.setLong("reloadTime", player.getEntityWorld().getTotalWorldTime() + (long)((int)Math.max((double)gun.maxReloadTime * 1.5D, (double)gun.maxFireCooldown * 1.5D)));
                  } else {
                     nbt.setLong("reloadTime", player.getEntityWorld().getTotalWorldTime() + (long)gun.maxReloadTime);
                  }

                  if (!gun.shellreload) {
                     Left2MinePacket.INSTANCE.sendToAll(new MovingSoundMessage(player, 2));
                  } else {
                     player.world.playSound((EntityPlayer)null, player.getPosition(), gun.equipSound, SoundCategory.PLAYERS, 0.25F, 1.0F);
                  }
               }

            }
         });
         return null;
      }
   }
}
