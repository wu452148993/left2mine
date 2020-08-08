package net.thecallunxz.left2mine.networking.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thecallunxz.left2mine.entities.projectiles.EntityProjectile;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;
import net.thecallunxz.left2mine.items.weapons.properties.ProjectileProperties;

public class ShootMessage implements IMessage {
   private int currentSlot;
   private long worldTime;

   public ShootMessage() {
   }

   public ShootMessage(int currentSlot, long worldTime) {
      this.currentSlot = currentSlot;
      this.worldTime = worldTime;
   }

   public void fromBytes(ByteBuf buf) {
      this.currentSlot = buf.readInt();
      this.worldTime = buf.readLong();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.currentSlot);
      buf.writeLong(this.worldTime);
   }

   public static class Handler implements IMessageHandler<ShootMessage, IMessage> {
      public IMessage onMessage(final ShootMessage message, final MessageContext ctx) {
         IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.world;
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               EntityPlayerMP player = ctx.getServerHandler().player;
               ItemStack stack = player.inventory.getStackInSlot(message.currentSlot);
               if (stack.getItem() instanceof ItemGun) {
                  GunProperties gun = ((ItemGun)stack.getItem()).getGun();
                  ProjectileProperties projectile = ((ItemGun)stack.getItem()).getProjectile();
                  NBTTagCompound nbt = stack.getTagCompound();

                  for(int i = 0; i < gun.shots; ++i) {
                     EntityProjectile bullet = new EntityProjectile(player.world, player, (ItemGun)stack.getItem());
                     player.world.spawnEntity(bullet);
                  }

                  nbt.setLong("reloadTime", 0L);
                  nbt.setLong("nextShot", message.worldTime + (long)gun.maxFireCooldown);
                  nbt.setInteger("bloom", Math.min(gun.maxBloom, nbt.getInteger("bloom") + gun.bloomAdd));
                  nbt.setInteger("ammo", Math.max(0, nbt.getInteger("ammo") - 1));
                  nbt.setBoolean("justShot", true);
                  stack.setTagCompound(nbt);
                  player.world.playSound(player, player.getPosition(), gun.fireSound, SoundCategory.PLAYERS, 0.5F, 1.0F);
               }

            }
         });
         return null;
      }
   }
}
