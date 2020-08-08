package net.thecallunxz.left2mine.entities;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.IFluidBlock;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.items.ItemBase;

public class EntityItemLoot extends EntityItemNew {
   EntityItem orig;
   private int customAge;
   long gameSeed;

   public EntityItemLoot(World world) {
      super(world);
      this.orig = null;
      this.hoverStart = this.rand.nextFloat();
      this.setSize(0.65F, 0.65F);
      this.setCustomAge(0);
   }

   public EntityItemLoot(EntityItem orig) {
      this(orig.world, orig.posX, orig.posY, orig.posZ, orig.getItem());
      NBTTagCompound oldT = new NBTTagCompound();
      orig.writeEntityToNBT(oldT);
      this.readEntityFromNBT(oldT);
      String thrower = orig.getThrower();
      Entity entity = thrower == null ? null : orig.world.getPlayerEntityByName(thrower);
      double tossSpd = entity != null && entity.isSprinting() ? 2.0D : 1.0D;
      if (entity != null) {
         this.motionX = orig.motionX * tossSpd;
         this.motionY = orig.motionY * tossSpd;
         this.motionZ = orig.motionZ * tossSpd;
      }

      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(this.world);
      this.gameSeed = (long)data.getGameSeed();
      this.setNoDespawn();
      this.setPickupDelay(0);
      this.setCustomAge(0);
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote) {
         WorldDataLeft2Mine data = WorldDataLeft2Mine.get(this.world);
         if (this.gameSeed == 0L) {
            this.gameSeed = (long)data.getGameSeed();
         }

         if ((!data.isInGame() || this.gameSeed != (long)data.getGameSeed()) && this.gameSeed != 0L) {
            this.setDead();
         }

         this.setCustomAge(this.getCustomAge() + 1);
      }

      float x = (float)MathHelper.floor(this.posX);
      float y = (float)MathHelper.floor(this.posY);
      float z = (float)MathHelper.floor(this.posZ);
      IBlockState bsHere = this.world.getBlockState(new BlockPos((double)x, (double)y, (double)z));
      IBlockState bsAbove = this.world.getBlockState(new BlockPos((double)x, (double)(y + 1.0F), (double)z));
      boolean liqHere = bsHere.getBlock() instanceof BlockLiquid || bsHere.getBlock() instanceof IFluidBlock;
      boolean liqAbove = bsAbove.getBlock() instanceof BlockLiquid || bsAbove.getBlock() instanceof IFluidBlock;
      if (liqHere) {
         this.onGround = false;
         this.inWater = true;
         if (this.motionY < 0.05D && (liqAbove || this.posY % 1.0D < 0.8999999761581421D)) {
            this.motionY += Math.min(0.075D, 0.075D - this.motionY);
         }

         this.motionX = MathHelper.clamp(this.motionX, -0.05D, 0.05D);
         this.motionZ = MathHelper.clamp(this.motionZ, -0.05D, 0.05D);
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setLong("GameSeed", this.gameSeed);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.gameSeed = compound.getLong("GameSeed");
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public EntityItemLoot(World world, double x, double y, double z, ItemStack stack) {
      super(world, x, y, z, stack);
      this.orig = null;
      this.hoverStart = this.rand.nextFloat();
      this.setSize(0.65F, 0.65F);
      this.setCustomAge(0);
   }

   public void onCollideWithPlayer(EntityPlayer player) {
   }

   public int getCustomAge() {
      return this.customAge;
   }

   public void setCustomAge(int customAge) {
      this.customAge = customAge;
   }

   public void pickup(EntityPlayer player) {
      if (!this.isDead && !player.world.isRemote) {
         int i = this.getItem().getCount();
         ItemBase item = (ItemBase)this.getItem().getItem();
         int slot = item.getSlot(item.getItemType());
         this.onItemPickup(this, i, player);
         player.replaceItemInInventory(slot, this.getItem());
         this.setDead();
      }
   }

   public void onItemPickup(Entity entityIn, int quantity, EntityLivingBase player) {
      if (!entityIn.isDead && !this.world.isRemote) {
         EntityTracker entitytracker = ((WorldServer)this.world).getEntityTracker();
         if (entityIn instanceof EntityItemNew || entityIn instanceof EntityArrow || entityIn instanceof EntityXPOrb) {
            entitytracker.sendToTracking(entityIn, new SPacketCollectItem(entityIn.getEntityId(), player.getEntityId(), quantity));
         }
      }

   }
}
