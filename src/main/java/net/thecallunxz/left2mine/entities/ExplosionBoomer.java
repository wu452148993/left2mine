package net.thecallunxz.left2mine.entities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.capabilities.EquipProvider;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.init.InitPotions;
import net.thecallunxz.left2mine.init.InitSounds;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.BoomerFXMessage;
import net.thecallunxz.left2mine.util.Left2MineUtilities;

public class ExplosionBoomer extends Explosion {
   private final boolean causesFire;
   private final boolean damagesTerrain;
   private final Random random;
   private final World world;
   private final double x;
   private final double y;
   private final double z;
   private final Entity exploder;
   private final float size;
   private final List<BlockPos> affectedBlockPositions;
   private final Map<EntityPlayer, Vec3d> playerKnockbackMap;
   private final Vec3d position;

   @SideOnly(Side.CLIENT)
   public ExplosionBoomer(World worldIn, Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
      this(worldIn, entityIn, x, y, z, size, false, true, affectedPositions);
   }

   @SideOnly(Side.CLIENT)
   public ExplosionBoomer(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean causesFire, boolean damagesTerrain, List<BlockPos> affectedPositions) {
      this(worldIn, entityIn, x, y, z, size, causesFire, damagesTerrain);
      this.affectedBlockPositions.addAll(affectedPositions);
   }

   public ExplosionBoomer(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain) {
      super(worldIn, entityIn, x, y, z, size, flaming, damagesTerrain);
      this.random = new Random();
      this.affectedBlockPositions = Lists.newArrayList();
      this.playerKnockbackMap = Maps.newHashMap();
      this.world = worldIn;
      this.exploder = entityIn;
      this.size = size;
      this.x = x;
      this.y = y;
      this.z = z;
      this.causesFire = flaming;
      this.damagesTerrain = damagesTerrain;
      this.position = new Vec3d(this.x, this.y, this.z);
   }

   public void doExplosionA() {
      Set<BlockPos> set = Sets.newHashSet();
      int i = true;

      int k;
      int l;
      for(int j = 0; j < 16; ++j) {
         for(k = 0; k < 16; ++k) {
            for(l = 0; l < 16; ++l) {
               if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                  double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                  double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                  double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                  double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                  d0 /= d3;
                  d1 /= d3;
                  d2 /= d3;
                  float f = this.size * (0.7F + this.world.rand.nextFloat() * 0.6F);
                  double d4 = this.x;
                  double d6 = this.y;
                  double d8 = this.z;

                  for(float var21 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                     BlockPos blockpos = new BlockPos(d4, d6, d8);
                     IBlockState iblockstate = this.world.getBlockState(blockpos);
                     if (iblockstate.getMaterial() != Material.AIR) {
                        float f2 = this.exploder != null ? this.exploder.getExplosionResistance(this, this.world, blockpos, iblockstate) : iblockstate.getBlock().getExplosionResistance(this.world, blockpos, (Entity)null, this);
                        f -= (f2 + 0.3F) * 0.3F;
                     }

                     if (f > 0.0F && (this.exploder == null || this.exploder.canExplosionDestroyBlock(this, this.world, blockpos, iblockstate, f))) {
                        set.add(blockpos);
                     }

                     d4 += d0 * 0.30000001192092896D;
                     d6 += d1 * 0.30000001192092896D;
                     d8 += d2 * 0.30000001192092896D;
                  }
               }
            }
         }
      }

