package net.thecallunxz.left2mine.blocks.nodes.tileentities;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.thecallunxz.left2mine.Main;

public class TileEntityNodeParent extends TileEntity implements ITickable {
   private ArrayList<BlockPos> childNodeList = new ArrayList();
   private float redColour;
   private float blueColour;
   private float greenColour;
   private long breakTimer;

   public TileEntityNodeParent() {
      Random rand = new Random();
      this.breakTimer = 0L;
      this.redColour = rand.nextFloat();
      this.blueColour = rand.nextFloat();
      this.greenColour = rand.nextFloat();
   }

   public void update() {
      Main.proxy.renderNodeParticles(this.getWorld(), this.pos, Item.getItemFromBlock(this.getWorld().getBlockState(this.pos).getBlock()), false, this.redColour, this.blueColour, this.greenColour);
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
      NBTTagList tagList = (NBTTagList)nbt.getTag("childNodeList");
      this.childNodeList.clear();

      for(int i = 0; i < tagList.tagCount(); ++i) {
         NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
         this.childNodeList.add(NBTUtil.getPosFromTag(nbttagcompound));
      }

      this.redColour = nbt.getFloat("redColour");
      this.blueColour = nbt.getFloat("blueColour");
      this.greenColour = nbt.getFloat("greenColour");
      this.breakTimer = nbt.getLong("breakTimer");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      NBTTagList tagList = new NBTTagList();

      for(int i = 0; i < this.childNodeList.size(); ++i) {
         BlockPos nodePos = (BlockPos)this.childNodeList.get(i);
         NBTTagCompound nbttagcompound = NBTUtil.createPosTag(nodePos);
         tagList.appendTag(nbttagcompound);
      }

      nbt.setTag("childNodeList", tagList);
      nbt.setFloat("redColour", this.redColour);
      nbt.setFloat("blueColour", this.blueColour);
      nbt.setFloat("greenColour", this.greenColour);
      nbt.setLong("breakTimer", this.breakTimer);
      return super.writeToNBT(nbt);
   }

   public float getRedColour() {
      return this.redColour;
   }

   public float getBlueColour() {
      return this.blueColour;
   }

   public float getGreenColour() {
      return this.greenColour;
   }

   public void addChildNode(BlockPos pos) {
      if (!this.findChildNode(pos)) {
         this.childNodeList.add(pos);
      }

      this.notifyUpdate();
      this.markDirty();
   }

   public void removeChildNode(BlockPos pos) {
      this.childNodeList.remove(pos);
      this.notifyUpdate();
      this.markDirty();
   }

   public void clearChildNodes() {
      this.childNodeList.clear();
      this.notifyUpdate();
      this.markDirty();
   }

   public boolean findChildNode(BlockPos pos) {
      return this.childNodeList.contains(pos);
   }

   public ArrayList<BlockPos> getChildNodes() {
      return this.childNodeList;
   }

   public long getBreakTimer() {
      return this.breakTimer;
   }

   public void setBreakTimer(long breakTimer) {
      this.breakTimer = breakTimer;
      this.notifyUpdate();
      this.markDirty();
   }

   protected void notifyUpdate() {
      this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 0);
   }
}
