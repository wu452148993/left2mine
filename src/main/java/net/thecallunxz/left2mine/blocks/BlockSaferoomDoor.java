package net.thecallunxz.left2mine.blocks;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockBossSpawn;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockHordeSpawn;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockZombieSpawn;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeChild;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntitySaferoomDoor;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntitySaferoomNode;
import net.thecallunxz.left2mine.entities.mobs.IRagdollEntities;
import net.thecallunxz.left2mine.items.weapons.ItemGun;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.MusicMessage;
import net.thecallunxz.left2mine.networking.client.PlayerDeathMessage;

public class BlockSaferoomDoor extends BlockBase {
   public static final PropertyDirection FACING;
   public static final PropertyBool OPEN;
   public static final PropertyBool LOCKED;
   public static final PropertyEnum<BlockSaferoomDoor.EnumHingePosition> HINGE;
   public static final PropertyEnum<BlockSaferoomDoor.EnumDoorHalf> HALF;
   protected static final AxisAlignedBB SOUTH_AABB;
   protected static final AxisAlignedBB NORTH_AABB;
   protected static final AxisAlignedBB WEST_AABB;
   protected static final AxisAlignedBB EAST_AABB;

   public BlockSaferoomDoor(String name) {
      super(Material.IRON, name);
      this.setBlockUnbreakable();
      this.setResistance(6000001.0F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, false).withProperty(HINGE, BlockSaferoomDoor.EnumHingePosition.LEFT).withProperty(LOCKED, false).withProperty(HALF, BlockSaferoomDoor.EnumDoorHalf.LOWER));
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      state = state.getActualState(source, pos);
      EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
      boolean flag = !(Boolean)state.getValue(OPEN);
      boolean flag1 = state.getValue(HINGE) == BlockSaferoomDoor.EnumHingePosition.RIGHT;
      switch(enumfacing) {
      case EAST:
      default:
         return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
      case SOUTH:
         return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
      case WEST:
         return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
      case NORTH:
         return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
      }
   }

   public BlockBase setCreativeTab(CreativeTabs tab) {
      return this;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return isOpen(combineMetadata(worldIn, pos));
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   private int getCloseSound() {
      return this.blockMaterial == Material.IRON ? 1011 : 1012;
   }

   private int getOpenSound() {
      return this.blockMaterial == Material.IRON ? 1005 : 1006;
   }

   public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return MapColor.IRON;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      BlockPos blockpos = state.getValue(HALF) == BlockSaferoomDoor.EnumDoorHalf.LOWER ? pos : pos.down();
      IBlockState iblockstate = pos.equals(blockpos) ? state : worldIn.getBlockState(blockpos);
      if (iblockstate.getBlock() != this) {
         return false;
      } else if (worldIn.getTileEntity(blockpos.down()) instanceof TileEntitySaferoomDoor) {
         TileEntitySaferoomDoor tile = (TileEntitySaferoomDoor)worldIn.getTileEntity(blockpos.down());
         if (tile.getEntrance() && tile.getSwitched()) {
            return false;
         } else if (!tile.getEntrance() && !tile.getSwitched()) {
            return false;
         } else {
            state = iblockstate.cycleProperty(OPEN);
            worldIn.setBlockState(blockpos, state, 10);
            worldIn.markBlockRangeForRenderUpdate(blockpos, pos);
            worldIn.playEvent(playerIn, (Boolean)state.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), pos, 0);
            this.checkSaferoom((TileEntitySaferoomDoor)worldIn.getTileEntity(blockpos.down()), blockpos, worldIn, state);
            return !(playerIn.getHeldItem(hand).getItem() instanceof ItemGun);
         }
      } else {
         return false;
      }
   }

   private void checkSaferoom(TileEntitySaferoomDoor tileEntity, BlockPos blockpos, World worldIn, IBlockState state) {
      if (!(Boolean)state.getValue(OPEN)) {
         EnumFacing facing = (EnumFacing)state.getValue(FACING);
         Predicate<Entity> predicate = EntitySelectors.CAN_AI_TARGET;
         int outofroom = 0;
         int playerCount = 0;
         if (tileEntity.getEntrance()) {
            Iterator var9 = worldIn.playerEntities.iterator();

            while(var9.hasNext()) {
               EntityPlayer player = (EntityPlayer)var9.next();
               if (predicate.apply(player)) {
                  ++playerCount;
                  if (facing == EnumFacing.NORTH && player.posZ - 0.6D > (double)blockpos.getZ()) {
                     ++outofroom;
                     break;
                  }

                  if (facing == EnumFacing.SOUTH && player.posZ - 0.4D < (double)blockpos.getZ()) {
                     ++outofroom;
                     break;
                  }

                  if (facing == EnumFacing.EAST && player.posX - 0.4D < (double)blockpos.getX()) {
                     ++outofroom;
                     break;
                  }

                  if (facing == EnumFacing.WEST && player.posX - 0.6D > (double)blockpos.getX()) {
                     ++outofroom;
                     break;
                  }

                  if (tileEntity.getConnectedNode().distanceSq(player.getPosition()) > 25.0D) {
                     ++outofroom;
                     break;
                  }
               }
            }

            if (outofroom == 0 && playerCount > 0) {
               this.allInSaferoom((TileEntitySaferoomNode)worldIn.getTileEntity(tileEntity.getConnectedNode()), worldIn);
            }
         }
      }

   }

   private void allInSaferoom(TileEntitySaferoomNode tile, World world) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      if (data.isInGame()) {
         ArrayList<BlockPos> poslist = tile.getChildNodes();
         Iterator var5;
         if (!world.isRemote) {
            Left2MinePacket.INSTANCE.sendToAll(new MusicMessage(1));

            EntityPlayer player;
            for(var5 = world.playerEntities.iterator(); var5.hasNext(); player.sendMessage(new TextComponentTranslation("message.player.safefornow", new Object[0]))) {
               player = (EntityPlayer)var5.next();
               if (player.isSpectator()) {
                  player.setGameType(GameType.ADVENTURE);
                  player.setPositionAndUpdate((double)tile.getPos().getX(), (double)tile.getPos().getY(), (double)tile.getPos().getZ());
                  player.inventory.clear();
               }
            }

            Left2MinePacket.INSTANCE.sendToAll(new PlayerDeathMessage("", 1));
         }

         var5 = world.loadedEntityList.iterator();

         while(var5.hasNext()) {
            Entity entity = (Entity)var5.next();
            if (entity instanceof IRagdollEntities) {
               entity.setDead();
            }
         }

         TileEntity TEChild;
         int i;
         BlockPos childnode;
         for(i = 0; i < data.getActiveNodes().size(); ++i) {
            childnode = (BlockPos)data.getActiveNodes().get(i);
            TEChild = world.getTileEntity(childnode);
            ((TileEntityNodeChild)TEChild).setActive(false);
         }

         for(i = 0; i < data.getBossNodes().size(); ++i) {
            childnode = (BlockPos)data.getBossNodes().get(i);
            TEChild = world.getTileEntity(childnode);
            ((TileEntityNodeChild)TEChild).setActive(false);
         }

         data.clearActiveNodes();
         data.clearHordeNodes();
         data.clearBossNodes();

         for(i = 0; i < poslist.size(); ++i) {
            childnode = (BlockPos)poslist.get(i);
            if (world.getBlockState(childnode).getBlock() instanceof BlockZombieSpawn) {
               data.addActiveNode(childnode);
            }

            if (world.getBlockState(childnode).getBlock() instanceof BlockHordeSpawn) {
               data.addHordeNode(childnode);
            }

            if (world.getBlockState(childnode).getBlock() instanceof BlockBossSpawn) {
               data.addBossNode(childnode);
            }

            TEChild = world.getTileEntity(childnode);
            ((TileEntityNodeChild)TEChild).setActive(true);
         }

         if (data.getActiveNodes().size() > 1) {
            data.sortActiveNodes(tile.getPos());
         }

         data.setLastSpawnPos(data.getRespawnPos());
         data.setRespawnPos(new BlockPos(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ()));
         data.resetInfectedCount();
         data.setReadyToPanic(false);
         data.setReadyToSpecial(false);
         data.setInPanicEvent(false);
         world.setBlockState(tile.getEntrance().up(), world.getBlockState(tile.getEntrance().up()).cycleProperty(LOCKED));
         world.setBlockState(tile.getEntrance().up().up(), world.getBlockState(tile.getEntrance().up().up()).cycleProperty(LOCKED));
         world.setBlockState(tile.getExit().up(), world.getBlockState(tile.getExit().up()).cycleProperty(LOCKED));
         world.setBlockState(tile.getExit().up().up(), world.getBlockState(tile.getExit().up().up()).cycleProperty(LOCKED));
         TileEntitySaferoomDoor tile1 = (TileEntitySaferoomDoor)world.getTileEntity(tile.getEntrance());
         TileEntitySaferoomDoor tile2 = (TileEntitySaferoomDoor)world.getTileEntity(tile.getExit());
         tile1.setSwitched(true);
         tile2.setSwitched(true);
      }

   }

   public void toggleDoor(World worldIn, BlockPos pos, boolean open) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      if (iblockstate.getBlock() == this) {
         BlockPos blockpos = iblockstate.getValue(HALF) == BlockSaferoomDoor.EnumDoorHalf.LOWER ? pos : pos.down();
         IBlockState iblockstate1 = pos == blockpos ? iblockstate : worldIn.getBlockState(blockpos);
         if (iblockstate1.getBlock() == this && (Boolean)iblockstate1.getValue(OPEN) != open) {
            worldIn.setBlockState(blockpos, iblockstate1.withProperty(OPEN, open), 10);
            worldIn.markBlockRangeForRenderUpdate(blockpos, pos);
            worldIn.playEvent((EntityPlayer)null, open ? this.getOpenSound() : this.getCloseSound(), pos, 0);
         }
      }

   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (state.getValue(HALF) == BlockSaferoomDoor.EnumDoorHalf.UPPER) {
         BlockPos blockpos = pos.down();
         IBlockState iblockstate = worldIn.getBlockState(blockpos);
         if (iblockstate.getBlock() != this) {
            worldIn.setBlockToAir(pos);
         } else if (blockIn != this) {
            iblockstate.neighborChanged(worldIn, blockpos, blockIn, fromPos);
         }
      } else {
         boolean flag1 = false;
         BlockPos blockpos1 = pos.up();
         IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);
         if (iblockstate1.getBlock() != this) {
            worldIn.setBlockToAir(pos);
            flag1 = true;
         }

         if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP)) {
            worldIn.setBlockToAir(pos);
            flag1 = true;
            if (iblockstate1.getBlock() == this) {
               worldIn.setBlockToAir(blockpos1);
            }
         }

         if (flag1 && !worldIn.isRemote) {
         }
      }

   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      if (pos.getY() >= worldIn.getHeight() - 1) {
         return false;
      } else {
         IBlockState state = worldIn.getBlockState(pos.down());
         return (state.isTopSolid() || state.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID) && super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up());
      }
   }

   public EnumPushReaction getMobilityFlag(IBlockState state) {
      return EnumPushReaction.DESTROY;
   }

   public static int combineMetadata(IBlockAccess worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      int i = iblockstate.getBlock().getMetaFromState(iblockstate);
      boolean flag = isTop(i);
      IBlockState iblockstate1 = worldIn.getBlockState(pos.down());
      int j = iblockstate1.getBlock().getMetaFromState(iblockstate1);
      int k = flag ? j : i;
      IBlockState iblockstate2 = worldIn.getBlockState(pos.up());
      int l = iblockstate2.getBlock().getMetaFromState(iblockstate2);
      int i1 = flag ? i : l;
      boolean flag1 = (i1 & 1) != 0;
      boolean flag2 = (i1 & 2) != 0;
      return removeHalfBit(k) | (flag ? 8 : 0) | (flag1 ? 16 : 0) | (flag2 ? 32 : 0);
   }

   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      BlockPos blockpos = pos.down();
      BlockPos blockpos1 = pos.up();
      if (player.capabilities.isCreativeMode && state.getValue(HALF) == BlockSaferoomDoor.EnumDoorHalf.UPPER && worldIn.getBlockState(blockpos).getBlock() == this) {
         worldIn.setBlockToAir(blockpos);
         worldIn.setBlockState(blockpos.down(), Blocks.IRON_BLOCK.getDefaultState());
      }

      if (state.getValue(HALF) == BlockSaferoomDoor.EnumDoorHalf.LOWER && worldIn.getBlockState(blockpos1).getBlock() == this) {
         if (player.capabilities.isCreativeMode) {
            worldIn.setBlockToAir(pos);
         }

         worldIn.setBlockToAir(blockpos1);
         worldIn.setBlockState(blockpos, Blocks.IRON_BLOCK.getDefaultState());
      }

   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      IBlockState iblockstate;
      if (state.getValue(HALF) == BlockSaferoomDoor.EnumDoorHalf.LOWER) {
         iblockstate = worldIn.getBlockState(pos.up());
         if (iblockstate.getBlock() == this) {
            state = state.withProperty(HINGE, iblockstate.getValue(HINGE)).withProperty(LOCKED, iblockstate.getValue(LOCKED));
         }
      } else {
         iblockstate = worldIn.getBlockState(pos.down());
         if (iblockstate.getBlock() == this) {
            state = state.withProperty(FACING, iblockstate.getValue(FACING)).withProperty(OPEN, iblockstate.getValue(OPEN));
         }
      }

      return state;
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.getValue(HALF) != BlockSaferoomDoor.EnumDoorHalf.LOWER ? state : state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return mirrorIn == Mirror.NONE ? state : state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING))).cycleProperty(HINGE);
   }

   public IBlockState getStateFromMeta(int meta) {
      return (meta & 8) > 0 ? this.getDefaultState().withProperty(HALF, BlockSaferoomDoor.EnumDoorHalf.UPPER).withProperty(HINGE, (meta & 1) > 0 ? BlockSaferoomDoor.EnumHingePosition.RIGHT : BlockSaferoomDoor.EnumHingePosition.LEFT).withProperty(LOCKED, (meta & 2) > 0) : this.getDefaultState().withProperty(HALF, BlockSaferoomDoor.EnumDoorHalf.LOWER).withProperty(FACING, EnumFacing.getHorizontal(meta & 3).rotateYCCW()).withProperty(OPEN, (meta & 4) > 0);
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      int i;
      if (state.getValue(HALF) == BlockSaferoomDoor.EnumDoorHalf.UPPER) {
         i = i | 8;
         if (state.getValue(HINGE) == BlockSaferoomDoor.EnumHingePosition.RIGHT) {
            i |= 1;
         }

         if ((Boolean)state.getValue(LOCKED)) {
            i |= 2;
         }
      } else {
         i = i | ((EnumFacing)state.getValue(FACING)).rotateY().getHorizontalIndex();
         if ((Boolean)state.getValue(OPEN)) {
            i |= 4;
         }
      }

      return i;
   }

   protected static int removeHalfBit(int meta) {
      return meta & 7;
   }

   public static boolean isOpen(IBlockAccess worldIn, BlockPos pos) {
      return isOpen(combineMetadata(worldIn, pos));
   }

   public static EnumFacing getFacing(IBlockAccess worldIn, BlockPos pos) {
      return getFacing(combineMetadata(worldIn, pos));
   }

   public static EnumFacing getFacing(int combinedMeta) {
      return EnumFacing.getHorizontal(combinedMeta & 3).rotateYCCW();
   }

   protected static boolean isOpen(int combinedMeta) {
      return (combinedMeta & 4) != 0;
   }

   protected static boolean isTop(int meta) {
      return (meta & 8) != 0;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{HALF, FACING, OPEN, HINGE, LOCKED});
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   static {
      FACING = BlockHorizontal.FACING;
      OPEN = PropertyBool.create("open");
      LOCKED = PropertyBool.create("locked");
      HINGE = PropertyEnum.create("hinge", BlockSaferoomDoor.EnumHingePosition.class);
      HALF = PropertyEnum.create("half", BlockSaferoomDoor.EnumDoorHalf.class);
      SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.1875D);
      NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.8125D, 1.0D, 1.0D, 1.0D);
      WEST_AABB = new AxisAlignedBB(0.8125D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
      EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.1875D, 1.0D, 1.0D);
   }

   public static enum EnumHingePosition implements IStringSerializable {
      LEFT,
      RIGHT;

      public String toString() {
         return this.getName();
      }

      public String getName() {
         return this == LEFT ? "left" : "right";
      }
   }

   public static enum EnumDoorHalf implements IStringSerializable {
      UPPER,
      LOWER;

      public String toString() {
         return this.getName();
      }

      public String getName() {
         return this == UPPER ? "upper" : "lower";
      }
   }
}
