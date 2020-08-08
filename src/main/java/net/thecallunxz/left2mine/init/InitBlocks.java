package net.thecallunxz.left2mine.init;

import com.google.common.base.Preconditions;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import net.thecallunxz.left2mine.ItemModelProvider;
import net.thecallunxz.left2mine.blocks.BlockAmmoPile;
import net.thecallunxz.left2mine.blocks.BlockChunkLoader;
import net.thecallunxz.left2mine.blocks.BlockDeathTouch;
import net.thecallunxz.left2mine.blocks.BlockDoorSpawn;
import net.thecallunxz.left2mine.blocks.BlockFakeWall;
import net.thecallunxz.left2mine.blocks.BlockFencePole;
import net.thecallunxz.left2mine.blocks.BlockGameDoor;
import net.thecallunxz.left2mine.blocks.BlockLightDirectional;
import net.thecallunxz.left2mine.blocks.BlockLightSourceWalls;
import net.thecallunxz.left2mine.blocks.BlockMolotovFire;
import net.thecallunxz.left2mine.blocks.BlockNoPath;
import net.thecallunxz.left2mine.blocks.BlockProximitySensor;
import net.thecallunxz.left2mine.blocks.BlockRedstoneLock;
import net.thecallunxz.left2mine.blocks.BlockResetSeed;
import net.thecallunxz.left2mine.blocks.BlockSaferoomDoor;
import net.thecallunxz.left2mine.blocks.BlockTrafficCone;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockBossSpawn;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockHordeSpawn;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockItemSpawn;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockPlayerSpawn;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockSaferoomDoorNode;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockSaferoomNode;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockSurvivalSpawn;
import net.thecallunxz.left2mine.blocks.nodeblocks.BlockZombieSpawn;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityChunkLoader;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityDoorSpawn;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityGameDoor;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityItemNode;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityMolotovFire;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNameRender;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeChild;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityNodeParent;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityProximitySensor;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityRedstoneLock;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityResetSeed;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntitySaferoomDoor;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntitySaferoomNode;

@ObjectHolder("left2mine")
public class InitBlocks {
   public static final BlockZombieSpawn zombiespawn = new BlockZombieSpawn("zombiespawn");
   public static final BlockBossSpawn bossspawn = new BlockBossSpawn("bossspawn");
   public static final BlockHordeSpawn hordespawn = new BlockHordeSpawn("hordespawn");
   public static final BlockPlayerSpawn playerspawn = new BlockPlayerSpawn("playerspawn");
   public static final BlockSurvivalSpawn survivalspawn = new BlockSurvivalSpawn("survivalspawn");
   public static final BlockItemSpawn itemspawn = new BlockItemSpawn("itemspawn");
   public static final BlockDoorSpawn doorspawn = new BlockDoorSpawn("doorspawn");
   public static final BlockSaferoomNode saferoomnode = new BlockSaferoomNode("saferoomnode");
   public static final BlockSaferoomDoorNode saferoomdoornode = new BlockSaferoomDoorNode("saferoomdoornode");
   public static final BlockChunkLoader chunkloader = new BlockChunkLoader("chunkloader");
   public static final BlockDeathTouch deathblock = new BlockDeathTouch("deathblock");
   public static final BlockProximitySensor proximitysensor = new BlockProximitySensor("proximitysensor");
   public static final BlockRedstoneLock redstonelock = new BlockRedstoneLock("redstonelock");
   public static final BlockResetSeed seedresetter = new BlockResetSeed("seedresetter");
   public static final BlockSaferoomDoor saferoomdoor = new BlockSaferoomDoor("saferoomdoor");
   public static final BlockAmmoPile ammopile = new BlockAmmoPile("ammopile");
   public static final BlockNoPath nopath = new BlockNoPath("nopath");
   public static final BlockMolotovFire molotovfire = new BlockMolotovFire("molotovfire");
   public static final BlockLightSourceWalls light1 = new BlockLightSourceWalls("light1");
   public static final BlockLightSourceWalls light2 = new BlockLightSourceWalls("light2");
   public static final BlockLightDirectional light3 = new BlockLightDirectional("light3");
   public static final BlockLightDirectional light4 = new BlockLightDirectional("light4");
   public static final BlockLightSourceWalls light5 = new BlockLightSourceWalls("light5", 0.5F);
   public static final BlockLightSourceWalls light6 = new BlockLightSourceWalls("light6", 0.5F);
   public static final BlockTrafficCone trafficcone1 = new BlockTrafficCone("trafficcone1", false);
   public static final BlockTrafficCone trafficcone2 = new BlockTrafficCone("trafficcone2", true);
   public static final BlockTrafficCone trafficcone3 = new BlockTrafficCone("trafficcone3", false);
   public static final BlockFencePole lightgraypole = new BlockFencePole("lightgraypole");
   public static final BlockFencePole lightgraywall = new BlockFencePole("lightgraywalls");
   public static final BlockGameDoor gamedoor1 = new BlockGameDoor("gamedoor1");
   public static final BlockGameDoor gamedoorsaferoom;
   public static final BlockFakeWall fakewall;

