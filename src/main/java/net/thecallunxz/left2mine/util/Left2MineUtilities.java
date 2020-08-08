package net.thecallunxz.left2mine.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.thecallunxz.left2mine.WorldDataLeft2Mine;
import net.thecallunxz.left2mine.blocks.BlockFakeWall;
import net.thecallunxz.left2mine.blocks.BlockSaferoomDoor;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockBossSpawn;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockHordeSpawn;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockItemSpawn;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockZombieSpawn;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeChild;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeParent;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntitySaferoomDoor;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntitySaferoomNode;
import net.thecallunxz.left2mine.capabilities.CapabilityStats;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.capabilities.IStats;
import net.thecallunxz.left2mine.capabilities.StatsEnum;
import net.thecallunxz.left2mine.capabilities.StatsProvider;
import net.thecallunxz.left2mine.entities.ICustomProjectileBox;
import net.thecallunxz.left2mine.entities.mobs.EntityCommonInfected;
import net.thecallunxz.left2mine.entities.mobs.IRagdollEntities;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityBoomer;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.EquippedMessage;
import net.thecallunxz.left2mine.networking.client.GameStatsMessage;
import net.thecallunxz.left2mine.networking.client.MusicMessage;
import net.thecallunxz.left2mine.networking.client.PlayerDeathMessage;
import net.thecallunxz.left2mine.networking.client.ShaderMessage;
import net.thecallunxz.left2mine.networking.client.StatsMessage;
import net.thecallunxz.left2mine.networking.client.SurvivalUpdateMessage;
import net.thecallunxz.left2mine.networking.client.VoteClientMessage;
import net.thecallunxz.left2mine.pathfinding.WalkCustomDoorProcessor;
import org.lwjgl.util.vector.Vector3f;

public class Left2MineUtilities {
   public static long pistolTime = 0L;
   public static long gameOverTime = 0L;

   public static void knockBack(Entity pushed, Entity origin, float strength) {
      double xRatio = origin.posX - pushed.posX;
      double zRatio = origin.posZ - pushed.posZ;
      pushed.isAirBorne = true;
      float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
      pushed.motionX /= 2.0D;
      pushed.motionZ /= 2.0D;
      pushed.motionX -= xRatio / (double)f * (double)strength;
      pushed.motionZ -= zRatio / (double)f * (double)strength;
   }

   public static List<Entity> getEntitiesInAABBexcluding(World world, @Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate) {
      List<Entity> list = Lists.newArrayList();
      int j2 = MathHelper.floor((boundingBox.minX - World.MAX_ENTITY_RADIUS) / 16.0D);
      int k2 = MathHelper.floor((boundingBox.maxX + World.MAX_ENTITY_RADIUS) / 16.0D);
      int l2 = MathHelper.floor((boundingBox.minZ - World.MAX_ENTITY_RADIUS) / 16.0D);
      int i3 = MathHelper.floor((boundingBox.maxZ + World.MAX_ENTITY_RADIUS) / 16.0D);

      for(int j3 = j2; j3 <= k2; ++j3) {
         for(int k3 = l2; k3 <= i3; ++k3) {
            if (world.isBlockLoaded(new BlockPos((double)j3 * 16.0D, 0.0D, (double)k3 * 16.0D), true)) {
               getEntitiesWithinAABBForEntity(world.getChunkFromChunkCoords(j3, k3), entityIn, boundingBox, list, predicate);
            }
         }
      }

      return list;
   }

   public static AxisAlignedBB getEntityCustomBox(Entity entity) {
      return entity instanceof ICustomProjectileBox ? ((ICustomProjectileBox)entity).getCustomProjectileBox() : entity.getEntityBoundingBox();
   }

