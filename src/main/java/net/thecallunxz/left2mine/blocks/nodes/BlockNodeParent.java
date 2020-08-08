package net.thecallunxz.left2mine.blocks.nodes;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
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
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.BlockBase;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeChild;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeParent;

public abstract class BlockNodeParent extends BlockBase implements ITileEntityProvider {
   protected static final AxisAlignedBB BOUND_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0525D, 0.9375D);

   public BlockNodeParent(String name) {
      super(Main.SURFACE, name);
      this.setHardness(-1.0F);
      this.setBlockUnbreakable();
      this.setResistance(6000001.0F);
      this.translucent = true;
      this.hasTileEntity = true;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   public BlockNodeParent setCreativeTab(CreativeTabs tab) {
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

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(worldIn);
      if (NodeSelection.isNodeSelected()) {
         if (NodeSelection.getSelectedNode().equals(pos)) {
            return false;
         }

         NodeSelection.setSelectedNode(pos);
      } else {
         NodeSelection.setSelectedNode(pos);
      }

      return true;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos.down()).isTopSolid();
   }

   public void clearSelectedNode(World worldIn, BlockPos pos) {
      if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntityNodeParent) {
         TileEntityNodeParent tileentity = (TileEntityNodeParent)worldIn.getTileEntity(pos);
         Iterator var4 = tileentity.getChildNodes().iterator();

         while(var4.hasNext()) {
            BlockPos pos2 = (BlockPos)var4.next();
            if (worldIn.getTileEntity(pos2) instanceof TileEntityNodeChild) {
               TileEntityNodeChild tileentity2 = (TileEntityNodeChild)worldIn.getTileEntity(pos2);
               tileentity2.setColours(1.0F, 1.0F, 1.0F);
            }
         }
      }

      if (NodeSelection.isNodeSelected() && NodeSelection.getSelectedNode().equals(pos)) {
         NodeSelection.clearNode();
      }

   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityNodeParent();
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      super.eventReceived(state, worldIn, pos, id, param);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      this.clearSelectedNode(worldIn, pos);
      super.breakBlock(worldIn, pos, state);
      if (worldIn.getTileEntity(pos) != null) {
         worldIn.removeTileEntity(pos);
      }

   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
