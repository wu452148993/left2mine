package net.thecallunxz.left2mine.proxy;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.thecallunxz.left2mine.blocks.BlockFakeWall;
import net.thecallunxz.left2mine.config.Left2MineConfig;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityTank;
import net.thecallunxz.left2mine.entities.mobs.specialinfected.EntityWitch;
import net.thecallunxz.left2mine.entities.particle.EntityBloodFX;
import net.thecallunxz.left2mine.entities.particle.EntityFireFX;
import net.thecallunxz.left2mine.events.BacteriaMusicDirector;
import net.thecallunxz.left2mine.events.ClientEventHandler;
import net.thecallunxz.left2mine.gui.GuiElements;
import net.thecallunxz.left2mine.init.InitColourManager;
import net.thecallunxz.left2mine.init.InitEntities;
import net.thecallunxz.left2mine.init.InitKeybinding;
import net.thecallunxz.left2mine.models.ModelFakeWall;
import net.thecallunxz.left2mine.ragdoll.Ragdoll;
import net.thecallunxz.left2mine.render.RenderIconParticle;
import paulscode.sound.SoundSystemConfig;

public class ClientProxy extends CommonProxy {
   public static ClientEventHandler clientevents = new ClientEventHandler();

   public void preInit(FMLPreInitializationEvent e) {
      super.preInit(e);
      StateMapperBase var10000 = new StateMapperBase() {
         protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
            if (iBlockState.getValue(BlockFakeWall.FACING) == EnumFacing.NORTH) {
               return ModelFakeWall.variantTagNorth;
            } else if (iBlockState.getValue(BlockFakeWall.FACING) == EnumFacing.EAST) {
               return ModelFakeWall.variantTagEast;
            } else {
               return iBlockState.getValue(BlockFakeWall.FACING) == EnumFacing.SOUTH ? ModelFakeWall.variantTagSouth : ModelFakeWall.variantTagWest;
            }
         }
      };
      MinecraftForge.EVENT_BUS.register(clientevents);
      MinecraftForge.EVENT_BUS.register(new BacteriaMusicDirector());
      MinecraftForge.EVENT_BUS.register(new GuiElements());
      InitKeybinding.init();
      InitEntities.initModels();
      SoundSystemConfig.setNumberNormalChannels(64);
      SoundSystemConfig.setNumberStreamingChannels(16);
   }

   public void init(FMLInitializationEvent e) {
      super.init(e);
      InitColourManager.registerColourHandlers();
   }

   public void postInit(FMLPostInitializationEvent e) {
      super.postInit(e);
   }

   public void registerItemRenderer(Item item, int meta, String id) {
      ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("left2mine:" + id, "inventory"));
   }

   public boolean isClient() {
      return true;
   }

   public void renderNodeParticles(World world, BlockPos pos, Item itemFromBlock, boolean b, float redColour, float blueColour, float greenColour) {
      if (world.isRemote && Minecraft.getMinecraft().player.isCreative() && Math.sqrt(pos.distanceSq(Minecraft.getMinecraft().player.getPosition())) < 25.0D) {
         Particle particle = new RenderIconParticle(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemFromBlock, b, redColour, blueColour, greenColour);
         Minecraft.getMinecraft().effectRenderer.addEffect(particle);
      }

   }

   public void renderFireParticles(World world, BlockPos pos) {
      if (world.isRemote) {
         for(int j = 0; j < 2; ++j) {
            double addX = world.rand.nextDouble();
            double addY = world.rand.nextDouble() * 0.5D + 0.1D;
            double addZ = world.rand.nextDouble();
            Particle flame = new EntityFireFX(world, (double)pos.getX() + addX, (double)pos.getY() + addY, (double)pos.getZ() + addZ, 0.0D, 0.15D, 0.0D);
            Minecraft.getMinecraft().effectRenderer.addEffect(flame);
         }
      }

   }

   public void renderProximityParticles(World world, BlockPos pos, Item itemFromBlock, boolean b, float redColour, float blueColour, float greenColour, EnumFacing facing) {
      if (world.isRemote && Minecraft.getMinecraft().player.isCreative() && Math.sqrt(pos.distanceSq(Minecraft.getMinecraft().player.getPosition())) < 25.0D) {
         Particle particle = new RenderIconParticle(world, (double)pos.getX() + 0.5D + (double)facing.getDirectionVec().getX() * 0.75D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D + (double)facing.getDirectionVec().getZ() * 0.75D, itemFromBlock, b, redColour, blueColour, greenColour);
         Minecraft.getMinecraft().effectRenderer.addEffect(particle);
      }

   }

   public void displayMobFire(World world, double d, double e, double f, int i, double g, int j) {
      Particle flame = new EntityFireFX(world, d, e, f, (double)i, g, (double)j);
      Minecraft.getMinecraft().effectRenderer.addEffect(flame);
   }

   public Vec3d scaleNoY(Vec3d vec, double scale) {
      return new Vec3d(vec.x * scale, vec.y * scale / 2.0D, vec.z * scale);
   }

   public void spawnCorpse(Entity entity, Vec3d vec, float power, float damage, float YOffset) {
      if (Left2MineConfig.maxCorpseCount >= 1) {
         if (clientevents.corpseListSize() > Left2MineConfig.maxCorpseCount) {
            clientevents.removeCorpse(0);
         }

         Vec3d eyeVec = vec.addVector(0.0D, (double)entity.getEyeHeight(), 0.0D);
         Vec3d corpse = new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);
         double scale = (double)(1000.0F * power) - 250.0D * eyeVec.distanceTo(corpse) / (double)damage;
         Vec3d vecNew = corpse.subtract(eyeVec).normalize();
         Vec3d vecScaled = this.scaleNoY(vecNew, scale);
         Ragdoll ragdoll = new Ragdoll(entity);
         clientevents.addCorpse(ragdoll);
         this.addBlood((EntityLivingBase)entity, 100);
         if (!(entity instanceof EntityTank) && !(entity instanceof EntityWitch)) {
            ragdoll.skeleton.push(vecScaled.x, vecScaled.y + 7.5D, vecScaled.z);
         } else {
            ragdoll.skeleton.push(vecScaled.x * 0.25D, 0.0D, vecScaled.z * 0.25D);
         }

      }
   }

   public void pipebombExplosion(World world, double x, double y, double z) {
      world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 1.0D, 0.0D, 0.0D, new int[0]);
      world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 1.0D, 0.0D, 0.0D, new int[0]);
   }

   public void spawnCorpseExplosion(Entity entity, double x, double y, double z) {
      if (clientevents.corpseListSize() > 40) {
         clientevents.removeCorpse(0);
      }

      Ragdoll ragdoll = new Ragdoll((float)entity.posX + 0.5F, (float)entity.posY + 5.0F, (float)entity.posZ + 0.5F, entity);
      Vec3d corpse = new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);
      Vec3d vecNew = corpse.subtract(new Vec3d(x, y, z)).normalize().scale(2500.0D);
      clientevents.addCorpse(ragdoll);
      this.addBlood((EntityLivingBase)entity, 10);
      ragdoll.skeleton.push(vecNew.x, vecNew.y, vecNew.z);
   }

   public ClientEventHandler getClientHandler() {
      return clientevents;
   }

   public void addBlood(EntityLivingBase living, int amount, boolean onhit) {
      if (onhit && (Left2MineConfig.bloodAmount == 3 || Left2MineConfig.bloodAmount == 2)) {
         this.addBlood(living, amount);
      }

   }

   public void addBlood(EntityLivingBase living, int amount) {
      if (Left2MineConfig.bloodAmount <= 3 && Left2MineConfig.bloodAmount >= 1) {
         for(int k = 0; k < amount; ++k) {
            float var4 = 0.3F;
            double mX = (double)(-MathHelper.sin(living.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(living.rotationPitch / 180.0F * 3.1415927F) * var4);
            double mZ = (double)(MathHelper.cos(living.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(living.rotationPitch / 180.0F * 3.1415927F) * var4);
            double mY = (double)(-MathHelper.sin(living.rotationPitch / 180.0F * 3.1415927F) * var4 + 0.1F);
            var4 = 0.02F;
            float var5 = living.getRNG().nextFloat() * 3.1415927F * 2.0F;
            var4 *= living.getRNG().nextFloat();
            mX += Math.cos((double)var5) * (double)var4;
            mY += (double)((living.getRNG().nextFloat() - living.getRNG().nextFloat()) * 0.1F);
            mZ += Math.sin((double)var5) * (double)var4;
            Particle blood = new EntityBloodFX(living.getEntityWorld(), living.posX, living.posY + 0.5D + living.getRNG().nextDouble() * 0.7D, living.posZ, living.motionX * 2.0D + mX, living.motionY + mY, living.motionZ * 2.0D + mZ, 0.0D);
            Minecraft.getMinecraft().effectRenderer.addEffect(blood);
         }

      }
   }
}
