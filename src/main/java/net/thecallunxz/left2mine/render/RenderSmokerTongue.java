package net.thecallunxz.left2mine.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.Main;

@SideOnly(Side.CLIENT)
public class RenderSmokerTongue extends Particle {
   public RenderSmokerTongue(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z, 0.0D, 0.0D, 0.0D);
      this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Main.tongueParticle.toString()));
      this.particleAlpha = 1.0F;
      this.particleRed = 1.0F;
      this.particleBlue = 1.0F;
      this.particleGreen = 1.0F;
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.particleScale = 1.0F;
      this.particleGravity = 0.0F;
      this.particleMaxAge = 1;
   }

   public int getFXLayer() {
      return 1;
   }

   @SideOnly(Side.CLIENT)
   public static class Factory implements IParticleFactory {
      public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
         return new RenderSmokerTongue(worldIn, xCoordIn, yCoordIn, zCoordIn);
      }
   }
}
