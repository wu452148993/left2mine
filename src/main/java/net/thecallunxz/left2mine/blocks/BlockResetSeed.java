package net.thecallunxz.left2mine.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityResetSeed;

public class BlockResetSeed extends BlockBase implements ITileEntityProvider {
   public BlockResetSeed(String name) {
      super(Material.IRON, name);
      this.hasTileEntity = true;
   }

   public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      int i = 0;
      TileEntity tileentity = blockAccess.getTileEntity(pos);
      if (tileentity instanceof TileEntityResetSeed) {
         if (((TileEntityResetSeed)tileentity).isTriggered()) {
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
      return new TileEntityResetSeed();
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      super.eventReceived(state, worldIn, pos, id, param);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      worldIn.removeTileEntity(pos);
   }
}
