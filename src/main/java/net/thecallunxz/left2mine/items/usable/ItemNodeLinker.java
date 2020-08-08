package net.thecallunxz.left2mine.items.usable;

import java.awt.Color;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.blocks.nodes.BlockNodeChild;
import net.thecallunxz.left2mine.blocks.nodes.BlockNodeParent;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeChild;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeParent;
import net.thecallunxz.left2mine.items.ItemBase;

public class ItemNodeLinker extends ItemBase {
   public ItemNodeLinker(String name, ItemBase.EnumItemType type) {
      super(name, type);
      this.setMaxStackSize(1);
      this.setCreativeTab(Main.tabL2M);
   }

   public void onUpdate(ItemStack itemstack, World world, Entity entity, int metadata, boolean bool) {
      if (itemstack.getTagCompound() == null) {
         itemstack.setTagCompound(new NBTTagCompound());
      }

      NBTTagCompound nbt = itemstack.getTagCompound();
      BlockPos parentPos = NBTUtil.getPosFromTag(nbt.getCompoundTag("parentnode"));
      if (bool && parentPos != null && world.getTileEntity(parentPos) instanceof TileEntityNodeParent) {
         TileEntityNodeParent parentTile = (TileEntityNodeParent)world.getTileEntity(parentPos);
         int rgb = (new Color(parentTile.getRedColour(), parentTile.getGreenColour(), parentTile.getBlueColour())).getRGB();
         nbt.setInteger("rgbCustom", rgb);
      }

   }

   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      NBTTagCompound nbt = player.getHeldItem(hand).getTagCompound();
      IBlockState iblockstate = worldIn.getBlockState(pos);
      NBTTagCompound nbttag;
      if (iblockstate.getBlock() instanceof BlockNodeChild) {
         nbttag = NBTUtil.createPosTag(pos);
         nbt.setTag("childnode", nbttag);
         if (NBTUtil.getPosFromTag(nbt.getCompoundTag("parentnode")) != null) {
            this.connectNodes(player, worldIn, pos, nbt);
         }

         return EnumActionResult.SUCCESS;
      } else if (iblockstate.getBlock() instanceof BlockNodeParent) {
         nbttag = NBTUtil.createPosTag(pos);
         nbt.setTag("parentnode", nbttag);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }

   private void connectNodes(EntityPlayer player, World worldIn, BlockPos pos, NBTTagCompound tag) {
      if (worldIn.getTileEntity(NBTUtil.getPosFromTag(tag.getCompoundTag("parentnode"))) instanceof TileEntityNodeParent && worldIn.getTileEntity(NBTUtil.getPosFromTag(tag.getCompoundTag("childnode"))) instanceof TileEntityNodeChild) {
         TileEntityNodeChild TEChild = (TileEntityNodeChild)worldIn.getTileEntity(NBTUtil.getPosFromTag(tag.getCompoundTag("childnode")));
         TileEntityNodeParent TEParent = (TileEntityNodeParent)worldIn.getTileEntity(NBTUtil.getPosFromTag(tag.getCompoundTag("parentnode")));
         TEChild.setConnectedNode(NBTUtil.getPosFromTag(tag.getCompoundTag("parentnode")));
         TEParent.addChildNode(NBTUtil.getPosFromTag(tag.getCompoundTag("childnode")));
      }

   }
}
