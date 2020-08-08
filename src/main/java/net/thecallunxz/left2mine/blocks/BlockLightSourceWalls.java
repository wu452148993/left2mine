package net.thecallunxz.left2mine.blocks;

import javax.annotation.Nullable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.Main;

public class BlockLightSourceWalls extends BlockCustomDirectional {
   protected static final AxisAlignedBB AABB_DOWN_OFF = new AxisAlignedBB(0.3125D, 0.875D, 0.375D, 0.6875D, 1.0D, 0.625D);
   protected static final AxisAlignedBB AABB_UP_OFF = new AxisAlignedBB(0.3125D, 0.0D, 0.375D, 0.6875D, 0.125D, 0.625D);
   protected static final AxisAlignedBB AABB_NORTH_OFF = new AxisAlignedBB(0.3125D, 0.375D, 0.875D, 0.6875D, 0.625D, 1.0D);
   protected static final AxisAlignedBB AABB_SOUTH_OFF = new AxisAlignedBB(0.3125D, 0.375D, 0.0D, 0.6875D, 0.625D, 0.125D);
   protected static final AxisAlignedBB AABB_WEST_OFF = new AxisAlignedBB(0.875D, 0.375D, 0.3125D, 1.0D, 0.625D, 0.6875D);
   protected static final AxisAlignedBB AABB_EAST_OFF = new AxisAlignedBB(0.0D, 0.375D, 0.3125D, 0.125D, 0.625D, 0.6875D);

   public BlockLightSourceWalls(String name) {
      super(Main.SURFACE, name);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setLightLevel(0.85F);
      this.setCreativeTab(Main.tabL2M3);
   }

   public BlockLightSourceWalls(String name, float lightLevel) {
      super(Main.SURFACE, name);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setLightLevel(lightLevel);
      this.setCreativeTab(Main.tabL2M3);
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return true;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return true;
   }

   protected static boolean canPlaceBlock(World worldIn, BlockPos pos, EnumFacing direction) {
      return true;
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return canPlaceBlock(worldIn, pos, facing) ? this.getDefaultState().withProperty(FACING, facing) : this.getDefaultState().withProperty(FACING, EnumFacing.DOWN);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
      switch(enumfacing) {
      case EAST:
         return AABB_EAST_OFF;
      case WEST:
         return AABB_WEST_OFF;
      case SOUTH:
         return AABB_SOUTH_OFF;
      case NORTH:
      default:
         return AABB_NORTH_OFF;
      case UP:
         return AABB_UP_OFF;
      case DOWN:
         return AABB_DOWN_OFF;
      }
   }

   private void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing) {
      worldIn.notifyNeighborsOfStateChange(pos, this, false);
      worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this, false);
   }

   public IBlockState getStateFromMeta(int meta) {
      EnumFacing enumfacing;
      switch(meta & 7) {
      case 0:
         enumfacing = EnumFacing.DOWN;
         break;
      case 1:
         enumfacing = EnumFacing.EAST;
         break;
      case 2:
         enumfacing = EnumFacing.WEST;
         break;
      case 3:
         enumfacing = EnumFacing.SOUTH;
         break;
      case 4:
         enumfacing = EnumFacing.NORTH;
         break;
      case 5:
      default:
         enumfacing = EnumFacing.UP;
      }

      return this.getDefaultState().withProperty(FACING, enumfacing);
   }

   public int getMetaFromState(IBlockState state) {
      byte i;
      switch((EnumFacing)state.getValue(FACING)) {
      case EAST:
         i = 1;
         break;
      case WEST:
         i = 2;
         break;
      case SOUTH:
         i = 3;
         break;
      case NORTH:
         i = 4;
         break;
      case UP:
      default:
         i = 5;
         break;
      case DOWN:
         i = 0;
      }

      return i;
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING});
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
