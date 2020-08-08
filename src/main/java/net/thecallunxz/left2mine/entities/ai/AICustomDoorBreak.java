package net.thecallunxz.left2mine.entities.ai;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.ForgeEventFactory;
import net.thecallunxz.left2mine.blocks.BlockFakeWall;
import net.thecallunxz.left2mine.blocks.BlockGameDoor;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityDoorSpawn;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityTank;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.init.InitBlocks;

public class AICustomDoorBreak extends AIGameDoorInteract {
   private int breakingTime;
   private int maxBreakTime;
   private int previousBreakProgress = -1;
   private boolean instaSmash;

   public AICustomDoorBreak(EntityLiving entityIn, boolean instaSmash) {
      super(entityIn);
      this.instaSmash = instaSmash;
      if (instaSmash) {
         this.maxBreakTime = 60;
      } else {
         this.maxBreakTime = 240;
      }

      if (entityIn instanceof EntityTank || entityIn instanceof EntityWitch) {
         this.maxBreakTime = 1;
      }

   }

   public boolean shouldExecute() {
      IBlockState iblockstate = this.entity.world.getBlockState(this.doorPosition);
      if (!super.shouldExecute()) {
         return false;
      } else if (ForgeEventFactory.onEntityDestroyBlock(this.entity, this.doorPosition, this.entity.world.getBlockState(this.doorPosition)) && this.entity.getAttackTarget() != null) {
         if (iblockstate.getMaterial() != Material.WOOD) {
            return false;
         } else {
            return !BlockGameDoor.isOpen(this.entity.world, this.doorPosition);
         }
      } else {
         return false;
      }
   }

   public void startExecuting() {
      super.startExecuting();
      this.breakingTime = 0;
   }

   private BlockGameDoor getBlockGameDoor(BlockPos pos) {
      IBlockState iblockstate = this.entity.world.getBlockState(pos);
      Block block = iblockstate.getBlock();
      return block instanceof BlockGameDoor && iblockstate.getMaterial() == Material.WOOD ? (BlockGameDoor)block : null;
   }

   public boolean shouldContinueExecuting() {
      double d0 = this.entity.getDistanceSq(this.doorPosition);
      if (this.getBlockGameDoor(this.doorPosition) == null) {
         return false;
      } else {
         boolean flag;
         if (this.breakingTime <= this.maxBreakTime) {
            BlockGameDoor BlockGameDoor = this.doorBlock;
            if (!BlockGameDoor.isOpen(this.entity.world, this.doorPosition) && d0 < 4.0D) {
               flag = true;
               return flag;
            }
         }

         if (this.instaSmash && this.breakingTime > this.maxBreakTime) {
            return false;
         } else {
            flag = false;
            return flag;
         }
      }
   }

   public void resetTask() {
      super.resetTask();
      this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, -1);
   }

   public void updateTask() {
      super.updateTask();
      if (this.entity.getRNG().nextInt(20) == 0) {
         Random rand = new Random();
         this.entity.swingArm(rand.nextBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
         this.entity.world.playEvent(1019, this.doorPosition, 0);
      }

      ++this.breakingTime;
      int i = (int)((float)this.breakingTime / (float)this.maxBreakTime * 10.0F);
      if (i != this.previousBreakProgress) {
         this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, i);
         this.previousBreakProgress = i;
      }

      IBlockState oldState = this.entity.world.getBlockState(this.doorPosition.down());
      if (oldState.getBlock() instanceof BlockFakeWall) {
         this.breakWallBlock(oldState);
      } else if (this.breakingTime >= this.maxBreakTime) {
         this.breakDoorBlock(oldState);
      }
   }

   private void breakDoorBlock(IBlockState oldState) {
      if (oldState.getBlock() instanceof BlockGameDoor) {
         this.entity.world.setBlockState(this.doorPosition.down(), InitBlocks.doorspawn.getStateFromFacing(((EnumFacing)oldState.getValue(BlockGameDoor.FACING)).getOpposite()));
         this.entity.world.playEvent(1021, this.doorPosition, 0);
         if (this.entity.world.getTileEntity(this.doorPosition.down()) instanceof TileEntityDoorSpawn) {
            TileEntityDoorSpawn spawner = (TileEntityDoorSpawn)this.entity.world.getTileEntity(this.doorPosition.down());
            spawner.setDoorType(Block.getIdFromBlock(oldState.getBlock()));
            spawner.setRightHinge(oldState.getValue(BlockGameDoor.HINGE) == BlockGameDoor.EnumHingePosition.RIGHT);
         }
      }

   }

   private void breakWallBlock(IBlockState oldState) {
      BlockFakeWall wall = (BlockFakeWall)oldState.getBlock();
      wall.onWallDestroy(this.entity.world, this.doorPosition);
   }
}