   public static void getEntitiesWithinAABBForEntity(Chunk chunk, @Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> filter) {
      int i = MathHelper.floor((aabb.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
      int j = MathHelper.floor((aabb.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);
      i = MathHelper.clamp(i, 0, chunk.getEntityLists().length - 1);
      j = MathHelper.clamp(j, 0, chunk.getEntityLists().length - 1);

      label67:
      for(int k = i; k <= j; ++k) {
         if (!chunk.getEntityLists()[k].isEmpty()) {
            Iterator var8 = chunk.getEntityLists()[k].iterator();

            while(true) {
               Entity[] aentity;
               do {
                  Entity entity;
                  do {
                     do {
                        if (!var8.hasNext()) {
                           continue label67;
                        }

                        entity = (Entity)var8.next();
                     } while(!getEntityCustomBox(entity).intersects(aabb));
                  } while(entity == entityIn);

                  if (filter == null || filter.apply(entity)) {
                     listToFill.add(entity);
                  }

                  aentity = entity.getParts();
               } while(aentity == null);

               Entity[] var11 = aentity;
               int var12 = aentity.length;

               for(int var13 = 0; var13 < var12; ++var13) {
                  Entity entity1 = var11[var13];
                  if (entity1 != entityIn && getEntityCustomBox(entity1).intersects(aabb) && (filter == null || filter.apply(entity1))) {
                     listToFill.add(entity1);
                  }
               }
            }
         }
      }

   }

   public static boolean isIntBetween(int intMin, int intMax, int intValue) {
      return intValue <= intMax && intValue >= intMin || intValue >= intMax && intValue <= intMin;
   }

   public static ArrayList<BlockPos> getTouchingBlocks(BlockPos pos) {
      ArrayList<BlockPos> posList = new ArrayList();
      posList.add(pos.north());
      posList.add(pos.east());
      posList.add(pos.south());
      posList.add(pos.west());
      posList.add(pos.up());
      posList.add(pos.down());
      return posList;
   }

   public static Vec3i scaleVec3i(Vec3i vec, double scale) {
      return new Vec3i((double)vec.getX() * scale, (double)vec.getY() * scale, (double)vec.getZ() * scale);
   }

   public static boolean canPlayersSeeEntity(World world, Entity ent) {
      boolean canSee = false;
      Predicate<Entity> predicate = EntitySelectors.CAN_AI_TARGET;
      Iterator var4 = world.playerEntities.iterator();

      while(var4.hasNext()) {
         EntityPlayer player = (EntityPlayer)var4.next();
         if (predicate.apply(player) && Math.sqrt(player.getDistanceSq(ent.getPosition())) < 70.0D && (RayCastCheckOpaque(new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ), new Vec3d(ent.posX, ent.posY, ent.posZ), world, 0) == null || RayCastCheckOpaque(new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ), new Vec3d(ent.posX, ent.posY + (double)ent.height, ent.posZ), world, 0) == null)) {
            canSee = true;
            break;
         }
      }

      return canSee;
   }

   public static void setPanicSlow(EntityCommonInfected infected) {
      int randNumb = (new Random()).nextInt(9);
      switch(randNumb) {
      case 0:
      default:
         break;
      case 1:
         infected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 5, 1, true, false));
         break;
      case 2:
         infected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 5, 2, true, false));
         break;
      case 3:
         infected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10, 1, true, false));
         break;
      case 4:
         infected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 10, 2, true, false));
         break;
      case 5:
         infected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 15, 1, true, false));
         break;
      case 6:
         infected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 15, 2, true, false));
         break;
      case 7:
         infected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 1, true, false));
         break;
      case 8:
         infected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 2, true, false));
      }

   }

   public static boolean canSeeHigh(EntityPlayer player, Entity ent) {
      Vec3d vec3d = player.getLook(1.0F).normalize();
      Vec3d vec3d1 = new Vec3d(ent.posX - player.posX, ent.getEntityBoundingBox().minY + (double)ent.getEyeHeight() - (player.posY + (double)player.getEyeHeight()), ent.posZ - player.posZ);
      double d0 = vec3d1.lengthVector();
      vec3d1 = vec3d1.normalize();
      double d1 = vec3d.dotProduct(vec3d1);
      return d1 > 0.9D - 0.025D / d0;
   }

   public static boolean canSeeLow(EntityPlayer player, Entity ent) {
      Vec3d vec3d = player.getLook(1.0F).normalize();
      Vec3d vec3d1 = new Vec3d(ent.posX - player.posX, ent.getEntityBoundingBox().minY + (double)ent.getEyeHeight() / 3.0D - (player.posY + (double)player.getEyeHeight()), ent.posZ - player.posZ);
      double d0 = vec3d1.lengthVector();
      vec3d1 = vec3d1.normalize();
      double d1 = vec3d.dotProduct(vec3d1);
      return d1 > 0.9D - 0.025D / d0;
   }

   public static boolean canBoomerHitHigh(EntityBoomer boomer, Entity ent) {
      Vec3d vec3d = boomer.getLook(1.0F).normalize();
      Vec3d vec3d1 = new Vec3d(ent.posX - boomer.posX, ent.getEntityBoundingBox().minY + (double)ent.getEyeHeight() - (boomer.posY + (double)boomer.getEyeHeight()), ent.posZ - boomer.posZ);
      double d0 = vec3d1.lengthVector();
      vec3d1 = vec3d1.normalize();
      double d1 = vec3d.dotProduct(vec3d1);
      return d1 > 0.9D - 0.025D / d0;
   }

   public static boolean canBoomerHitLow(EntityBoomer boomer, Entity ent) {
      Vec3d vec3d = boomer.getLook(1.0F).normalize();
      Vec3d vec3d1 = new Vec3d(ent.posX - boomer.posX, ent.getEntityBoundingBox().minY + (double)ent.getEyeHeight() / 3.0D - (boomer.posY + (double)boomer.getEyeHeight()), ent.posZ - boomer.posZ);
      double d0 = vec3d1.lengthVector();
      vec3d1 = vec3d1.normalize();
      double d1 = vec3d.dotProduct(vec3d1);
      return d1 > 0.9D - 0.025D / d0;
   }

   public static Vec3d calculateVelocity(Vec3d from, Vec3d to, int heightGain) {
      double gravity = 0.115D;
      double endGain = to.y - from.y;
      double horizDist = from.distanceTo(to);
      double maxGain = (double)heightGain > endGain + (double)heightGain ? (double)heightGain : endGain + (double)heightGain;
      double a = -horizDist * horizDist / (4.0D * maxGain);
      double c = -endGain;
      double slope = -horizDist / (2.0D * a) - Math.sqrt(horizDist * horizDist - 4.0D * a * c) / (2.0D * a);
      double vy = Math.sqrt(maxGain * gravity);
      double vh = vy / slope;
      double dx = to.x - from.x;
      double dz = to.z - from.z;
      double mag = Math.sqrt(dx * dx + dz * dz);
      double dirx = dx / mag;
      double dirz = dz / mag;
      double vx = vh * dirx;
      double vz = vh * dirz;
      return new Vec3d(vx, vy, vz);
   }

   public static RayTraceResult RayCastCheckOpaque(Vec3d from, Vec3d to, World world, int timesLooped) {
      double zplus = 0.0D;
      double yplus = 0.1D;
      double xplus = 0.0D;
      int loopCount = timesLooped + 1;
      IBlockState state;
      if (from.z > to.z) {
         state = world.getBlockState(new BlockPos(to.x, to.y, to.z + 1.0D));
         if ((!state.getMaterial().isOpaque() || !state.isOpaqueCube() || !state.isFullCube()) && !(state.getBlock() instanceof BlockFakeWall) && !(state.getBlock() instanceof BlockStairs) && !(state.getBlock() instanceof BlockSlab)) {
            zplus = 0.45D;
         }
      } else {
         state = world.getBlockState(new BlockPos(to.x, to.y, to.z - 1.0D));
         if ((!state.getMaterial().isOpaque() || !state.isOpaqueCube() || !state.isFullCube()) && !(state.getBlock() instanceof BlockFakeWall) && !(state.getBlock() instanceof BlockStairs) && !(state.getBlock() instanceof BlockSlab)) {
            zplus = -0.45D;
         }
      }

      if (from.x > to.x) {
         state = world.getBlockState(new BlockPos(to.x + 1.0D, to.y, to.z));
         if ((!state.getMaterial().isOpaque() || !state.isOpaqueCube() || !state.isFullCube()) && !(state.getBlock() instanceof BlockFakeWall) && !(state.getBlock() instanceof BlockStairs) && !(state.getBlock() instanceof BlockSlab)) {
            xplus = 0.45D;
         }
      } else {
         state = world.getBlockState(new BlockPos(to.x - 1.0D, to.y, to.z));
         if ((!state.getMaterial().isOpaque() || !state.isOpaqueCube() || !state.isFullCube()) && !(state.getBlock() instanceof BlockFakeWall) && !(state.getBlock() instanceof BlockStairs) && !(state.getBlock() instanceof BlockSlab)) {
            xplus = -0.45D;
         }
      }

      RayTraceResult mop = world.rayTraceBlocks(from, to.addVector(xplus, yplus, zplus), false, true, false);
      if (mop == null) {
         return null;
      } else if (world.rayTraceBlocks(from, to.addVector(-xplus, yplus, zplus), false, true, false) == null) {
         return null;
      } else if (world.rayTraceBlocks(from, to.addVector(xplus, yplus, -zplus), false, true, false) == null) {
         return null;
      } else if (loopCount > 100) {
         return mop;
      } else {
         if (mop != null) {
            Material mat = world.getBlockState(mop.getBlockPos()).getMaterial();
            IBlockState state = world.getBlockState(mop.getBlockPos());
            if ((!mat.isOpaque() || !state.isOpaqueCube() || !state.isFullCube()) && !(state.getBlock() instanceof BlockFakeWall) && !(state.getBlock() instanceof BlockStairs) && !(state.getBlock() instanceof BlockSlab)) {
               Vec3d vecnew = to.subtract(mop.hitVec).normalize();
               return RayCastCheckOpaque(mop.hitVec.add(vecnew), to, world, loopCount);
            }
         }

         return mop;
      }
   }

   public static RayTraceResult RayCastCheckWalkThrough(Vec3d from, Vec3d to, World world) {
      RayTraceResult mop = world.rayTraceBlocks(from, to, false, true, false);
      return mop;
   }

   public static RayTraceResult RayCastCheckOpaqueDirect(Vec3d from, Vec3d to, World world, int timesLooped) {
      int loopCount = timesLooped + 1;
      RayTraceResult mop = world.rayTraceBlocks(from, to, false, true, false);
      if (mop == null) {
         return null;
      } else if (loopCount > 100) {
         return mop;
      } else {
         if (mop != null) {
            Material mat = world.getBlockState(mop.getBlockPos()).getMaterial();
            IBlockState state = world.getBlockState(mop.getBlockPos());
            if ((!mat.isOpaque() || !state.isOpaqueCube() || !state.isFullCube()) && !(state.getBlock() instanceof BlockFakeWall) && !(state.getBlock() instanceof BlockStairs) && !(state.getBlock() instanceof BlockSlab) && !(state.getBlock() instanceof BlockGlass)) {
               Vec3d vecnew = to.subtract(mop.hitVec).normalize();
               return RayCastCheckOpaqueDirect(mop.hitVec.add(vecnew), to, world, loopCount);
            }
         }

         return mop;
      }
   }

   public static boolean canEntitySeeEntity(Entity ent1, Entity ent2, World world) {
      if (ent1 != null && ent2 != null) {
         RayTraceResult raycast = RayCastCheckOpaque(new Vec3d(ent1.posX, ent1.posY + (double)ent1.getEyeHeight() - 0.25D, ent1.posZ), new Vec3d(ent2.posX, ent2.posY + (double)ent2.getEyeHeight() - 0.25D, ent2.posZ), world, 0);
         return raycast == null;
      } else {
         return false;
      }
   }

   public static boolean canEntityGetEntity(Entity ent1, Entity ent2, World world) {
      if (ent1 != null && ent2 != null) {
         RayTraceResult raycast = RayCastCheckWalkThrough(new Vec3d(ent1.posX, ent1.posY + (double)ent1.getEyeHeight(), ent1.posZ), new Vec3d(ent2.posX, ent2.posY + (double)ent2.getEyeHeight(), ent2.posZ), world);
         return raycast == null;
      } else {
         return false;
      }
   }

   public static List getCollidingBoundingBoxes(World world, AxisAlignedBB p_72945_2_) {
      ArrayList arraylist = Lists.newArrayList();
      int i = MathHelper.floor(p_72945_2_.minX);
      int j = MathHelper.floor(p_72945_2_.maxX + 1.0D);
      int k = MathHelper.floor(p_72945_2_.minY);
      int l = MathHelper.floor(p_72945_2_.maxY + 1.0D);
      int i1 = MathHelper.floor(p_72945_2_.minZ);
      int j1 = MathHelper.floor(p_72945_2_.maxZ + 1.0D);

      for(int k1 = i; k1 < j; ++k1) {
         for(int l1 = i1; l1 < j1; ++l1) {
            if (world.isBlockLoaded(new BlockPos(k1, 64, l1))) {
               for(int i2 = k - 1; i2 < l; ++i2) {
                  BlockPos blockpos = new BlockPos(k1, i2, l1);
                  IBlockState iblockstate = world.getBlockState(blockpos);
                  iblockstate.getBlock().addCollisionBoxToList(iblockstate, world, blockpos, p_72945_2_, arraylist, (Entity)null, true);
               }
            }
         }
      }

      double d0 = 2.0D;
      List list = world.getEntitiesWithinAABB(Entity.class, p_72945_2_.expand(d0, d0, d0));

      for(int j2 = 0; j2 < list.size(); ++j2) {
         if (((Entity)list.get(j2)).canBeCollidedWith()) {
            AxisAlignedBB axisalignedbb1 = ((Entity)list.get(j2)).getEntityBoundingBox();
            if (axisalignedbb1 == null && (Entity)list.get(j2) instanceof EntityPlayer) {
               axisalignedbb1 = new AxisAlignedBB(((Entity)list.get(j2)).posX - 0.30000001192092896D, ((Entity)list.get(j2)).posY + 1.7999999523162842D, ((Entity)list.get(j2)).posZ - 0.30000001192092896D, ((Entity)list.get(j2)).posX + 0.30000001192092896D, ((Entity)list.get(j2)).posY, ((Entity)list.get(j2)).posZ + 0.30000001192092896D);
            }

            if (axisalignedbb1 != null && axisalignedbb1.intersects(p_72945_2_)) {
               arraylist.add(axisalignedbb1);
            }
         }
      }

      return arraylist;
   }

   public static List getCollidingEntities(World world, AxisAlignedBB p_72945_2_) {
      ArrayList arraylist = Lists.newArrayList();
      double d0 = 2.0D;
      List list = world.getEntitiesWithinAABB(Entity.class, p_72945_2_.expand(d0, d0, d0));

      for(int j2 = 0; j2 < list.size(); ++j2) {
         if (((Entity)list.get(j2)).canBeCollidedWith()) {
            AxisAlignedBB axisalignedbb1 = ((Entity)list.get(j2)).getEntityBoundingBox();
            if (axisalignedbb1 == null && (Entity)list.get(j2) instanceof EntityPlayer) {
               axisalignedbb1 = new AxisAlignedBB(((Entity)list.get(j2)).posX - 0.30000001192092896D, ((Entity)list.get(j2)).posY + 1.7999999523162842D, ((Entity)list.get(j2)).posZ - 0.30000001192092896D, ((Entity)list.get(j2)).posX + 0.30000001192092896D, ((Entity)list.get(j2)).posY, ((Entity)list.get(j2)).posZ + 0.30000001192092896D);
            }

            if (axisalignedbb1 != null && axisalignedbb1.intersects(p_72945_2_)) {
               arraylist.add(axisalignedbb1);
            }
         }
      }

      return arraylist;
   }

   public static Vector3f rotateY(Vector3f num, float rotation) {
      Vector3f x = new Vector3f();
      Vector3f z = new Vector3f();
      x.x = (float)Math.cos((double)(rotation / 180.0F) * 3.141592653589793D);
      x.z = (float)Math.sin((double)(rotation / 180.0F) * 3.141592653589793D);
      x.normalise();
      x.x *= num.x;
      x.z *= num.x;
      z.x = (float)Math.sin((double)(-rotation / 180.0F) * 3.141592653589793D);
      z.z = (float)Math.cos((double)(-rotation / 180.0F) * 3.141592653589793D);
      z.normalise();
      z.x *= num.z;
      z.z *= num.z;
      num = new Vector3f(x.x + z.x, num.y, x.z + z.z);
      return num;
   }

   public static BlockPos getHighestBelow(int posX, int posY, int posZ, World world) {
      BlockPos pos = new BlockPos(posX, posY, posZ);

      for(int i = 0; i < 12; ++i) {
         if (world.getBlockState(pos.down(i)).getMaterial().blocksMovement()) {
            return pos.down(i);
         }
      }

      return null;
   }

   @Nullable
   public static Entity getEntityByUUID(World world, UUID uuid) {
      Iterator var2 = world.loadedEntityList.iterator();

      Entity ent;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         ent = (Entity)var2.next();
      } while(!uuid.equals(ent.getUniqueID()));

      return ent;
   }

   public static boolean isPinnedPotionActive(EntityPlayer playerIn) {
      return playerIn.isPotionActive(InitPotions.hunter_pinned) || playerIn.isPotionActive(InitPotions.smoker_tongued);
   }

   public static boolean checkGameOver(World world, EntityPlayer origPlayer) {
      Iterator var2 = world.playerEntities.iterator();

      EntityPlayer player;
      IEquip equip;
      while(var2.hasNext()) {
         player = (EntityPlayer)var2.next();
         equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         if (!player.isSpectator() && origPlayer.getDisplayNameString() != player.getDisplayNameString() && !equip.getLying()) {
            return false;
         }
      }

      var2 = world.playerEntities.iterator();

      while(true) {
         IStats stats;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            player = (EntityPlayer)var2.next();
            equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
         } while(!equip.getLying() && !player.isPotionActive(InitPotions.incapacitated));

         if (!player.isSpectator()) {
            stats.setDeaths(stats.getDeaths() + 1);
            Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.DEATHS, stats.getDeaths(), player));
         }
      }
   }

   public static void startGameOver(World world) {
      gameOverTime = world.getTotalWorldTime() + 40L;
      Left2MinePacket.INSTANCE.sendToAll(new MusicMessage(4));
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      if (data.isSurvivalInGame() && data.hasSurvivalStarted()) {
         long currentTime = world.getTotalWorldTime() - data.getGameStartTime();
         long bestTime = data.getBestSurvivalTime();
         data.setSurvivalStarted(false);
         if (currentTime > bestTime) {
            data.setBestSurvivalTime(currentTime);
         }

         Left2MinePacket.INSTANCE.sendToAll(new SurvivalUpdateMessage(3, data.getGameStartTime(), data.getBestSurvivalTime()));
         Left2MinePacket.INSTANCE.sendToAll(new SurvivalUpdateMessage(4, data.getGameStartTime(), currentTime));
      }

   }

   public static void gameOver(World world, boolean splash) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      Iterator var3 = world.loadedEntityList.iterator();

      while(var3.hasNext()) {
         Entity entity = (Entity)var3.next();
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

      data.randomizeGameSeed();
      data.resetInfectedCount();
      data.clearActiveNodes();
      data.clearBossNodes();
      data.clearHordeNodes();
      data.setReadyToPanic(false);
      data.setInPanicEvent(false);
      if (splash) {
         data.setTimesRestarted(data.getTimesRestarted() + 1);
         if (!world.isRemote) {
            Left2MinePacket.INSTANCE.sendToAll(new MusicMessage(5));
         }
      }

      double xCoord = (double)data.getRespawnPos().getX() + 0.5D;
      double yCoord = (double)data.getRespawnPos().getY();
      double zCoord = (double)data.getRespawnPos().getZ() + 0.5D;
      pistolTime = world.getTotalWorldTime() + 1L;
      Iterator var9 = world.playerEntities.iterator();

      while(var9.hasNext()) {
         EntityPlayer player = (EntityPlayer)var9.next();
         if (!player.isCreative()) {
            player.clearActivePotions();
            player.setGameType(GameType.ADVENTURE);
            player.inventory.clear();
            player.setHealth(player.getMaxHealth());
            player.setAbsorptionAmount(0.0F);
            player.getFoodStats().setFoodLevel(20);
            player.setPositionAndUpdate(xCoord, yCoord, zCoord);
            player.motionX = 0.0D;
            player.motionY = 0.0D;
            player.motionZ = 0.0D;
            if (!world.isRemote) {
               IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
               equip.setLives(3);
               Left2MinePacket.INSTANCE.sendTo(new ShaderMessage(0), (EntityPlayerMP)player);
            }
         }
      }

      Left2MinePacket.INSTANCE.sendToAll(new PlayerDeathMessage("", 1));
      TileEntity tileentity = world.getTileEntity(data.getRespawnPos());
      ArrayList<BlockPos> poslist = ((TileEntityNodeParent)tileentity).getChildNodes();

      for(int i = 0; i < poslist.size(); ++i) {
         BlockPos childnode = (BlockPos)poslist.get(i);
         if (world.getBlockState(childnode).getBlock() instanceof BlockZombieSpawn) {
            data.addActiveNode(childnode);
         }

         if (world.getBlockState(childnode).getBlock() instanceof BlockBossSpawn) {
            data.addBossNode(childnode);
         }

         if (world.getBlockState(childnode).getBlock() instanceof BlockHordeSpawn) {
            data.addHordeNode(childnode);
         }

         TileEntity TEChild = world.getTileEntity(childnode);
         ((TileEntityNodeChild)TEChild).setActive(true);
      }

      if (data.getActiveNodes().size() > 1) {
         data.sortActiveNodes(data.getRespawnPos());
      }

      data.setLastPanicTime(world.getTotalWorldTime());
      data.setReadyToPanic(false);
      data.setLastBossTime(world.getTotalWorldTime() - (long)world.rand.nextInt(1000));
      data.setReadyToBoss(false);
      data.setLastSpecialTime(world.getTotalWorldTime());
      data.setReadyToSpecial(false);
      data.setInPanicEvent(false);
      data.setDirectorEnabled(true);
      if (data.getRespawnPos() != data.getSpawnPos() && data.getRespawnPos() != data.getSurvivalSpawnPos() && world.getTileEntity(data.getRespawnPos()) instanceof TileEntitySaferoomNode) {
         TileEntitySaferoomNode tile = (TileEntitySaferoomNode)world.getTileEntity(data.getRespawnPos());
         world.setBlockState(tile.getEntrance().up(), world.getBlockState(tile.getEntrance().up()).cycleProperty(BlockSaferoomDoor.LOCKED));
         world.setBlockState(tile.getEntrance().up().up(), world.getBlockState(tile.getEntrance().up().up()).cycleProperty(BlockSaferoomDoor.LOCKED));
         world.setBlockState(tile.getExit().up(), world.getBlockState(tile.getExit().up()).cycleProperty(BlockSaferoomDoor.LOCKED));
         world.setBlockState(tile.getExit().up().up(), world.getBlockState(tile.getExit().up().up()).cycleProperty(BlockSaferoomDoor.LOCKED));
         world.setBlockState(tile.getExit().up(), world.getBlockState(tile.getExit().up()).withProperty(BlockSaferoomDoor.OPEN, false));
         world.setBlockState(tile.getExit().up().up(), world.getBlockState(tile.getExit().up().up()).withProperty(BlockSaferoomDoor.OPEN, false));
         TileEntitySaferoomDoor tile1 = (TileEntitySaferoomDoor)world.getTileEntity(tile.getEntrance());
         TileEntitySaferoomDoor tile2 = (TileEntitySaferoomDoor)world.getTileEntity(tile.getExit());
         tile1.setSwitched(true);
         tile2.setSwitched(true);
         tile1.setDoorSeed(data.getGameSeed());
         tile2.setDoorSeed(data.getGameSeed());
      }

      if (data.getLastSpawnPos().compareTo(data.getRespawnPos()) != 0) {
         TileEntity lasttileentity = world.getTileEntity(data.getLastSpawnPos());
         ArrayList<BlockPos> lastposlist = ((TileEntityNodeParent)lasttileentity).getChildNodes();

         for(int i = 0; i < lastposlist.size(); ++i) {
            BlockPos childnode = (BlockPos)lastposlist.get(i);
            if (world.getBlockState(childnode).getBlock() instanceof BlockItemSpawn) {
               TileEntity TEChild = world.getTileEntity(childnode);
               ((TileEntityNodeChild)TEChild).setActive(true);
            }
         }
      }

   }

   public static void endGame(World world, boolean win) {
      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      data.randomizeGameSeed();
      if (data.isSurvivalInGame()) {
         data.setSurvivalInGame(false);
         long currentTime = world.getTotalWorldTime() - data.getGameStartTime();
         long bestTime = data.getBestSurvivalTime();
         if (data.hasSurvivalStarted() && currentTime > bestTime) {
            data.setBestSurvivalTime(currentTime);
         }

         data.setSurvivalStarted(false);
         Left2MinePacket.INSTANCE.sendToAll(new SurvivalUpdateMessage(3, data.getGameStartTime(), data.getBestSurvivalTime()));
         Left2MinePacket.INSTANCE.sendToAll(new SurvivalUpdateMessage(1, data.getGameStartTime(), data.getBestSurvivalTime()));
      }

      if (!world.isRemote && !win) {
         Left2MinePacket.INSTANCE.sendToAll(new MusicMessage(2));
      }

      BlockPos childnode;
      int i;
      TileEntity TEChild;
      for(i = 0; i < data.getActiveNodes().size(); ++i) {
         childnode = (BlockPos)data.getActiveNodes().get(i);
         TEChild = world.getTileEntity(childnode);
         if (TEChild != null) {
            ((TileEntityNodeChild)TEChild).setActive(false);
         }
      }

      for(i = 0; i < data.getBossNodes().size(); ++i) {
         childnode = (BlockPos)data.getBossNodes().get(i);
         TEChild = world.getTileEntity(childnode);
         if (TEChild != null) {
            ((TileEntityNodeChild)TEChild).setActive(false);
         }
      }

      Iterator var8 = world.playerEntities.iterator();

      while(var8.hasNext()) {
         EntityPlayer player = (EntityPlayer)var8.next();
         if (!player.isCreative()) {
            player.clearActivePotions();
            player.setGameType(GameType.ADVENTURE);
            player.setPositionAndUpdate((double)world.getSpawnPoint().getX(), (double)world.getSpawnPoint().getY(), (double)world.getSpawnPoint().getZ());
            player.inventory.clear();
            IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
            equip.clearEquipped();
            Left2MinePacket.INSTANCE.sendToAll(new EquippedMessage(equip.getEquippedList(), player));
            if (!world.isRemote) {
               equip.setLives(3);
               Left2MinePacket.INSTANCE.sendTo(new ShaderMessage(0), (EntityPlayerMP)player);
            }
         }
      }

      Left2MinePacket.INSTANCE.sendToAll(new PlayerDeathMessage("", 1));
      world.getGameRules().setOrCreateGameRule("naturalRegeneration", "true");
      data.setDifficulty(0);
      data.resetInfectedCount();
      data.clearActiveNodes();
      data.clearHordeNodes();
      data.clearBossNodes();
      data.setReadyToPanic(false);
      data.setReadyToBoss(false);
      data.setReadyToSpecial(false);
      data.setInPanicEvent(false);
      data.setInGame(false);
      data.setDirectorEnabled(true);
   }

   @Nullable
   public static BlockPos getPathToPos(BlockPos posStart, BlockPos posEnd, World world) {
      float f = 50.0F;
      world.profiler.startSection("pathfind");
      Random rand = new Random();
      EntityLiving infected = new EntityCommonInfected(world);
      WalkCustomDoorProcessor nodeProcessor = new WalkCustomDoorProcessor();
      nodeProcessor.setCanEnterDoors(true);
      nodeProcessor.setCanOpenDoors(true);
      PathFinder pathFinder = new PathFinder(nodeProcessor);
      infected.setPositionAndRotation((double)posStart.getX() + 0.5D, (double)posStart.getY(), (double)posStart.getZ() + 0.5D, rand.nextFloat() * 360.0F - 180.0F, 0.0F);
      int i = (int)(f + 8.0F);
      ChunkCache chunkcache = new ChunkCache(world, posStart.add(-i, -i, -i), posStart.add(i, i, i), 0);
      Path path = pathFinder.findPath(chunkcache, infected, posEnd, f);
      world.profiler.endSection();
      BlockPos endPoint = posStart;
      if (path != null && path.getFinalPathPoint() != null) {
         endPoint = new BlockPos(path.getFinalPathPoint().x, path.getFinalPathPoint().y, path.getFinalPathPoint().z);
      }

      return endPoint;
   }

   @Nullable
   public static Path getFullPathToPos(BlockPos posStart, BlockPos posEnd, World world) {
      float f = 50.0F;
      world.profiler.startSection("pathfind");
      Random rand = new Random();
      EntityLiving infected = new EntityCommonInfected(world);
      WalkCustomDoorProcessor nodeProcessor = new WalkCustomDoorProcessor();
      nodeProcessor.setCanEnterDoors(true);
      nodeProcessor.setCanOpenDoors(true);
      PathFinder pathFinder = new PathFinder(nodeProcessor);
      infected.setPositionAndRotation((double)posStart.getX() + 0.5D, (double)posStart.getY(), (double)posStart.getZ() + 0.5D, rand.nextFloat() * 360.0F - 180.0F, 0.0F);
      int i = (int)(f + 8.0F);
      ChunkCache chunkcache = new ChunkCache(world, posStart.add(-i, -i, -i), posStart.add(i, i, i), 0);
      Path path = pathFinder.findPath(chunkcache, infected, posEnd, f);
      world.profiler.endSection();
      return path;
   }

   public static void winGame(World world) {
      boolean anySpectators = false;
      int count = 0;

      for(Iterator var3 = world.playerEntities.iterator(); var3.hasNext(); ++count) {
         EntityPlayer player = (EntityPlayer)var3.next();
         IStats stats = (IStats)player.getCapability(StatsProvider.STATS, (EnumFacing)null);
         IEquip equip = (IEquip)player.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
         CapabilityStats.updateStats(stats, player);
      }

      WorldDataLeft2Mine data = WorldDataLeft2Mine.get(world);
      VotingUtil.maxVoters = count;
      Left2MinePacket.INSTANCE.sendToAll(new VoteClientMessage(VotingUtil.voting, VotingUtil.maxVoters));
      Left2MinePacket.INSTANCE.sendToAll(new GameStatsMessage(data.getGameStartTime(), data.getDifficulty(), data.getTimesRestarted()));
      endGame(world, true);
   }

   public static boolean canEntityPathToEntity(EntityLivingBase ent, EntityLivingBase ent2) {
      return getPathToPos(ent.getPosition(), ent2.getPosition(), ent.getEntityWorld()).distanceSq(ent2.getPosition()) < 3.0D;
   }
}
