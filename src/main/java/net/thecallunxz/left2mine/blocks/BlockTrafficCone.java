package net.thecallunxz.left2mine.blocks;

import javax.annotation.Nullable;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.thecallunxz.left2mine.Main;

public class BlockTrafficCone extends BlockBase {
   protected static final AxisAlignedBB BOUND_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.5D, 1.0D);
   private boolean randomPos;

   public BlockTrafficCone(String string, boolean randomPos) {
      super(string);
      this.randomPos = randomPos;
      this.setCreativeTab(Main.tabL2M3);
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return BOUND_AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public EnumOffsetType getOffsetType() {
      return this.randomPos ? EnumOffsetType.XZ : EnumOffsetType.NONE;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
