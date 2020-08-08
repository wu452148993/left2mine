package net.thecallunxz.left2mine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityDoorSpawn;
import net.thecallunxz.left2mine.init.InitBlocks;

public class BlockFakeWall extends BlockGameDoor {
   public static final PropertyDirection FACING;
   public static final UnlistedPropertyCopiedBlock COPIEDBLOCK;

   public BlockFakeWall(String name) {
      super(Material.WOOD, name, true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
   }

   public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
      return EnumBlockRenderType.MODEL;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      return false;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
   }

   public static boolean isOpen(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return true;
   }

   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      BlockPos blockpos = pos.down();
      BlockPos blockpos1 = pos.up();
      if (worldIn.getBlockState(blockpos).getBlock() instanceof BlockFakeWall) {
         worldIn.setBlockToAir(blockpos);
      }

      if (worldIn.getBlockState(blockpos1).getBlock() instanceof BlockFakeWall) {
         worldIn.setBlockToAir(blockpos1);
      }

   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      state = state.getActualState(source, pos);
      EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
      switch(enumfacing) {
      case EAST:
      default:
         return EAST_AABB;
      case SOUTH:
         return SOUTH_AABB;
      case WEST:
         return WEST_AABB;
      case NORTH:
         return NORTH_AABB;
      }
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state;
   }

   public IBlockState getStateFromFacing(EnumFacing facing) {
      return this.getDefaultState().withProperty(FACING, facing);
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
   }

   protected BlockStateContainer createBlockState() {
      IProperty[] listedProperties = new IProperty[]{FACING};
      IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[]{COPIEDBLOCK};
      return new ExtendedBlockState(this, listedProperties, unlistedProperties);
   }

   public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
      if (state instanceof IExtendedBlockState) {
         IExtendedBlockState retval = (IExtendedBlockState)state;
         IBlockState bestAdjacentBlock = this.selectBestAdjacentBlock(world, pos);
         retval = retval.withProperty(COPIEDBLOCK, bestAdjacentBlock);
         return retval;
      } else {
         return state;
      }
   }

   private IBlockState selectBestAdjacentBlock(IBlockAccess world, BlockPos blockPos) {
      BlockPos down = blockPos.down();
      IBlockState state = Blocks.AIR.getDefaultState();

      for(int i = 0; i < 5; ++i) {
         if (world.getBlockState(down).isOpaqueCube()) {
            state = world.getBlockState(down);
            break;
         }

         down = down.down();
      }

      return state;
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return null;
   }

   public void onWallDestroy(World world, BlockPos doorPosition) {
      IBlockState oldState = world.getBlockState(doorPosition.down());
      if (oldState.getBlock() instanceof BlockGameDoor) {
         world.setBlockToAir(doorPosition);
         world.setBlockState(doorPosition.down(), InitBlocks.doorspawn.getStateFromFacing(((EnumFacing)oldState.getValue(BlockGameDoor.FACING)).getOpposite()));
         world.playEvent(1021, doorPosition, 0);
         if (world.getTileEntity(doorPosition.down()) instanceof TileEntityDoorSpawn) {
            TileEntityDoorSpawn spawner = (TileEntityDoorSpawn)world.getTileEntity(doorPosition.down());
            spawner.setDoorType(Block.getIdFromBlock(oldState.getBlock()));
         }
      }

      if (world.getBlockState(doorPosition.north()).getBlock() instanceof BlockFakeWall) {
         this.onWallDestroy(world, doorPosition.north());
      }

      if (world.getBlockState(doorPosition.east()).getBlock() instanceof BlockFakeWall) {
         this.onWallDestroy(world, doorPosition.east());
      }

      if (world.getBlockState(doorPosition.south()).getBlock() instanceof BlockFakeWall) {
         this.onWallDestroy(world, doorPosition.south());
      }

      if (world.getBlockState(doorPosition.west()).getBlock() instanceof BlockFakeWall) {
         this.onWallDestroy(world, doorPosition.west());
      }

   }

   static {
      FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
      COPIEDBLOCK = new UnlistedPropertyCopiedBlock();
   }
}
