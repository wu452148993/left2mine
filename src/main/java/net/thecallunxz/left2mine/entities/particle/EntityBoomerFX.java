package net.thecallunxz.left2mine.entities.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityBoomerFX extends Particle {
   public EntityBoomerFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double maxAge) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.motionX = xSpeedIn + (Math.random() * 2.0D - 1.0D) * 0.05000000074505806D;
      this.motionY = ySpeedIn + (Math.random() * 2.0D - 1.0D) * 0.05000000074505806D;
      this.motionZ = zSpeedIn + (Math.random() * 2.0D - 1.0D) * 0.05000000074505806D;
      float f = this.rand.nextFloat() * 0.3F + 0.2F;
      this.particleRed = 0.0F;
      this.particleGreen = f;
      this.particleBlue = 0.0F;
      this.particleScale = this.rand.nextFloat() * this.rand.nextFloat() * 6.0F + 1.0F;
      this.particleMaxAge = (int)(maxAge / ((double)this.rand.nextFloat() * 0.8D + 0.8D)) + 4;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

      this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
      this.motionY += 0.004D;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.8999999761581421D;
      this.motionY *= 0.8999999761581421D;
      this.motionZ *= 0.8999999761581421D;
      if (this.onGround) {
         this.motionX *= 0.699999988079071D;
         this.motionZ *= 0.699999988079071D;
      }

   }

   @SideOnly(Side.CLIENT)
   public static class Factory implements IParticleFactory {
      public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
         return new EntityBoomerFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, 16.0D);
      }
   }
}