   public static void registerTileEntities() {
      registerTileEntity(TileEntityNodeChild.class);
      registerTileEntity(TileEntityNodeParent.class);
      registerTileEntity(TileEntityItemNode.class);
      registerTileEntity(TileEntityDoorSpawn.class);
      registerTileEntity(TileEntitySaferoomNode.class);
      registerTileEntity(TileEntitySaferoomDoor.class);
      registerTileEntity(TileEntityChunkLoader.class);
      registerTileEntity(TileEntityNameRender.class);
      registerTileEntity(TileEntityProximitySensor.class);
      registerTileEntity(TileEntityResetSeed.class);
      registerTileEntity(TileEntityGameDoor.class);
      registerTileEntity(TileEntityRedstoneLock.class);
      registerTileEntity(TileEntityMolotovFire.class);
   }

   private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass) {
      GameRegistry.registerTileEntity(tileEntityClass, "left2mine:" + tileEntityClass.getSimpleName().replaceFirst("TileEntity", ""));
   }

   static {
      gamedoorsaferoom = new BlockGameDoor(Material.IRON, "gamedoorsaferoom");
      fakewall = new BlockFakeWall("fakewall");
   }

   @EventBusSubscriber(
      modid = "left2mine"
   )
   public static class RegistrationHandler {
      public static final Set<ItemBlock> ITEM_BLOCKS = new HashSet();

      @SubscribeEvent
      public static void registerBlocks(Register<Block> event) {
         IForgeRegistry<Block> registry = event.getRegistry();
         Block[] blocks = new Block[]{InitBlocks.playerspawn, InitBlocks.survivalspawn, InitBlocks.saferoomnode, InitBlocks.zombiespawn, InitBlocks.hordespawn, InitBlocks.bossspawn, InitBlocks.itemspawn, InitBlocks.doorspawn, InitBlocks.saferoomdoornode, InitBlocks.saferoomdoor, InitBlocks.chunkloader, InitBlocks.nopath, InitBlocks.deathblock, InitBlocks.proximitysensor, InitBlocks.redstonelock, InitBlocks.ammopile, InitBlocks.seedresetter, InitBlocks.molotovfire, InitBlocks.light1, InitBlocks.light2, InitBlocks.light3, InitBlocks.light4, InitBlocks.light5, InitBlocks.light6, InitBlocks.trafficcone1, InitBlocks.trafficcone2, InitBlocks.trafficcone3, InitBlocks.lightgraypole, InitBlocks.lightgraywall, InitBlocks.gamedoor1, InitBlocks.gamedoorsaferoom, InitBlocks.fakewall};
         registry.registerAll(blocks);
      }

      @SubscribeEvent
      public static void registerItemBlocks(Register<Item> event) {
         ItemBlock[] items = new ItemBlock[]{new ItemBlock(InitBlocks.proximitysensor), new ItemBlock(InitBlocks.redstonelock), new ItemBlock(InitBlocks.seedresetter), new ItemBlock(InitBlocks.ammopile), new ItemBlock(InitBlocks.survivalspawn), new ItemBlock(InitBlocks.playerspawn), new ItemBlock(InitBlocks.zombiespawn), new ItemBlock(InitBlocks.itemspawn), new ItemBlock(InitBlocks.hordespawn), new ItemBlock(InitBlocks.bossspawn), new ItemBlock(InitBlocks.doorspawn), new ItemBlock(InitBlocks.saferoomnode), new ItemBlock(InitBlocks.saferoomdoornode), new ItemBlock(InitBlocks.saferoomdoor), new ItemBlock(InitBlocks.nopath), new ItemBlock(InitBlocks.deathblock), new ItemBlock(InitBlocks.molotovfire), new ItemBlock(InitBlocks.chunkloader), new ItemBlock(InitBlocks.light1), new ItemBlock(InitBlocks.light2), new ItemBlock(InitBlocks.light3), new ItemBlock(InitBlocks.light4), new ItemBlock(InitBlocks.light5), new ItemBlock(InitBlocks.light6), new ItemBlock(InitBlocks.trafficcone1), new ItemBlock(InitBlocks.trafficcone2), new ItemBlock(InitBlocks.trafficcone3), new ItemBlock(InitBlocks.lightgraypole), new ItemBlock(InitBlocks.lightgraywall), new ItemBlock(InitBlocks.gamedoor1), new ItemBlock(InitBlocks.gamedoorsaferoom), new ItemBlock(InitBlocks.fakewall)};
         IForgeRegistry<Item> registry = event.getRegistry();
         ItemBlock[] var3 = items;
         int var4 = items.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ItemBlock item = var3[var5];
            Block block = item.getBlock();
            if (block instanceof ItemModelProvider) {
               ((ItemModelProvider)block).registerItemModel(item);
            }

            ResourceLocation registryName = (ResourceLocation)Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
            registry.register(item.setRegistryName(registryName));
            ITEM_BLOCKS.add(item);
         }

      }
   }
}
