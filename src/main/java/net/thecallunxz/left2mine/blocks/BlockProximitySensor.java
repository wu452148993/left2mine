package net.thecallunxz.left2mine.blocks;

import java.util.Iterator;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityProximitySensor;

public class BlockProximitySensor extends BlockBase implements ITileEntityProvider {
   public static final PropertyDirection FACING;

   public BlockProximitySensor(String name) {
      super(Material.IRON, name);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.hasTileEntity = true;
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING});
   }

   public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      int i = 0;
      TileEntity tileentity = blockAccess.getTileEntity(pos);
      if (tileentity instanceof TileEntityProximitySensor) {
         if (((TileEntityProximitySensor)tileentity).isTriggered()) {
            i = 15;
         } else {
            i = 0;
         }
      }

      return i;
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityProximitySensor();
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      super.eventReceived(state, worldIn, pos, id, param);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (worldIn.getTileEntity(pos) instanceof TileEntityProximitySensor) {
         TileEntityProximitySensor sensor = (TileEntityProximitySensor)worldIn.getTileEntity(pos);
         if (worldIn.getTileEntity(sensor.getPairedLoc()) instanceof TileEntityProximitySensor) {
            TileEntityProximitySensor pair = (TileEntityProximitySensor)worldIn.getTileEntity(sensor.getPairedLoc());
            pair.setPairedLoc(BlockPos.ORIGIN);
         }

         Iterator var8 = sensor.getLinkedList().iterator();

         while(var8.hasNext()) {
            BlockPos link = (BlockPos)var8.next();
            if (worldIn.getTileEntity(link) instanceof TileEntityProximitySensor) {
               TileEntityProximitySensor linkedsensor = (TileEntityProximitySensor)worldIn.getTileEntity(link);
               linkedsensor.removeLink(pos);
            }
         }
      }

      super.breakBlock(worldIn, pos, state);
      worldIn.removeTileEntity(pos);
   }

   static {
      FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
   }
}
