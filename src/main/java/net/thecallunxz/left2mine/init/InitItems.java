package net.thecallunxz.left2mine.init;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import net.thecallunxz.left2mine.ItemModelProvider;
import net.thecallunxz.left2mine.items.ItemBase;
import net.thecallunxz.left2mine.items.usable.ItemFakeWall;
import net.thecallunxz.left2mine.items.usable.ItemFirstAid;
import net.thecallunxz.left2mine.items.usable.ItemGameDoor;
import net.thecallunxz.left2mine.items.usable.ItemNodeLinker;
import net.thecallunxz.left2mine.items.usable.ItemPills;
import net.thecallunxz.left2mine.items.usable.ItemSaferoomDoor;
import net.thecallunxz.left2mine.items.weapons.ItemMolotov;
import net.thecallunxz.left2mine.items.weapons.ItemPipebomb;
import net.thecallunxz.left2mine.items.weapons.guns.ItemAK47;
import net.thecallunxz.left2mine.items.weapons.guns.ItemAutoShotgun;
import net.thecallunxz.left2mine.items.weapons.guns.ItemChromeShotgun;
import net.thecallunxz.left2mine.items.weapons.guns.ItemCombatShotgun;
import net.thecallunxz.left2mine.items.weapons.guns.ItemM16;
import net.thecallunxz.left2mine.items.weapons.guns.ItemPistol;
import net.thecallunxz.left2mine.items.weapons.guns.ItemPumpShotgun;
import net.thecallunxz.left2mine.items.weapons.guns.ItemSilencedSubmachineGun;
import net.thecallunxz.left2mine.items.weapons.guns.ItemSubmachineGun;

@ObjectHolder("left2mine")
public class InitItems {
   public static final ItemNodeLinker nodelinker;
   public static final ItemPills pills;
   public static final ItemFirstAid firstaid;
   public static final ItemPipebomb pipebomb;
   public static final ItemMolotov molotov;
   public static final ItemPistol pistol;
   public static final ItemAK47 ak47;
   public static final ItemM16 m16rifle;
   public static final ItemSubmachineGun submachinegun;
   public static final ItemSilencedSubmachineGun silencedsubmachinegun;
   public static final ItemPumpShotgun pumpshotgun;
   public static final ItemChromeShotgun chromeshotgun;
   public static final ItemCombatShotgun combatshotgun;
   public static final ItemAutoShotgun autoshotgun;
   public static final ItemSaferoomDoor saferoomdooritem;
   public static final ItemGameDoor gamedoor1item;
   public static final ItemGameDoor gamedoorsaferoomitem;
   public static final ItemFakeWall fakewallitem;
   public static final ItemBase flash;
   public static final ItemBase flashlight;

   static {
      nodelinker = new ItemNodeLinker("nodelinker", ItemBase.EnumItemType.MISC);
      pills = new ItemPills("pills", ItemBase.EnumItemType.SECONDARY_HEALING);
      firstaid = new ItemFirstAid("firstaid", ItemBase.EnumItemType.PRIMARY_HEALING);
      pipebomb = new ItemPipebomb("pipebomb", ItemBase.EnumItemType.GRENADE);
      molotov = new ItemMolotov("molotov", ItemBase.EnumItemType.GRENADE);
      pistol = new ItemPistol("pistol", ItemBase.EnumItemType.SECONDARY_WEAPON);
      ak47 = new ItemAK47("ak47", ItemBase.EnumItemType.PRIMARY_WEAPON);
      m16rifle = new ItemM16("m16rifle", ItemBase.EnumItemType.PRIMARY_WEAPON);
      submachinegun = new ItemSubmachineGun("submachinegun", ItemBase.EnumItemType.PRIMARY_WEAPON);
      silencedsubmachinegun = new ItemSilencedSubmachineGun("silencedsubmachinegun", ItemBase.EnumItemType.PRIMARY_WEAPON);
      pumpshotgun = new ItemPumpShotgun("pumpshotgun", ItemBase.EnumItemType.PRIMARY_WEAPON);
      chromeshotgun = new ItemChromeShotgun("chromeshotgun", ItemBase.EnumItemType.PRIMARY_WEAPON);
      combatshotgun = new ItemCombatShotgun("combatshotgun", ItemBase.EnumItemType.PRIMARY_WEAPON);
      autoshotgun = new ItemAutoShotgun("autoshotgun", ItemBase.EnumItemType.PRIMARY_WEAPON);
      saferoomdooritem = new ItemSaferoomDoor("saferoomdooritem", ItemBase.EnumItemType.MISC);
      gamedoor1item = new ItemGameDoor("gamedoor1item", ItemBase.EnumItemType.MISC, InitBlocks.gamedoor1);
      gamedoorsaferoomitem = new ItemGameDoor("gamedoorsaferoomitem", ItemBase.EnumItemType.MISC, InitBlocks.gamedoorsaferoom);
      fakewallitem = new ItemFakeWall("fakewallitem", ItemBase.EnumItemType.MISC, InitBlocks.fakewall);
      flash = new ItemBase("flash", ItemBase.EnumItemType.MISC) {
         public ItemBase setCreativeTab(CreativeTabs tab) {
            return this;
         }
      };
      flashlight = new ItemBase("flashlight", ItemBase.EnumItemType.MISC) {
         public ItemBase setCreativeTab(CreativeTabs tab) {
            return this;
         }
      };
   }

   @EventBusSubscriber(
      modid = "left2mine"
   )
   public static class RegistrationHandler {
      public static final Set<Item> ITEMS = new HashSet();

      @SubscribeEvent
      public static void registerItems(Register<Item> event) {
         Item[] items = new Item[]{InitItems.nodelinker, InitItems.saferoomdooritem, InitItems.gamedoorsaferoomitem, InitItems.gamedoor1item, InitItems.fakewallitem, InitItems.pills, InitItems.firstaid, InitItems.pipebomb, InitItems.molotov, InitItems.pistol, InitItems.pumpshotgun, InitItems.chromeshotgun, InitItems.submachinegun, InitItems.silencedsubmachinegun, InitItems.autoshotgun, InitItems.combatshotgun, InitItems.m16rifle, InitItems.ak47, InitItems.flash, InitItems.flashlight};
         IForgeRegistry<Item> registry = event.getRegistry();
         Item[] var3 = items;
         int var4 = items.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Item item = var3[var5];
            if (item instanceof ItemModelProvider) {
               ((ItemModelProvider)item).registerItemModel(item);
            }

            registry.register(item);
            ITEMS.add(item);
         }

      }
   }
}