      this.affectedBlockPositions.addAll(set);
      float f3 = this.size * 2.0F;
      k = MathHelper.floor(this.x - (double)f3 - 1.0D);
      l = MathHelper.floor(this.x + (double)f3 + 1.0D);
      int i2 = MathHelper.floor(this.y - (double)f3 - 1.0D);
      int i1 = MathHelper.floor(this.y + (double)f3 + 1.0D);
      int j2 = MathHelper.floor(this.z - (double)f3 - 1.0D);
      int j1 = MathHelper.floor(this.z + (double)f3 + 1.0D);
      List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB((double)k, (double)i2, (double)j2, (double)l, (double)i1, (double)j1));
      ForgeEventFactory.onExplosionDetonate(this.world, this, list, (double)f3);
      Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

      for(int k2 = 0; k2 < list.size(); ++k2) {
         Entity entity = (Entity)list.get(k2);
         if (!entity.isImmuneToExplosions()) {
            double d12 = entity.getDistance(this.x, this.y, this.z) / (double)f3;
            if (d12 <= 1.0D) {
               double d5 = entity.posX - this.x;
               double d7 = entity.posY + (double)entity.getEyeHeight() - this.y;
               double d9 = entity.posZ - this.z;
               double d13 = (double)MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
               if (d13 != 0.0D) {
                  d5 /= d13;
                  d7 /= d13;
                  d9 /= d13;
                  double d14 = (double)this.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
                  double d10 = (1.0D - d12) * d14;
                  double d11 = d10;
                  if (entity instanceof EntityLivingBase) {
                     d11 = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase)entity, d10);
                  }

                  if (Left2MineUtilities.canEntitySeeEntity(this.exploder, entity, this.world) && entity instanceof EntityPlayer) {
                     EntityPlayer entityplayer = (EntityPlayer)entity;
                     entity.motionX += d5 * d11 * 5.0D;
                     entity.motionZ += d9 * d11 * 5.0D;
                     if (!entityplayer.isSpectator()) {
                        IEquip equip = (IEquip)entityplayer.getCapability(EquipProvider.EQUIPPED, (EnumFacing)null);
                        if (!equip.isPuked() || !entityplayer.isPotionActive(InitPotions.puked)) {
                           entityplayer.addPotionEffect(new PotionEffect(InitPotions.puked, 260, 0, true, false));
                        }

                        this.playerKnockbackMap.put(entityplayer, (new Vec3d(d5 * d10, d7 * d10, d9 * d10)).scale(5.0D));
                     }
                  }
               }
            }
         }
      }

   }

   public void doExplosionB(boolean spawnParticles) {
      this.world.playSound((EntityPlayer)null, new BlockPos(this.x, this.y, this.z), InitSounds.boomer_explode, SoundCategory.HOSTILE, 8.0F, 1.0F);
      if (this.size >= 2.0F && this.damagesTerrain) {
      }

      Iterator var2;
      BlockPos blockpos1;
      if (this.damagesTerrain) {
         var2 = this.affectedBlockPositions.iterator();

         while(var2.hasNext()) {
            blockpos1 = (BlockPos)var2.next();
            IBlockState iblockstate = this.world.getBlockState(blockpos1);
            Block block = iblockstate.getBlock();
            double d0 = (double)((float)blockpos1.getX() + this.world.rand.nextFloat());
            double d1 = (double)((float)blockpos1.getY() + this.world.rand.nextFloat());
            double d2 = (double)((float)blockpos1.getZ() + this.world.rand.nextFloat());
            double d3 = d0 - this.x;
            double d4 = d1 - this.y;
            double d5 = d2 - this.z;
            double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.5D / (d6 / 2.5D + 0.1D);
            d7 *= (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
            d3 *= d7;
            d4 *= d7;
            d5 *= d7;
            Left2MinePacket.INSTANCE.sendToAll(new BoomerFXMessage((d0 + this.x) / 2.0D, (d1 + this.y + 1.25D) / 2.0D, (d2 + this.z) / 2.0D, d3, d4, d5, d0, d1, d2));
         }
      }

      if (this.causesFire) {
         var2 = this.affectedBlockPositions.iterator();

         while(var2.hasNext()) {
            blockpos1 = (BlockPos)var2.next();
            if (this.world.getBlockState(blockpos1).getMaterial() == Material.AIR && this.world.getBlockState(blockpos1.down()).isFullBlock() && this.random.nextInt(3) == 0) {
               this.world.setBlockState(blockpos1, Blocks.FIRE.getDefaultState());
            }
         }
      }

   }

   public Map<EntityPlayer, Vec3d> getPlayerKnockbackMap() {
      return this.playerKnockbackMap;
   }

   @Nullable
   public EntityLivingBase getExplosivePlacedBy() {
      if (this.exploder == null) {
         return null;
      } else if (this.exploder instanceof EntityTNTPrimed) {
         return ((EntityTNTPrimed)this.exploder).getTntPlacedBy();
      } else {
         return this.exploder instanceof EntityLivingBase ? (EntityLivingBase)this.exploder : null;
      }
   }

   public void clearAffectedBlockPositions() {
      this.affectedBlockPositions.clear();
   }

   public List<BlockPos> getAffectedBlockPositions() {
      return this.affectedBlockPositions;
   }

   public Vec3d getPosition() {
      return this.position;
   }
}
