package net.thecallunxz.left2mine;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.thecallunxz.left2mine.init.InitBlocks;
import net.thecallunxz.left2mine.init.InitCommands;
import net.thecallunxz.left2mine.init.InitItems;
import net.thecallunxz.left2mine.proxy.CommonProxy;

@Mod(
   modid = "left2mine",
   name = "Left 2 Mine",
   version = "v1.0.4",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class Main {
   @Instance("left2mine")
   public static Main instance;
   public static int distance = 1;
   public static final Material SURFACE;
   public static final DamageSource DOWN;
   public static final DamageSource HUNTER;
   public static final DamageSource SMOKER;
   public static List<String> deadPlayerList;
   public static long gameStartTime;
   public static long bestSurvivalTime;
   public static long lastSurvivalTime;
   public static int difficulty;
   public static int timesRestarted;
   public static boolean inSurvivalGame;
   public static boolean survivalStarted;
   public static boolean flashlight;
   public static final ResourceLocation tongueParticle;
   public static final ResourceLocation tickParticle;
   public static final ResourceLocation crossParticle;
   public static final CreativeTabs tabL2M;
   public static final CreativeTabs tabL2M2;
   public static final CreativeTabs tabL2M3;
   @SidedProxy(
      clientSide = "net.thecallunxz.left2mine.proxy.ClientProxy",
      serverSide = "net.thecallunxz.left2mine.proxy.ServerProxy"
   )
   public static CommonProxy proxy;

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      InitCommands.registerCommands(event);
   }

   static {
      SURFACE = new Material(MapColor.WOOD) {
         public boolean blocksMovement() {
            return false;
         }

         public boolean isOpaque() {
            return false;
         }

         public boolean blocksLight() {
            return false;
         }
      };
      DOWN = new DamageSource("down");
      HUNTER = new DamageSource("hunter");
      SMOKER = new DamageSource("smoker");
      deadPlayerList = new ArrayList();
      gameStartTime = 0L;
      bestSurvivalTime = 0L;
      lastSurvivalTime = 0L;
      difficulty = 0;
      timesRestarted = 0;
      inSurvivalGame = false;
      survivalStarted = false;
      flashlight = false;
      tongueParticle = new ResourceLocation("left2mine:particle/tongue");
      tickParticle = new ResourceLocation("left2mine:particle/tick");
      crossParticle = new ResourceLocation("left2mine:particle/cross");
      tabL2M = new CreativeTabs("Left2MineTools") {
         public ItemStack getTabIconItem() {
            return InitItems.nodelinker.getDefaultInstance();
         }
      };
      tabL2M2 = new CreativeTabs("Left2MineItems") {
         public ItemStack getTabIconItem() {
            return InitItems.firstaid.getDefaultInstance();
         }
      };
      tabL2M3 = new CreativeTabs("Left2MineBlocks") {
         public ItemStack getTabIconItem() {
            return Item.getItemFromBlock(InitBlocks.light2).getDefaultInstance();
         }
      };
   }
}
