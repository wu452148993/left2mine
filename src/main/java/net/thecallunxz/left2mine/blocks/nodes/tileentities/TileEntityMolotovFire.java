package net.thecallunxz.left2mine.blocks.nodes.tileentities;

import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockLiquid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.BlockMolotovFire;
import net.thecallunxz.left2mine.init.InitBlocks;

public class TileEntityMolotovFire extends TileEntity implements ITickable {
   private long seed = 0L;
   private boolean seedSet = false;
   private boolean init = false;
   private boolean floating = false;
   private long timeToDisappear = 0L;
   private int fireID = 4;

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
      WorldDataLeft2Mine data;
      if (!this.init && this.world != null && !this.seedSet) {
         this.setInit(true);
         if (!this.world.isRemote) {
            data = WorldDataLeft2Mine.get(this.world);
            this.setSeed((long)data.getGameSeed());
            this.setInit(true);
            this.setTime(this.world.getTotalWorldTime() + (long)this.world.rand.nextInt(50) + 250L);
            if (this.isEmptyPos(this.pos.down()) && this.isEmptyPos(this.pos.down().down())) {
               this.world.setBlockToAir(this.pos);
            }

            if (this.isEmptyPos(this.pos.down())) {
               this.setFloating(true);
            }

            if (this.fireID > 0) {
               for(int i = 0; i < 4; ++i) {
                  BlockPos nextPos = null;
                  switch(i) {
                  case 0:
                     nextPos = this.pos.add(1, 0, 0);
                     break;
                  case 1:
                     nextPos = this.pos.add(-1, 0, 0);
                     break;
                  case 2:
                     nextPos = this.pos.add(0, 0, 1);
                     break;
                  case 3:
                     nextPos = this.pos.add(0, 0, -1);
                  }

                  if (!(this.world.getBlockState(nextPos.up()).getBlock() instanceof BlockMolotovFire) && !(this.world.getBlockState(nextPos.up()).getBlock() instanceof BlockLiquid) && !(this.world.getBlockState(nextPos).getBlock() instanceof BlockMolotovFire) && !(this.world.getBlockState(nextPos).getBlock() instanceof BlockLiquid) && !(this.world.getBlockState(nextPos.down()).getBlock() instanceof BlockMolotovFire) && !(this.world.getBlockState(nextPos.down()).getBlock() instanceof BlockLiquid) && !(this.world.getBlockState(nextPos.down().down()).getBlock() instanceof BlockMolotovFire) && !(this.world.getBlockState(nextPos.down().down()).getBlock() instanceof BlockLiquid)) {
                     BlockPos firePos = null;
                     if (this.floating) {
                        firePos = this.getBestFirePos(nextPos.down().down());
                     } else {
                        firePos = this.getBestFirePos(nextPos.down());
                     }

                     if (firePos != null) {
                        this.world.setBlockState(firePos, InitBlocks.molotovfire.getDefaultState());
                        if (this.world.getTileEntity(firePos) instanceof TileEntityMolotovFire) {
                           TileEntityMolotovFire fireTile = (TileEntityMolotovFire)this.world.getTileEntity(firePos);
                           fireTile.setFireID(this.fireID - 1);
                        }
                     }
                  }
               }
            }
         }
      }

      if (this.world != null && this.seedSet && !this.world.isRemote) {
         data = WorldDataLeft2Mine.get(this.world);
         if (this.seed != (long)data.getGameSeed() || this.getTime() <= this.world.getTotalWorldTime()) {
            this.world.setBlockToAir(this.pos);
         }
      }

      if (this.floating) {
         Main.proxy.renderFireParticles(this.world, this.pos.down());
      } else {
         Main.proxy.renderFireParticles(this.world, this.pos);
      }

   }

   public boolean isEmptyPos(BlockPos pos) {
      return this.world.getBlockState(pos).getCollisionBoundingBox(this.world, pos) == null || this.world.getBlockState(pos).getBlock() instanceof BlockCarpet;
   }

   public BlockPos getBestFirePos(BlockPos pos) {
      BlockPos firePos = null;

      for(int i = 0; i < 4; ++i) {
         if (this.world.isAirBlock(pos.up(i))) {
            if (!this.isEmptyPos(pos.up(i).down()) && i < 3) {
               if (this.isEmptyPos(pos.up(i).up())) {
                  firePos = pos.up(i);
               }
            } else if (!this.isEmptyPos(pos.up(i).down().down()) && this.isEmptyPos(pos.up(i).down())) {
               firePos = pos.up(i);
            }
         }
      }

      return firePos;
   }

   public int getFireID() {
      return this.fireID;
   }

   public void setFireID(int fireID) {
      this.fireID = fireID;
      this.markDirty();
      this.notifyUpdate();
   }

   public void setInit(boolean init) {
      this.init = init;
      this.markDirty();
      this.notifyUpdate();
   }

   public void setFloating(boolean init) {
      this.floating = init;
      this.markDirty();
      this.notifyUpdate();
   }

   public boolean getFloating() {
      return this.floating;
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

   public long getTime() {
      return this.timeToDisappear;
   }

   public void setTime(long timeToDisappear) {
      this.timeToDisappear = timeToDisappear;
      this.markDirty();
      this.notifyUpdate();
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.seed = nbt.getLong("seed");
      this.seedSet = nbt.getBoolean("seedSet");
      this.init = nbt.getBoolean("init");
      this.timeToDisappear = nbt.getLong("timeToDisappear");
      this.fireID = nbt.getInteger("fireID");
      this.floating = nbt.getBoolean("floating");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setLong("seed", this.seed);
      nbt.setBoolean("seedSet", this.seedSet);
      nbt.setBoolean("init", this.init);
      nbt.setLong("timeToDisappear", this.timeToDisappear);
      nbt.setInteger("fireID", this.fireID);
      nbt.setBoolean("floating", this.floating);
      return super.writeToNBT(nbt);
   }

   protected void notifyUpdate() {
      this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 0);
   }
}
