package net.thecallunxz.left2mine.blocks.nodes.tileentities;

import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.blocks.nodes.BlockNodeChild;
import net.thecallunxz.left2mine.blocks.nodes.BlockNodeParent;

public class TileEntityNodeChild extends TileEntity implements ITickable {
   private boolean active;
   private BlockPos parentNode;
   private float redColour;
   private float blueColour;
   private float greenColour;
   private final int randomTimer;

   public TileEntityNodeChild() {
      this(new BlockPos(0, 0, 0));
   }

   public TileEntityNodeChild(BlockPos selectedNode) {
      this.active = false;
      this.parentNode = selectedNode;
      this.redColour = 1.0F;
      this.blueColour = 1.0F;
      this.greenColour = 1.0F;
      this.randomTimer = (new Random()).nextInt(2);
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

   public void update() {
      if (this.isActive()) {
         ((BlockNodeChild)this.world.getBlockState(this.pos).getBlock()).update(this.pos, this.world);
      }

      if (this.isConnected(this.world)) {
         this.setColours(((TileEntityNodeParent)this.world.getTileEntity(this.getConnectedNode())).getRedColour(), ((TileEntityNodeParent)this.world.getTileEntity(this.getConnectedNode())).getBlueColour(), ((TileEntityNodeParent)this.world.getTileEntity(this.getConnectedNode())).getGreenColour());
         Main.proxy.renderNodeParticles(this.getWorld(), this.pos, Item.getItemFromBlock(this.getWorld().getBlockState(this.pos).getBlock()), false, ((TileEntityNodeParent)this.world.getTileEntity(this.getConnectedNode())).getRedColour(), ((TileEntityNodeParent)this.world.getTileEntity(this.getConnectedNode())).getBlueColour(), ((TileEntityNodeParent)this.world.getTileEntity(this.getConnectedNode())).getGreenColour());
      } else {
         Main.proxy.renderNodeParticles(this.getWorld(), this.pos, Item.getItemFromBlock(this.getWorld().getBlockState(this.pos).getBlock()), false, this.getRedColour(), this.getBlueColour(), this.getGreenColour());
      }

   }

   public int getRandomTimer() {
      return this.randomTimer;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.active = nbt.getBoolean("active");
      this.parentNode = new BlockPos(nbt.getInteger("nodeX"), nbt.getInteger("nodeY"), nbt.getInteger("nodeZ"));
      this.redColour = nbt.getFloat("redColour");
      this.blueColour = nbt.getFloat("blueColour");
      this.greenColour = nbt.getFloat("greenColour");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setBoolean("active", this.active);
      nbt.setInteger("nodeX", this.parentNode.getX());
      nbt.setInteger("nodeY", this.parentNode.getY());
      nbt.setInteger("nodeZ", this.parentNode.getZ());
      nbt.setFloat("redColour", this.redColour);
      nbt.setFloat("blueColour", this.blueColour);
      nbt.setFloat("greenColour", this.greenColour);
      return super.writeToNBT(nbt);
   }

   public boolean isActive() {
      return this.active;
   }

   public boolean isConnected(World world) {
      return world.getBlockState(this.getConnectedNode()).getBlock() instanceof BlockNodeParent && ((TileEntityNodeParent)world.getTileEntity(this.getConnectedNode())).getChildNodes().contains(this.pos);
   }

   public boolean isChanged() {
      return this.redColour != 1.0F || this.blueColour != 1.0F || this.greenColour != 1.0F;
   }

   public void setColours(float redColour, float blueColour, float greenColour) {
      boolean changed = false;
      if (this.redColour != redColour) {
         this.redColour = redColour;
         changed = true;
      }

      if (this.blueColour != blueColour) {
         this.blueColour = blueColour;
         changed = true;
      }

      if (this.greenColour != greenColour) {
         this.greenColour = greenColour;
         changed = true;
      }

      if (changed) {
         this.markDirty();
         this.notifyUpdate();
      }

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

   public void setActive(boolean active) {
      this.active = active;
      this.markDirty();
      this.notifyUpdate();
   }

   public BlockPos getConnectedNode() {
      return this.parentNode;
   }

   public void setConnectedNode(BlockPos pos) {
      this.parentNode = pos;
      this.markDirty();
      this.notifyUpdate();
   }

   protected void notifyUpdate() {
      this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 0);
   }
}
