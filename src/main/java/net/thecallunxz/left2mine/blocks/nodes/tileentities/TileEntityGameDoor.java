package net.thecallunxz.left2mine.blocks.nodes.tileentities;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.BlockGameDoor;

public class TileEntityGameDoor extends TileEntity implements ITickable {
   private long seed = 0L;
   private boolean seedSet = false;
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
               this.setSeed((long)data.getGameSeed());
               if (this.world.getBlockState(this.pos).getBlock() instanceof BlockGameDoor) {
                  this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).withProperty(BlockGameDoor.OPEN, false));
                  if (this.world.getBlockState(this.pos.up()).getBlock() instanceof BlockGameDoor) {
                     this.world.setBlockState(this.pos.up(), this.world.getBlockState(this.pos.up()).withProperty(BlockGameDoor.OPEN, false));
                  }
               }
            }
         }
      }

   }

   public void setInit(boolean init) {
      this.init = init;
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
      this.seedSet = nbt.getBoolean("seedSet");
      this.init = nbt.getBoolean("init");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setLong("seed", this.seed);
      nbt.setBoolean("seedSet", this.seedSet);
      nbt.setBoolean("init", this.init);
      return super.writeToNBT(nbt);
   }

   protected void notifyUpdate() {
      this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 0);
   }

   private void notifyUpdateRedstone(Block block) {
      this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 0);
      this.world.notifyNeighborsOfStateChange(this.pos, block, false);
   }
}
