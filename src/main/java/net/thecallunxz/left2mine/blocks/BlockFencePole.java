package net.thecallunxz.left2mine.blocks;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.Main;

public class BlockFencePole extends BlockBase {
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   private String name;
   protected static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[]{new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
   public static final AxisAlignedBB PILLAR_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.5D, 0.625D);
   public static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.625D, 0.625D, 1.5D, 1.0D);
   public static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.375D, 1.5D, 0.625D);
   public static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.5D, 0.375D);
   public static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.625D, 0.0D, 0.375D, 1.0D, 1.5D, 0.625D);

   public BlockFencePole(String name) {
      super(Material.ROCK, name);
      this.setName(name);
      this.setCreativeTab(Main.tabL2M3);
      this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false));
   }

   public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
      if (!p_185477_7_) {
         state = state.getActualState(worldIn, pos);
      }

      addCollisionBoxToList(pos, entityBox, collidingBoxes, PILLAR_AABB);
      if ((Boolean)state.getValue(NORTH)) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
      }

      if ((Boolean)state.getValue(EAST)) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
      }

      if ((Boolean)state.getValue(SOUTH)) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
      }

      if ((Boolean)state.getValue(WEST)) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
      }

   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      state = this.getActualState(state, source, pos);
      return BOUNDING_BOXES[getBoundingBoxIdx(state)];
   }

   private static int getBoundingBoxIdx(IBlockState state) {
      int i = 0;
      if ((Boolean)state.getValue(NORTH)) {
         i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
      }

      if ((Boolean)state.getValue(EAST)) {
         i |= 1 << EnumFacing.EAST.getHorizontalIndex();
      }

      if ((Boolean)state.getValue(SOUTH)) {
         i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
      }

      if ((Boolean)state.getValue(WEST)) {
         i |= 1 << EnumFacing.WEST.getHorizontalIndex();
      }

      return i;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }

   public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing p_176524_3_) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos, p_176524_3_);
      Block block = iblockstate.getBlock();
      boolean flag = blockfaceshape == BlockFaceShape.MIDDLE_POLE && (iblockstate.getMaterial() == this.blockMaterial || block instanceof BlockFenceGate);
      return !isExcepBlockForAttachWithPiston(block) && blockfaceshape == BlockFaceShape.SOLID || flag;
   }

   protected static boolean isExcepBlockForAttachWithPiston(Block p_194142_0_) {
      return Block.isExceptBlockForAttachWithPiston(p_194142_0_) || p_194142_0_ == Blocks.BARRIER || p_194142_0_ == Blocks.MELON_BLOCK || p_194142_0_ == Blocks.PUMPKIN || p_194142_0_ == Blocks.LIT_PUMPKIN;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return true;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         return ItemLead.attachToFence(playerIn, worldIn, pos);
      } else {
         ItemStack itemstack = playerIn.getHeldItem(hand);
         return itemstack.getItem() == Items.LEAD || itemstack.isEmpty();
      }
   }

   public int getMetaFromState(IBlockState state) {
      return 0;
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(NORTH, this.canFenceConnectTo(worldIn, pos, EnumFacing.NORTH)).withProperty(EAST, this.canFenceConnectTo(worldIn, pos, EnumFacing.EAST)).withProperty(SOUTH, this.canFenceConnectTo(worldIn, pos, EnumFacing.SOUTH)).withProperty(WEST, this.canFenceConnectTo(worldIn, pos, EnumFacing.WEST));
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      switch(rot) {
      case CLOCKWISE_180:
         return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST));
      case COUNTERCLOCKWISE_90:
         return state.withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH));
      case CLOCKWISE_90:
         return state.withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH));
      default:
         return state;
      }
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      switch(mirrorIn) {
      case LEFT_RIGHT:
         return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH));
      case FRONT_BACK:
         return state.withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST));
      default:
         return super.withMirror(state, mirrorIn);
      }
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{NORTH, EAST, WEST, SOUTH});
   }

   public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
      Block connector = world.getBlockState(pos.offset(facing)).getBlock();
      if (connector instanceof BlockFencePole) {
         if (this != Blocks.NETHER_BRICK_FENCE && connector == Blocks.NETHER_BRICK_FENCE) {
            return false;
         } else {
            return this != Blocks.NETHER_BRICK_FENCE || connector == Blocks.NETHER_BRICK_FENCE;
         }
      } else {
         return false;
      }
   }

   private boolean canFenceConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
      BlockPos other = pos.offset(facing);
      Block block = world.getBlockState(other).getBlock();
      if (block instanceof BlockFencePole) {
         BlockFencePole fencePole = (BlockFencePole)block;
         if (fencePole.getName() == this.getName()) {
            return block.canBeConnectedTo(world, other, facing.getOpposite()) || this.canConnectTo(world, other, facing.getOpposite());
         }
      }

      return false;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return p_193383_4_ != EnumFacing.UP && p_193383_4_ != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
