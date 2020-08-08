package net.thecallunxz.left2mine.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thecallunxz.left2mine.Main;

@SideOnly(Side.CLIENT)
public class RenderIconParticle extends Particle {
   public RenderIconParticle(World worldIn, double p_i46286_2_, double p_i46286_4_, double p_i46286_6_, Item p_i46286_8_, boolean error, float redColour, float blueColour, float greenColour) {
      super(worldIn, p_i46286_2_, p_i46286_4_, p_i46286_6_, 0.0D, 0.0D, 0.0D);
      if (p_i46286_8_ != null) {
         this.setParticleTexture(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(p_i46286_8_));
      } else if (error) {
         this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Main.crossParticle.toString()));
      } else {
         this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Main.tickParticle.toString()));
      }

      this.particleAlpha = 1.0F;
      this.particleRed = redColour;
      this.particleBlue = blueColour;
      this.particleGreen = greenColour;
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.particleGravity = 0.0F;
      this.particleMaxAge = 1;
   }

   public int getFXLayer() {
      return 1;
   }

   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = this.particleTexture.getMinU();
      float f1 = this.particleTexture.getMaxU();
      float f2 = this.particleTexture.getMinV();
      float f3 = this.particleTexture.getMaxV();
      float f4 = 0.5F;
      float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      int j = i >> 16 & '\uffff';
      int k = i & '\uffff';
      buffer.pos((double)(f5 - rotationX * 0.5F - rotationXY * 0.5F), (double)(f6 - rotationZ * 0.5F), (double)(f7 - rotationYZ * 0.5F - rotationXZ * 0.5F)).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
      buffer.pos((double)(f5 - rotationX * 0.5F + rotationXY * 0.5F), (double)(f6 + rotationZ * 0.5F), (double)(f7 - rotationYZ * 0.5F + rotationXZ * 0.5F)).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
      buffer.pos((double)(f5 + rotationX * 0.5F + rotationXY * 0.5F), (double)(f6 + rotationZ * 0.5F), (double)(f7 + rotationYZ * 0.5F + rotationXZ * 0.5F)).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
      buffer.pos((double)(f5 + rotationX * 0.5F - rotationXY * 0.5F), (double)(f6 - rotationZ * 0.5F), (double)(f7 + rotationYZ * 0.5F - rotationXZ * 0.5F)).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
   }

   @SideOnly(Side.CLIENT)
   public static class Factory implements IParticleFactory {
      public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
         return new RenderIconParticle(worldIn, xCoordIn, yCoordIn, zCoordIn, Item.getItemFromBlock(Blocks.BARRIER), false, 0.0F, 0.0F, 0.0F);
      }
   }
}
