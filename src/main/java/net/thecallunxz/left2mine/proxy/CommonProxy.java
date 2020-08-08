package net.thecallunxz.left2mine.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.thecallunxz.left2mine.Main;
import net.thecallunxz.left2mine.capabilities.CapabilityEquip;
import net.thecallunxz.left2mine.capabilities.CapabilityStats;
import net.thecallunxz.left2mine.capabilities.EquipStorage;
import net.thecallunxz.left2mine.capabilities.IEquip;
import net.thecallunxz.left2mine.capabilities.IStats;
import net.thecallunxz.left2mine.capabilities.StatsStorage;
import net.thecallunxz.left2mine.events.ClientEventHandler;
import net.thecallunxz.left2mine.events.CommonEventHandler;
import net.thecallunxz.left2mine.events.TickableAIDirector;
import net.thecallunxz.left2mine.init.InitBlocks;
import net.thecallunxz.left2mine.init.InitChunkHandler;
import net.thecallunxz.left2mine.init.InitEntities;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.AnimationAngleMessage;
import net.thecallunxz.left2mine.networking.client.BoomerCorpseMessage;
import net.thecallunxz.left2mine.networking.client.BoomerFXMessage;
import net.thecallunxz.left2mine.networking.client.ChangeSlotMessage;
import net.thecallunxz.left2mine.networking.client.ClientCreditsMessage;
import net.thecallunxz.left2mine.networking.client.ClientSoundMessage;
import net.thecallunxz.left2mine.networking.client.CorpseGunMessage;
import net.thecallunxz.left2mine.networking.client.CorpsePushMessage;
import net.thecallunxz.left2mine.networking.client.DecalMessage;
import net.thecallunxz.left2mine.networking.client.EquippedMessage;
import net.thecallunxz.left2mine.networking.client.ExplosionDustMessage;
import net.thecallunxz.left2mine.networking.client.ExplosionFXMessage;
import net.thecallunxz.left2mine.networking.client.GameStatsMessage;
import net.thecallunxz.left2mine.networking.client.HunterCorpseMessage;
import net.thecallunxz.left2mine.networking.client.HurtArrowMessage;
import net.thecallunxz.left2mine.networking.client.LyingMessage;
import net.thecallunxz.left2mine.networking.client.MovingSoundMessage;
import net.thecallunxz.left2mine.networking.client.MusicMessage;
import net.thecallunxz.left2mine.networking.client.PinnedMessage;
import net.thecallunxz.left2mine.networking.client.PlayerDeathMessage;
import net.thecallunxz.left2mine.networking.client.PukedMessage;
import net.thecallunxz.left2mine.networking.client.ShaderMessage;
import net.thecallunxz.left2mine.networking.client.SmokerCorpseMessage;
import net.thecallunxz.left2mine.networking.client.StatsMessage;
import net.thecallunxz.left2mine.networking.client.SurvivalUpdateMessage;
import net.thecallunxz.left2mine.networking.client.TankCorpseMessage;
import net.thecallunxz.left2mine.networking.client.TipMessage;
import net.thecallunxz.left2mine.networking.client.VoteClientMessage;
import net.thecallunxz.left2mine.networking.client.WitchCorpseMessage;
import net.thecallunxz.left2mine.networking.server.FlashlightMessage;
import net.thecallunxz.left2mine.networking.server.ReloadMessage;
import net.thecallunxz.left2mine.networking.server.ShootMessage;
import net.thecallunxz.left2mine.networking.server.VoteServerMessage;

public class CommonProxy {
   public TickableAIDirector director = new TickableAIDirector();

