package net.thecallunxz.left2mine.blocks.nodes.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntitySaferoomNode extends TileEntityNodeParent {
   private BlockPos entrance = new BlockPos(0, 0, 0);
   private BlockPos exit = new BlockPos(0, 0, 0);

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

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.entrance = new BlockPos(nbt.getInteger("entranceX"), nbt.getInteger("entranceY"), nbt.getInteger("entranceZ"));
      this.exit = new BlockPos(nbt.getInteger("exitX"), nbt.getInteger("exitY"), nbt.getInteger("exitZ"));
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setInteger("entranceX", this.entrance.getX());
      nbt.setInteger("entranceY", this.entrance.getY());
      nbt.setInteger("entranceZ", this.entrance.getZ());
      nbt.setInteger("exitX", this.exit.getX());
      nbt.setInteger("exitY", this.exit.getY());
      nbt.setInteger("exitZ", this.exit.getZ());
      return super.writeToNBT(nbt);
   }

   public void setEntrance(BlockPos pos) {
      this.entrance = pos;
      this.markDirty();
      this.notifyUpdate();
   }

   public void setExit(BlockPos pos) {
      this.exit = pos;
      this.markDirty();
      this.notifyUpdate();
   }

   public BlockPos getEntrance() {
      return this.entrance;
   }

   public BlockPos getExit() {
      return this.exit;
   }
}
