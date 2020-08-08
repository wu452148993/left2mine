package net.thecallunxz.left2mine.blocks.nodes.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;

public class TileEntityResetSeed extends TileEntity implements ITickable {
   private long seed = 0L;
   private long unTriggerTime = 0L;
   private boolean seedSet = false;
   private boolean triggered = false;
   private boolean init = false;

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tagCompound = new NBTTagCompound();
      tagCompound = this.writeToNBT(tagCompound);
      return new SPacketUpdateTileEntity(this.pos, 1, tagCompound);
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.getNbtCompound());
   }

   public void update() {
      if (!this.world.isRemote) {
         WorldDataLeft2Mine data;
         if (!this.init && this.world != null && !this.seedSet) {
            data = WorldDataLeft2Mine.get(this.world);
            this.setSeed((long)data.getGameSeed());
            this.setInit(true);
         }

         if (this.world != null && this.seedSet) {
            data = WorldDataLeft2Mine.get(this.world);
            if (this.seed != (long)data.getGameSeed()) {
               this.setUnTriggerTime(this.world.getTotalWorldTime() + 10L);
               this.setSeed((long)data.getGameSeed());
               this.setTriggered(true);
            }

            if (this.unTriggerTime < this.world.getTotalWorldTime() && this.triggered) {
               this.setTriggered(false);
            }
         }
      }

   }

   public void setInit(boolean init) {
      this.init = init;
      this.markDirty();
      this.notifyUpdate();
   }

   public boolean isTriggered() {
      return this.triggered;
   }

   public void setTriggered(boolean triggered) {
      this.triggered = triggered;
      this.markDirty();
      this.notifyUpdateRedstone();
   }

   public void setUnTriggerTime(long unTriggerTime) {
      this.unTriggerTime = unTriggerTime;
      this.markDirty();
      this.notifyUpdate();
   }

   public long getSeed() {
      return this.seed;
   }

   public void setSeed(long seed) {
      this.seed = seed;
      this.seedSet = true;
      this.markDirty();
      this.notifyUpdate();
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.seed = nbt.getLong("seed");
      this.unTriggerTime = nbt.getLong("unTriggerTime");
      this.seedSet = nbt.getBoolean("seedSet");
      this.init = nbt.getBoolean("init");
      this.triggered = nbt.getBoolean("triggered");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setLong("seed", this.seed);
      nbt.setLong("unTriggerTime", this.unTriggerTime);
      nbt.setBoolean("seedSet", this.seedSet);
      nbt.setBoolean("init", this.init);
      nbt.setBoolean("triggered", this.triggered);
      return super.writeToNBT(nbt);
   }

   protected void notifyUpdate() {
      this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 0);
   }

   private void notifyUpdateRedstone() {
      this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 0);
      this.world.notifyNeighborsOfStateChange(this.pos, this.world.getBlockState(this.pos).getBlock(), false);
   }
}
