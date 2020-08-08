package net.thecallunxz.left2mine.items.usable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.blocks.BlockSaferoomDoor;
import net.thecallunxz.left2mine.blocks.nodes.NodeSelection;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntitySaferoomNode;
import net.thecallunxz.left2mine.init.InitBlocks;
import net.thecallunxz.left2mine.items.ItemBase;

public class ItemSaferoomDoor extends ItemBase {
   private final Block block;

   public ItemSaferoomDoor(String name, ItemBase.EnumItemType type) {
      super(name, type);
      this.block = InitBlocks.saferoomdoor;
      this.setCreativeTab(Main.tabL2M);
   }

   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (facing != EnumFacing.UP) {
         return EnumActionResult.FAIL;
      } else {
         if (NodeSelection.isNodeSelected() && worldIn.getTileEntity(NodeSelection.getSelectedNode()) instanceof TileEntitySaferoomNode) {
            TileEntitySaferoomNode tileentity = (TileEntitySaferoomNode)worldIn.getTileEntity(NodeSelection.getSelectedNode());
            boolean flagEntrance = tileentity.getEntrance().getX() == 0 && tileentity.getEntrance().getY() == 0 && tileentity.getEntrance().getZ() == 0;
            boolean flagExit = tileentity.getExit().getX() == 0 && tileentity.getExit().getY() == 0 && tileentity.getExit().getZ() == 0;
            if (flagEntrance || flagExit) {
               IBlockState iblockstate = worldIn.getBlockState(pos);
               Block block = iblockstate.getBlock();
               if (!block.isReplaceable(worldIn, pos)) {
                  pos = pos.offset(facing);
               }

               ItemStack itemstack = player.getHeldItem(hand);
               if (player.canPlayerEdit(pos, facing, itemstack) && this.block.canPlaceBlockAt(worldIn, pos)) {
                  EnumFacing enumfacing = EnumFacing.fromAngle((double)player.rotationYaw);
                  int i = enumfacing.getFrontOffsetX();
                  int j = enumfacing.getFrontOffsetZ();
                  if (flagEntrance) {
                     tileentity.setEntrance(pos.down());
                     if (worldIn.isRemote) {
                        player.sendMessage(new TextComponentTranslation("message.saferoom.entranceset", new Object[]{player.getName()}));
                     }
                  } else if (flagExit) {
                     tileentity.setExit(pos.down());
                     if (worldIn.isRemote) {
                        player.sendMessage(new TextComponentTranslation("message.saferoom.exitset", new Object[]{player.getName()}));
                     }
                  }

                  boolean var10000;
                  if ((i >= 0 || hitZ >= 0.5F) && (i <= 0 || hitZ <= 0.5F) && (j >= 0 || hitX <= 0.5F) && (j <= 0 || hitX >= 0.5F)) {
                     var10000 = false;
                  } else {
                     var10000 = true;
                  }

                  worldIn.setBlockState(pos.down(), InitBlocks.saferoomdoornode.getDefaultState());
                  placeDoor(worldIn, pos, enumfacing, this.block, false);
                  SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, player);
                  worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                  itemstack.shrink(1);
                  return EnumActionResult.SUCCESS;
               } else {
                  return EnumActionResult.FAIL;
               }
            }
         }

         return EnumActionResult.FAIL;
      }
   }

   public static void placeDoor(World worldIn, BlockPos pos, EnumFacing facing, Block door, boolean isRightHinge) {
      BlockPos blockpos = pos.offset(facing.rotateY());
      BlockPos blockpos1 = pos.offset(facing.rotateYCCW());
      int i = (worldIn.getBlockState(blockpos1).isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos1.up()).isNormalCube() ? 1 : 0);
      int j = (worldIn.getBlockState(blockpos).isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos.up()).isNormalCube() ? 1 : 0);
      boolean flag = worldIn.getBlockState(blockpos1).getBlock() == door || worldIn.getBlockState(blockpos1.up()).getBlock() == door;
      boolean flag1 = worldIn.getBlockState(blockpos).getBlock() == door || worldIn.getBlockState(blockpos.up()).getBlock() == door;
      if ((!flag || flag1) && j <= i) {
         if (flag1 && !flag || j < i) {
            isRightHinge = false;
         }
      } else {
         isRightHinge = true;
      }

      BlockPos blockpos2 = pos.up();
      boolean flag2 = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockpos2);
      IBlockState iblockstate = door.getDefaultState().withProperty(BlockSaferoomDoor.FACING, facing).withProperty(BlockSaferoomDoor.HINGE, isRightHinge ? BlockSaferoomDoor.EnumHingePosition.RIGHT : BlockSaferoomDoor.EnumHingePosition.LEFT).withProperty(BlockSaferoomDoor.OPEN, flag2);
      worldIn.setBlockState(pos, iblockstate.withProperty(BlockSaferoomDoor.HALF, BlockSaferoomDoor.EnumDoorHalf.LOWER), 2);
      worldIn.setBlockState(blockpos2, iblockstate.withProperty(BlockSaferoomDoor.HALF, BlockSaferoomDoor.EnumDoorHalf.UPPER), 2);
      worldIn.notifyNeighborsOfStateChange(pos, door, false);
      worldIn.notifyNeighborsOfStateChange(blockpos2, door, false);
   }
}
