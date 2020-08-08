package net.thecallunxz.left2mine.blocks.nodeblocks;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.nodes.BlockNodeChild;
import net.thecallunxz.left2mine.blocks.nodes.NodeSelection;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeChild;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeParent;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class BlockBossSpawn extends BlockNodeChild {
   public BlockBossSpawn(String name) {
      super(name);
   }

   public void update(BlockPos pos, World world) {
      TileEntityNodeChild node = (TileEntityNodeChild)world.getTileEntity(pos);
      if (node.isActive() && this.canBlockBeSeen(pos, world)) {
         WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
         node.setActive(false);
         data.removeBossNode(pos);
      }

   }

   public boolean canBlockBeSeen(BlockPos pos, World world) {
      Predicate<Entity> predicate = EntitySelectors.CAN_AI_TARGET;
      EntityPlayer player = world.getClosestPlayer((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 50.0D, predicate);
      if (player == null) {
         return false;
      } else {
         return Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D), world, 0) == null || Left2MineUtilities.RayCastCheckOpaque(new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 1.7D, (double)pos.getZ() + 0.5D), world, 0) == null;
      }
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      if (NodeSelection.getSelectedNode() != null && NodeSelection.isNodeSelected()) {
         TileEntity tileentity = worldIn.getTileEntity(NodeSelection.getSelectedNode());
         if (tileentity instanceof TileEntityNodeParent) {
            ((TileEntityNodeParent)tileentity).addChildNode(pos);
         }
      }

      return true;
   }
}