   public void preInit(FMLPreInitializationEvent e) {
      Left2MinePacket.INSTANCE.registerMessage(EquippedMessage.Handler.class, EquippedMessage.class, 0, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(ShootMessage.Handler.class, ShootMessage.class, 1, Side.SERVER);
      Left2MinePacket.INSTANCE.registerMessage(ReloadMessage.Handler.class, ReloadMessage.class, 2, Side.SERVER);
      Left2MinePacket.INSTANCE.registerMessage(MusicMessage.Handler.class, MusicMessage.class, 3, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(ClientSoundMessage.Handler.class, ClientSoundMessage.class, 4, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(LyingMessage.Handler.class, LyingMessage.class, 5, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(CorpsePushMessage.Handler.class, CorpsePushMessage.class, 6, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(ExplosionFXMessage.Handler.class, ExplosionFXMessage.class, 7, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(ExplosionDustMessage.Handler.class, ExplosionDustMessage.class, 8, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(CorpseGunMessage.Handler.class, CorpseGunMessage.class, 9, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(ClientCreditsMessage.Handler.class, ClientCreditsMessage.class, 10, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(StatsMessage.Handler.class, StatsMessage.class, 11, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(GameStatsMessage.Handler.class, GameStatsMessage.class, 12, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(PlayerDeathMessage.Handler.class, PlayerDeathMessage.class, 13, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(HurtArrowMessage.Handler.class, HurtArrowMessage.class, 14, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(MovingSoundMessage.Handler.class, MovingSoundMessage.class, 15, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(HunterCorpseMessage.Handler.class, HunterCorpseMessage.class, 16, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(PinnedMessage.Handler.class, PinnedMessage.class, 17, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(ChangeSlotMessage.Handler.class, ChangeSlotMessage.class, 18, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(AnimationAngleMessage.Handler.class, AnimationAngleMessage.class, 19, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(ShaderMessage.Handler.class, ShaderMessage.class, 20, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(SurvivalUpdateMessage.Handler.class, SurvivalUpdateMessage.class, 21, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(BoomerFXMessage.Handler.class, BoomerFXMessage.class, 22, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(PukedMessage.Handler.class, PukedMessage.class, 23, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(SmokerCorpseMessage.Handler.class, SmokerCorpseMessage.class, 24, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(TipMessage.Handler.class, TipMessage.class, 25, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(VoteClientMessage.Handler.class, VoteClientMessage.class, 26, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(VoteServerMessage.Handler.class, VoteServerMessage.class, 27, Side.SERVER);
      Left2MinePacket.INSTANCE.registerMessage(BoomerCorpseMessage.Handler.class, BoomerCorpseMessage.class, 28, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(TankCorpseMessage.Handler.class, TankCorpseMessage.class, 29, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(FlashlightMessage.Handler.class, FlashlightMessage.class, 30, Side.SERVER);
      Left2MinePacket.INSTANCE.registerMessage(WitchCorpseMessage.Handler.class, WitchCorpseMessage.class, 31, Side.CLIENT);
      Left2MinePacket.INSTANCE.registerMessage(DecalMessage.Handler.class, DecalMessage.class, 32, Side.CLIENT);
      MinecraftForge.EVENT_BUS.register(this.director);
      MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
      CapabilityManager.INSTANCE.register(IEquip.class, new EquipStorage(), CapabilityEquip::new);
      CapabilityManager.INSTANCE.register(IStats.class, new StatsStorage(), CapabilityStats::new);
      InitBlocks.registerTileEntities();
      InitEntities.register();
   }

   public void init(FMLInitializationEvent e) {
      ForgeChunkManager.setForcedChunkLoadingCallback(Main.instance, new InitChunkHandler());
   }

   public void postInit(FMLPostInitializationEvent e) {
   }

   public void registerItemRenderer(Item item, int meta, String id) {
   }

   public ClientEventHandler getClientHandler() {
      return null;
   }

   public boolean isClient() {
      return false;
   }

   public void renderNodeParticles(World world, BlockPos pos, Item itemFromBlock, boolean bool, float redColour, float blueColour, float greenColour) {
   }

   public void renderFireParticles(World world, BlockPos pos) {
   }

   public void spawnCorpseExplosion(Entity entity, double motionX, double motionY, double motionZ) {
   }

   public void spawnCorpse(Entity entity, Vec3d vec3d, float power, float damage, float YOffset) {
   }

   public void addBlood(EntityLivingBase living, int amount) {
   }

   public void addBlood(EntityLivingBase living, int amount, boolean onhit) {
   }

   public void pipebombExplosion(World world, double x, double y, double z) {
   }

   public void renderProximityParticles(World world, BlockPos pos, Item itemFromBlock, boolean b, float redColour, float blueColour, float greenColour, EnumFacing facing) {
   }

   public void displayMobFire(World world, double d, double e, double f, int i, double g, int j) {
   }
}
