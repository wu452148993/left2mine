package net.thecallunxz.left2mine.blocks.nodes;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.blocks.BlockBase;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeChild;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeParent;

public abstract class BlockNodeChild extends BlockBase implements ITileEntityProvider {
   protected static final AxisAlignedBB BOUND_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0525D, 0.9375D);

   public BlockNodeChild(String name) {
      super(Main.SURFACE, name);
      this.setHardness(-1.0F);
      this.setBlockUnbreakable();
      this.setResistance(6000001.0F);
      this.translucent = true;
      this.hasTileEntity = true;
   }

   public void update(BlockPos pos, World world) {
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   public BlockNodeChild setCreativeTab(CreativeTabs tab) {
      super.setCreativeTab(tab);
      return this;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return BOUND_AABB;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      if (super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos.down()).isTopSolid()) {
         if (NodeSelection.getSelectedNode() != null && NodeSelection.isNodeSelected()) {
            TileEntity tileentity = worldIn.getTileEntity(NodeSelection.getSelectedNode());
            if (tileentity instanceof TileEntityNodeParent) {
               ((TileEntityNodeParent)tileentity).addChildNode(pos);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      return false;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.getBlockState(pos.down()).isTopSolid()) {
         worldIn.setBlockToAir(pos);
      }

   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return NodeSelection.isNodeSelected() ? new TileEntityNodeChild(NodeSelection.getSelectedNode()) : new TileEntityNodeChild();
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      super.eventReceived(state, worldIn, pos, id, param);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntityNodeChild node = (TileEntityNodeChild)worldIn.getTileEntity(pos);
      if (node != null) {
         BlockPos nodePos = node.getConnectedNode();
         TileEntityNodeParent parentNode = (TileEntityNodeParent)worldIn.getTileEntity(nodePos);
         if (parentNode != null) {
            parentNode.removeChildNode(pos);
         }
      }

      super.breakBlock(worldIn, pos, state);
      if (worldIn.getTileEntity(pos) != null) {
         worldIn.removeTileEntity(pos);
      }

   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
