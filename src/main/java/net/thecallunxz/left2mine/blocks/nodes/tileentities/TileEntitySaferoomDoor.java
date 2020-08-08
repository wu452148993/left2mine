package net.thecallunxz.left2mine.blocks.nodes.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.BlockSaferoomDoor;
import net.thecallunxz.left2mine.blocks.nodes.BlockNodeParent;

public class TileEntitySaferoomDoor extends TileEntity implements ITickable {
   private BlockPos parentNode;
   private boolean entrance;
   private boolean initiated;
   private boolean switched;
   private int doorSeed;

   public TileEntitySaferoomDoor() {
      this(new BlockPos(0, 0, 0), 0);
   }

   public TileEntitySaferoomDoor(BlockPos selectedNode, int seed) {
      this.parentNode = selectedNode;
      this.doorSeed = seed;
      this.initiated = false;
      this.switched = false;
      this.entrance = true;
   }

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
      this.parentNode = new BlockPos(nbt.getInteger("nodeX"), nbt.getInteger("nodeY"), nbt.getInteger("nodeZ"));
      this.switched = nbt.getBoolean("switched");
      this.initiated = nbt.getBoolean("initiated");
      this.entrance = nbt.getBoolean("entrance");
      this.doorSeed = nbt.getInteger("doorSeed");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setInteger("nodeX", this.parentNode.getX());
      nbt.setInteger("nodeY", this.parentNode.getY());
      nbt.setInteger("nodeZ", this.parentNode.getZ());
      nbt.setBoolean("switched", this.switched);
      nbt.setBoolean("initiated", this.initiated);
      nbt.setBoolean("entrance", this.entrance);
      nbt.setInteger("doorSeed", this.doorSeed);
      return super.writeToNBT(nbt);
   }

   public boolean isConnected(World world) {
      return world.getBlockState(this.getConnectedNode()).getBlock() instanceof BlockNodeParent && ((TileEntityNodeParent)world.getTileEntity(this.getConnectedNode())).getChildNodes().contains(this.pos);
   }

   public BlockPos getConnectedNode() {
      return this.parentNode;
   }

   public void setConnectedNode(BlockPos pos) {
      this.parentNode = pos;
      this.markDirty();
      this.notifyUpdate();
   }

   public boolean getSwitched() {
      return this.switched;
   }

   public boolean getEntrance() {
      return this.entrance;
   }

   public void setDoorSeed(int seed) {
      this.doorSeed = seed;
      this.markDirty();
      this.notifyUpdate();
   }

   public void setSwitched(boolean bool) {
      System.out.println(bool);
      this.switched = bool;
      this.markDirty();
      this.notifyUpdate();
   }

   public void setInitiated(boolean bool) {
      this.initiated = bool;
      this.markDirty();
      this.notifyUpdate();
   }

   protected void notifyUpdate() {
      this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 0);
   }

   public void update() {
      NBTTagCompound nbt = this.serializeNBT();
      if (!this.world.isRemote) {
         WorldDataLeft2Mine data = WorldDataLeft2Mine.get(this.world);
         if (this.doorSeed == 0) {
            this.setDoorSeed(data.getGameSeed());
         }

         if (data.getGameSeed() != 0 && data.getGameSeed() != this.doorSeed && data.getRespawnPos() != this.getConnectedNode()) {
            this.setDoorSeed(data.getGameSeed());
            this.setSwitched(false);
            this.setInitiated(false);
         }

         if (!nbt.getBoolean("initiated")) {
            nbt.setBoolean("initiated", true);
            this.setInitiated(true);
            this.deserializeNBT(nbt);
            if (this.getConnectedNode() != new BlockPos(0, 0, 0) && this.world.getTileEntity(this.getConnectedNode()) instanceof TileEntitySaferoomNode) {
               BlockPos exitPos = ((TileEntitySaferoomNode)this.world.getTileEntity(this.getConnectedNode())).getExit();
               if (exitPos.getX() == this.pos.getX() && exitPos.getY() == this.pos.getY() && exitPos.getZ() == this.pos.getZ()) {
                  this.entrance = false;
               } else {
                  this.entrance = true;
               }

               IBlockState stateold;
               IBlockState statenew;
               if ((Boolean)this.world.getBlockState(this.pos.up()).getValue(BlockSaferoomDoor.OPEN)) {
                  stateold = this.world.getBlockState(this.pos.up());
                  statenew = stateold.cycleProperty(BlockSaferoomDoor.OPEN);
                  this.world.setBlockState(this.pos.up(), statenew, 2);
                  this.world.notifyBlockUpdate(this.pos.up(), stateold, statenew, 2);
                  stateold = this.world.getBlockState(this.pos.up().up());
                  statenew = stateold.cycleProperty(BlockSaferoomDoor.OPEN);
                  this.world.setBlockState(this.pos.up().up(), statenew, 2);
                  this.world.notifyBlockUpdate(this.pos.up().up(), stateold, statenew, 2);
               }

               if (this.entrance) {
                  if (!nbt.getBoolean("switched")) {
                     stateold = this.world.getBlockState(this.pos.up());
                     statenew = stateold.withProperty(BlockSaferoomDoor.LOCKED, false);
                     this.world.setBlockState(this.pos.up(), statenew, 2);
                     this.world.notifyBlockUpdate(this.pos.up(), stateold, statenew, 2);
                     stateold = this.world.getBlockState(this.pos.up().up());
                     statenew = stateold.withProperty(BlockSaferoomDoor.LOCKED, false);
                     this.world.setBlockState(this.pos.up().up(), statenew, 2);
                     this.world.notifyBlockUpdate(this.pos.up().up(), stateold, statenew, 2);
                  } else {
                     stateold = this.world.getBlockState(this.pos.up());
                     statenew = stateold.withProperty(BlockSaferoomDoor.LOCKED, true);
                     this.world.setBlockState(this.pos.up(), statenew, 2);
                     this.world.notifyBlockUpdate(this.pos.up(), stateold, statenew, 2);
                     stateold = this.world.getBlockState(this.pos.up().up());
                     statenew = stateold.withProperty(BlockSaferoomDoor.LOCKED, true);
                     this.world.setBlockState(this.pos.up().up(), statenew, 2);
                     this.world.notifyBlockUpdate(this.pos.up().up(), stateold, statenew, 2);
                  }
               } else if (!nbt.getBoolean("switched")) {
                  stateold = this.world.getBlockState(this.pos.up());
                  statenew = stateold.withProperty(BlockSaferoomDoor.LOCKED, true);
                  this.world.setBlockState(this.pos.up(), statenew, 2);
                  this.world.notifyBlockUpdate(this.pos.up(), stateold, statenew, 2);
                  stateold = this.world.getBlockState(this.pos.up().up());
                  statenew = stateold.withProperty(BlockSaferoomDoor.LOCKED, true);
                  this.world.setBlockState(this.pos.up().up(), statenew, 2);
                  this.world.notifyBlockUpdate(this.pos.up().up(), stateold, statenew, 2);
               } else {
                  stateold = this.world.getBlockState(this.pos.up());
                  statenew = stateold.withProperty(BlockSaferoomDoor.LOCKED, false);
                  this.world.setBlockState(this.pos.up(), statenew, 2);
                  this.world.notifyBlockUpdate(this.pos.up(), stateold, statenew, 2);
                  stateold = this.world.getBlockState(this.pos.up().up());
                  statenew = stateold.withProperty(BlockSaferoomDoor.LOCKED, false);
                  this.world.setBlockState(this.pos.up().up(), statenew, 2);
                  this.world.notifyBlockUpdate(this.pos.up().up(), stateold, statenew, 2);
               }
            }
         }
      }

   }
}
