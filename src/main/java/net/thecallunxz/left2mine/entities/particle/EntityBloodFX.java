package net.thecallunxz.left2mine.entities.particle;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBloodFX extends Particle {
   private boolean isCollided;

   public EntityBloodFX(World world, double d, double d1, double d2, double d3, double d4, double d5, double mult) {
      super(world, d, d1, d2, d3, d4, d5);
      this.particleGravity = 1.2F;
      this.particleRed = 1.0F;
      this.particleBlue = 0.0F;
      this.particleGreen = 0.0F;
      double scale = 1.5D * ((1.0D + mult) / 2.0D);
      this.multipleParticleScaleBy((float)scale);
      double expandBB = this.getBoundingBox().getAverageEdgeLength() * (scale - 1.0D);
      this.getBoundingBox().expand(expandBB * 2.0D, expandBB * 2.0D, expandBB * 2.0D);
      this.multiplyVelocity(1.2F);
      this.motionY += (double)(this.rand.nextFloat() * 0.15F);
      this.motionZ *= (double)(0.4F / (this.rand.nextFloat() * 0.9F + 0.1F));
      this.motionX *= (double)(0.4F / (this.rand.nextFloat() * 0.9F + 0.1F));
      this.particleMaxAge = (int)(200.0F + 20.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
      this.setParticleTextureIndex(19 + this.rand.nextInt(4));
      this.isCollided = false;
   }

   public void renderParticle(BufferBuilder tessellator, Entity e, float f, float f1, float f2, float f3, float f4, float f5) {
      super.renderParticle(tessellator, e, f, f1, f2, f3, f4, f5);
   }

   public int getBrightnessForRender(float f) {
      int i = super.getBrightnessForRender(f);
      float f1 = (float)(this.particleAge / this.particleMaxAge);
      f1 *= f1;
      f1 *= f1;
      int j = i & 255;
      int k = i >> 16 & 255;
      k += (int)(f1 * 15.0F * 16.0F);
      if (k > 240) {
         k = 240;
      }

      return j | k << 16;
   }

   public float getBrightness(float f) {
      float f1 = (float)super.getBrightnessForRender(f);
      float f2 = (float)(this.particleAge / this.particleMaxAge);
      f2 *= f2;
      f2 *= f2;
      return f1 * (1.0F - f2) + f2;
   }

   public void move(double x, double y, double z) {
      double d0 = y;
      if (this.canCollide) {
         List<AxisAlignedBB> list = this.world.getCollisionBoxes((Entity)null, this.getBoundingBox().expand(x, y, z));

         Iterator var14;
         AxisAlignedBB axisalignedbb2;
         for(var14 = list.iterator(); var14.hasNext(); this.isCollided = true) {
            axisalignedbb2 = (AxisAlignedBB)var14.next();
            y = axisalignedbb2.calculateYOffset(this.getBoundingBox(), y);
         }

         this.setBoundingBox(this.getBoundingBox().offset(0.0D, y, 0.0D));

         for(var14 = list.iterator(); var14.hasNext(); this.isCollided = true) {
            axisalignedbb2 = (AxisAlignedBB)var14.next();
            x = axisalignedbb2.calculateXOffset(this.getBoundingBox(), x);
         }

         this.setBoundingBox(this.getBoundingBox().offset(x, 0.0D, 0.0D));

         for(var14 = list.iterator(); var14.hasNext(); this.isCollided = true) {
            axisalignedbb2 = (AxisAlignedBB)var14.next();
            z = axisalignedbb2.calculateZOffset(this.getBoundingBox(), z);
         }

         this.setBoundingBox(this.getBoundingBox().offset(0.0D, 0.0D, z));
      } else {
         this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
      }

      this.resetPositionToBB();
      this.onGround = y != y && d0 < 0.0D;
      if (x != x) {
         this.motionX = 0.0D;
      }

      if (z != z) {
         this.motionZ = 0.0D;
      }

   }

   public void onUpdate() {
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (!this.isCollided) {
         this.motionY -= 0.04D * (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.9800000190734863D;
         this.motionY *= 0.9800000190734863D;
         this.motionZ *= 0.9800000190734863D;
         if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
            this.posY += 0.1D;
         }
      } else {
         this.setExpired();
      }

   }

   @SideOnly(Side.CLIENT)
   public static class Factory implements IParticleFactory {
      public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
         return new EntityBloodFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, zSpeedIn);
      }
   }
}
