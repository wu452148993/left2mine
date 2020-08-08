package net.thecallunxz.left2mine.blocks.nodes.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class TileEntityItemNode extends TileEntityNodeChild {
   private int type;
   private int amount;
   private ItemStack customSpawn;

   public TileEntityItemNode() {
      this(new BlockPos(0, 0, 0));
   }

   public TileEntityItemNode(BlockPos selectedNode) {
      super(selectedNode);
      this.type = 0;
      this.amount = 0;
      this.customSpawn = ItemStack.EMPTY;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.type = nbt.getInteger("type");
      this.amount = nbt.getInteger("amount");
      this.customSpawn = new ItemStack(nbt.getCompoundTag("customSpawn"));
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setInteger("type", this.type);
      nbt.setInteger("amount", this.amount);
      nbt.setTag("customSpawn", this.customSpawn.serializeNBT());
      return super.writeToNBT(nbt);
   }

   public int getType() {
      return this.type;
   }

   public int getAmount() {
      return this.amount;
   }

   public ItemStack getCustomSpawn() {
      return this.customSpawn;
   }

   public void setType(int type) {
      this.type = type;
      this.markDirty();
      this.notifyUpdate();
   }

   public void setAmount(int amount) {
      this.amount = amount;
      this.markDirty();
      this.notifyUpdate();
   }

   public void setCustomSpawn(ItemStack customSpawn) {
      this.customSpawn = customSpawn;
      this.markDirty();
      this.notifyUpdate();
   }
}
