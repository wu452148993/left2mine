package net.thecallunxz.left2mine.blocks.nodeblocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.BlockBase;
import net.thecallunxz.left2mine.blocks.nodes.NodeSelection;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntitySaferoomDoor;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntitySaferoomNode;

public class BlockSaferoomDoorNode extends BlockBase implements ITileEntityProvider {
   public BlockSaferoomDoorNode(String name) {
      super(Material.IRON, name);
      this.setHardness(-1.0F);
      this.setBlockUnbreakable();
      this.setResistance(6000001.0F);
   }

   public BlockBase setCreativeTab(CreativeTabs tab) {
      return this;
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return NodeSelection.isNodeSelected() ? new TileEntitySaferoomDoor(NodeSelection.getSelectedNode(), WorldDataLeft2Mine.get(worldIn).getGameSeed()) : new TileEntitySaferoomDoor();
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      super.eventReceived(state, worldIn, pos, id, param);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntitySaferoomDoor tileEnt = (TileEntitySaferoomDoor)worldIn.getTileEntity(pos);
      TileEntitySaferoomNode tileEntPar = (TileEntitySaferoomNode)worldIn.getTileEntity(((TileEntitySaferoomDoor)worldIn.getTileEntity(pos)).getConnectedNode());
      if (tileEntPar != null) {
         if (tileEnt.getEntrance()) {
            tileEntPar.setEntrance(new BlockPos(0, 0, 0));
         } else {
            tileEntPar.setExit(new BlockPos(0, 0, 0));
         }
      }

      super.breakBlock(worldIn, pos, state);
      worldIn.removeTileEntity(pos);
   }
}
