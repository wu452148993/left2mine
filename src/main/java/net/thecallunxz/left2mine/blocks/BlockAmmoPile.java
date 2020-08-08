package net.thecallunxz.left2mine.blocks;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.items.weapons.properties.GunProperties;

public class BlockAmmoPile extends BlockBase {
   protected static final AxisAlignedBB BOUND_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0625D, 0.9375D);

   public BlockAmmoPile(String name) {
      super(Main.SURFACE, name);
      this.setHardness(-1.0F);
      this.setBlockUnbreakable();
      this.setResistance(6000001.0F);
      this.translucent = true;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack held = playerIn.getHeldItem(hand);

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = playerIn.inventory.getStackInSlot(i);
         if (stack.getItem() instanceof ItemGun) {
            GunProperties gun = ((ItemGun)stack.getItem()).getGun();
            if (gun.refillable) {
               NBTTagCompound nbt = stack.getTagCompound();
               nbt.setInteger("storage", gun.storageSize);
               worldIn.playSound(playerIn, pos, InitSounds.item_pickup, SoundCategory.PLAYERS, 0.25F, 1.0F);
               stack.setTagCompound(nbt);
            }
         }
      }

      if (held.getItem() instanceof ItemGun) {
         return false;
      } else {
         return true;
      }
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
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

   public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
