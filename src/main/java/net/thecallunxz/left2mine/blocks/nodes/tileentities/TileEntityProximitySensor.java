package net.thecallunxz.left2mine.blocks.nodes.tileentities;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.BlockProximitySensor;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class TileEntityProximitySensor extends TileEntity implements ITickable {
   private ArrayList<BlockPos> linkedList = new ArrayList();
   private BlockPos pairedLoc;
   private boolean primary;
   private boolean triggered;
   private boolean isRemote;
   private boolean init;

   public TileEntityProximitySensor() {
      this.pairedLoc = BlockPos.ORIGIN;
      this.isRemote = false;
      this.triggered = false;
      this.primary = false;
      this.init = false;
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
      EnumFacing facing;
      if (this.getPairedLoc().equals(BlockPos.ORIGIN)) {
         facing = (EnumFacing)this.world.getBlockState(this.pos).getValue(BlockProximitySensor.FACING);
         Main.proxy.renderProximityParticles(this.getWorld(), this.pos, (Item)null, true, 1.0F, 1.0F, 1.0F, facing);
      }

      if (!this.init && this.world != null && this.getPairedLoc().equals(BlockPos.ORIGIN)) {
         facing = (EnumFacing)this.world.getBlockState(this.pos).getValue(BlockProximitySensor.FACING);

         BlockPos checkPos;
         for(int i = 1; i < 50; ++i) {
            checkPos = this.pos.add(Left2MineUtilities.scaleVec3i(facing.getDirectionVec(), (double)i));
            if (this.world.getTileEntity(checkPos) instanceof TileEntityProximitySensor) {
               EnumFacing facingOther = (EnumFacing)this.world.getBlockState(checkPos).getValue(BlockProximitySensor.FACING);
               if (facingOther.getOpposite().equals(facing)) {
                  TileEntityProximitySensor sensor = (TileEntityProximitySensor)this.world.getTileEntity(checkPos);
                  if (sensor.getPairedLoc().equals(BlockPos.ORIGIN)) {
                     sensor.setPrimary(false);
                     sensor.setPairedLoc(this.pos);
                     this.setPrimary(true);
                     this.setPairedLoc(checkPos);
                     break;
                  }
               }
            }
         }

         this.updateLinkedList();
         Iterator var10 = this.linkedList.iterator();

         while(var10.hasNext()) {
            checkPos = (BlockPos)var10.next();
            if (this.world.getTileEntity(checkPos) instanceof TileEntityProximitySensor) {
               TileEntityProximitySensor sensor = (TileEntityProximitySensor)this.world.getTileEntity(checkPos);
               sensor.addLink(this.pos);
            }
         }

         this.setInit(true);
      }

      if (this.primary && !this.getPairedLoc().equals(BlockPos.ORIGIN) && !this.world.isRemote) {
         WorldDataLeft2Mine data = WorldDataLeft2Mine.get(this.world);
         if (this.world.getTileEntity(this.getPairedLoc()) instanceof TileEntityProximitySensor) {
            TileEntityProximitySensor sensor = (TileEntityProximitySensor)this.world.getTileEntity(this.getPairedLoc());
            if (sensor.getPairedLoc().equals(this.pos)) {
               boolean triggeredLocal = false;
               Iterator var14 = this.world.playerEntities.iterator();

               label72:
               while(true) {
                  int playerX;
                  int playerY;
                  int playerZ;
                  do {
                     do {
                        EntityPlayer player;
                        do {
                           do {
                              if (!var14.hasNext()) {
                                 break label72;
                              }

                              player = (EntityPlayer)var14.next();
                           } while(player.isCreative());
                        } while(player.isSpectator());

                        playerX = player.getPosition().getX();
                        playerY = player.getPosition().getY();
                        playerZ = player.getPosition().getZ();
                     } while(!Left2MineUtilities.isIntBetween(this.getPairedLoc().getX(), this.pos.getX(), playerX));
                  } while(!Left2MineUtilities.isIntBetween(this.getPairedLoc().getY(), this.pos.getY(), playerY) && !Left2MineUtilities.isIntBetween(this.getPairedLoc().getY(), this.pos.getY(), playerY + 1));

                  if (Left2MineUtilities.isIntBetween(this.getPairedLoc().getZ(), this.pos.getZ(), playerZ)) {
                     triggeredLocal = true;
                     break;
                  }
               }

               if (triggeredLocal) {
                  if (!this.isTriggered()) {
                     this.manualSetTriggered(true, this.world, this.pos);
                     sensor.manualSetTriggered(true, this.world, this.getPairedLoc());
                  }
               } else if (this.isTriggered() && !this.isRemote()) {
                  this.manualSetTriggered(false, this.world, this.pos);
                  sensor.manualSetTriggered(false, this.world, this.getPairedLoc());
               }
            }
         }
      }

   }

   public void manualSetTriggered(boolean triggered, World world, BlockPos pos) {
      this.triggered = triggered;
      Iterator var4 = this.linkedList.iterator();

      while(var4.hasNext()) {
         BlockPos link = (BlockPos)var4.next();
         if (world.getTileEntity(link) instanceof TileEntityProximitySensor) {
            TileEntityProximitySensor sensor = (TileEntityProximitySensor)world.getTileEntity(link);
            sensor.setTriggered(triggered, world, pos);
            if (!triggered) {
               sensor.setRemote(false);
            }
         }
      }

      this.markDirty();
      this.notifyUpdateRedstone();
   }

   public BlockPos getPairedLoc() {
      return this.pairedLoc;
   }

   public void setPairedLoc(BlockPos pairedLoc) {
      this.pairedLoc = pairedLoc;
      this.markDirty();
      this.notifyUpdate();
   }

   public void setPrimary(boolean primary) {
      this.primary = primary;
      this.markDirty();
      this.notifyUpdate();
   }

   public void setInit(boolean init) {
      this.init = init;
      this.markDirty();
      this.notifyUpdate();
   }

   public boolean isTriggered() {
      return this.triggered;
   }

   public void setTriggered(boolean triggered, World world, BlockPos pos) {
      this.triggered = triggered;
      if (triggered) {
         this.setRemote(triggered);
      }

      this.markDirty();
      this.notifyUpdateRedstone();
   }

   public void addLink(BlockPos link) {
      if (!this.linkedList.contains(link)) {
         this.linkedList.add(link);
         this.markDirty();
         this.notifyUpdate();
      }

   }

   public void removeLink(BlockPos link) {
      if (this.linkedList.remove(link)) {
         this.markDirty();
         this.notifyUpdate();
      }

   }

   public ArrayList<BlockPos> getLinkedList() {
      return this.linkedList;
   }

   public void updateLinkedList() {
      this.linkedList.clear();
      this.recursiveFindProximity(this.pos);
      this.markDirty();
      this.notifyUpdate();
   }

   private void recursiveFindProximity(BlockPos startPos) {
      Iterator var2 = Left2MineUtilities.getTouchingBlocks(startPos).iterator();

      while(var2.hasNext()) {
         BlockPos newPos = (BlockPos)var2.next();
         if (this.world.getTileEntity(newPos) instanceof TileEntityProximitySensor && !this.linkedList.contains(newPos) && !newPos.equals(this.pos)) {
            this.linkedList.add(newPos);
            this.recursiveFindProximity(newPos);
         }
      }

   }

   public boolean isRemote() {
      return this.isRemote;
   }

   public void setRemote(boolean isRemote) {
      this.isRemote = isRemote;
      this.markDirty();
      this.notifyUpdate();
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.pairedLoc = new BlockPos(nbt.getInteger("pairedX"), nbt.getInteger("pairedY"), nbt.getInteger("pairedZ"));
      this.primary = nbt.getBoolean("primary");
      this.init = nbt.getBoolean("init");
      this.triggered = nbt.getBoolean("triggered");
      this.isRemote = nbt.getBoolean("isRemote");
      NBTTagList tagList = (NBTTagList)nbt.getTag("linkedList");
      this.linkedList.clear();

      for(int i = 0; i < tagList.tagCount(); ++i) {
         NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
         this.linkedList.add(NBTUtil.getPosFromTag(nbttagcompound));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setInteger("pairedX", this.pairedLoc.getX());
      nbt.setInteger("pairedY", this.pairedLoc.getY());
      nbt.setInteger("pairedZ", this.pairedLoc.getZ());
      nbt.setBoolean("primary", this.primary);
      nbt.setBoolean("init", this.init);
      nbt.setBoolean("triggered", this.triggered);
      nbt.setBoolean("isRemote", this.isRemote);
      NBTTagList tagList = new NBTTagList();

      for(int i = 0; i < this.linkedList.size(); ++i) {
         BlockPos nodePos = (BlockPos)this.linkedList.get(i);
         NBTTagCompound nbttagcompound = NBTUtil.createPosTag(nodePos);
         tagList.appendTag(nbttagcompound);
      }

      nbt.setTag("linkedList", tagList);
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
