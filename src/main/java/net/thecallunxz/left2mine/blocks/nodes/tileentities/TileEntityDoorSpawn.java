package net.thecallunxz.left2mine.blocks.nodes.tileentities;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.BlockFakeWall;
import net.thecallunxz.left2mine.blocks.BlockGameDoor;

public class TileEntityDoorSpawn extends TileEntity implements ITickable {
   private int doorSeed = 0;
   private int doorType = -1;
   private boolean rightHinge = false;

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
      Main.proxy.renderNodeParticles(this.getWorld(), this.pos, Item.getItemFromBlock(this.getWorld().getBlockState(this.pos).getBlock()), true, 1.0F, 1.0F, 1.0F);
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(this.world);
      if (!this.world.isRemote) {
         if (this.doorSeed == 0) {
            this.doorSeed = data.getGameSeed();
            this.setDoorSeed(data.getGameSeed());
         }

         if (data.getGameSeed() != this.doorSeed) {
            EnumFacing facing = (EnumFacing)this.world.getBlockState(this.pos).getValue(BlockGameDoor.FACING);
            Block blockToPlace = Block.getBlockById(this.getDoorType());
            if (blockToPlace != null && blockToPlace instanceof BlockGameDoor) {
               this.world.setBlockToAir(this.pos);
               if (blockToPlace instanceof BlockFakeWall) {
                  placeWall(this.world, this.pos, facing.getOpposite(), blockToPlace);
               } else {
                  placeDoor(this.world, this.pos, facing.getOpposite(), blockToPlace, this.isRightHinge());
               }
            }
         }
      }

   }

   public static void placeDoor(World worldIn, BlockPos pos, EnumFacing facing, Block door, boolean isRightHinge) {
      BlockPos blockpos = pos.offset(facing.rotateY());
      BlockPos blockpos1 = pos.offset(facing.rotateYCCW());
      int i = (worldIn.getBlockState(blockpos1).isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos1.up()).isNormalCube() ? 1 : 0);
      int j = (worldIn.getBlockState(blockpos).isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos.up()).isNormalCube() ? 1 : 0);
      boolean flag = worldIn.getBlockState(blockpos1).getBlock() == door || worldIn.getBlockState(blockpos1.up()).getBlock() == door;
      boolean flag1 = worldIn.getBlockState(blockpos).getBlock() == door || worldIn.getBlockState(blockpos.up()).getBlock() == door;
      if ((!flag || flag1) && j <= i) {
         if (flag1 && !flag || j < i) {
            isRightHinge = false;
         }
      } else {
         isRightHinge = true;
      }

      BlockPos blockpos2 = pos.up();
      boolean flag2 = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockpos2);
      IBlockState iblockstate = door.getDefaultState().withProperty(BlockGameDoor.FACING, facing).withProperty(BlockGameDoor.HINGE, isRightHinge ? BlockGameDoor.EnumHingePosition.RIGHT : BlockGameDoor.EnumHingePosition.LEFT).withProperty(BlockGameDoor.POWERED, flag2).withProperty(BlockGameDoor.OPEN, flag2);
      worldIn.setBlockState(pos, iblockstate.withProperty(BlockGameDoor.HALF, BlockGameDoor.EnumDoorHalf.LOWER), 2);
      worldIn.setBlockState(blockpos2, iblockstate.withProperty(BlockGameDoor.HALF, BlockGameDoor.EnumDoorHalf.UPPER), 2);
   }

   public static void placeWall(World worldIn, BlockPos pos, EnumFacing facing, Block door) {
      BlockPos blockpos = pos.offset(facing.rotateY());
      BlockPos blockpos1 = pos.offset(facing.rotateYCCW());
      int i = (worldIn.getBlockState(blockpos1).isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos1.up()).isNormalCube() ? 1 : 0);
      int j = (worldIn.getBlockState(blockpos).isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos.up()).isNormalCube() ? 1 : 0);
      boolean var10000;
      if (worldIn.getBlockState(blockpos1).getBlock() != door && worldIn.getBlockState(blockpos1.up()).getBlock() != door) {
         var10000 = false;
      } else {
         var10000 = true;
      }

      if (worldIn.getBlockState(blockpos).getBlock() != door && worldIn.getBlockState(blockpos.up()).getBlock() != door) {
         var10000 = false;
      } else {
         var10000 = true;
      }

      BlockPos blockpos2 = pos.up();
      if (!worldIn.isBlockPowered(pos) && !worldIn.isBlockPowered(blockpos2)) {
         var10000 = false;
      } else {
         var10000 = true;
      }

      IBlockState iblockstate = door.getDefaultState().withProperty(BlockGameDoor.FACING, facing);
      worldIn.setBlockState(pos, iblockstate, 2);
      worldIn.setBlockState(blockpos2, iblockstate, 2);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.doorSeed = nbt.getInteger("doorSeed");
      this.doorType = nbt.getInteger("doorType");
      this.rightHinge = nbt.getBoolean("rightHinge");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setInteger("doorSeed", this.doorSeed);
      nbt.setInteger("doorType", this.doorType);
      nbt.setBoolean("rightHinge", this.rightHinge);
      return super.writeToNBT(nbt);
   }

   public void setDoorSeed(int doorSeed) {
      this.doorSeed = doorSeed;
      this.markDirty();
      this.notifyUpdate();
   }

   public int getDoorType() {
      return this.doorType;
   }

   public void setDoorType(int doorType) {
      this.doorType = doorType;
      this.markDirty();
      this.notifyUpdate();
   }

   public boolean isRightHinge() {
      return this.rightHinge;
   }

   public void setRightHinge(boolean rightHinge) {
      this.rightHinge = rightHinge;
      this.markDirty();
      this.notifyUpdate();
   }

   protected void notifyUpdate() {
      this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 0);
   }
}
