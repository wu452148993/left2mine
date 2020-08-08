package net.thecallunxz.left2mine.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder("left2mine")
public final class InitSounds {
   public static final SoundEvent ak47_fire = register("ak47_fire");
   public static final SoundEvent ak47_equip = register("ak47_equip");
   public static final SoundEvent ak47_reload = register("ak47_reload");
   public static final SoundEvent m16_fire = register("m16_fire");
   public static final SoundEvent m16_equip = register("m16_equip");
   public static final SoundEvent m16_reload = register("m16_reload");
   public static final SoundEvent pumpshotgun_fire = register("pumpshotgun_fire");
   public static final SoundEvent pumpshotgun_equip = register("pumpshotgun_equip");
   public static final SoundEvent pumpshotgun_pump = register("pumpshotgun_pump");
   public static final SoundEvent pumpshotgun_reload = register("pumpshotgun_reload");
   public static final SoundEvent chromeshotgun_fire = register("chromeshotgun_fire");
   public static final SoundEvent combatshotgun_fire = register("combatshotgun_fire");
   public static final SoundEvent autoshotgun_fire = register("autoshotgun_fire");
   public static final SoundEvent autoshotgun_equip = register("autoshotgun_equip");
   public static final SoundEvent autoshotgun_pump = register("autoshotgun_pump");
   public static final SoundEvent smg_fire = register("smg_fire");
   public static final SoundEvent smg_equip = register("smg_equip");
   public static final SoundEvent smg_reload = register("smg_reload");
   public static final SoundEvent silenced_smg_fire = register("silenced_smg_fire");
   public static final SoundEvent silenced_smg_equip = register("silenced_smg_equip");
   public static final SoundEvent pistol_fire = register("pistol_fire");
   public static final SoundEvent pistol_equip = register("pistol_equip");
   public static final SoundEvent pistol_reload = register("pistol_reload");
   public static final SoundEvent beep = register("beep");
   public static final SoundEvent flashlight = register("flashlight");
   public static final SoundEvent molotov_detonate = register("molotov_detonate");
   public static final SoundEvent fire_loop = register("fire_loop");
   public static final SoundEvent bandaging = register("bandaging");
   public static final SoundEvent item_pickup = register("item_pickup");
   public static final SoundEvent misc_deploy = register("equipmisc");
   public static final SoundEvent pills_deploy = register("pills_deploy");
   public static final SoundEvent pills_use = register("pills_use");
   public static final SoundEvent common_ambient = register("common_ambient");
   public static final SoundEvent common_ambient_rage = register("common_ambient_rage");
   public static final SoundEvent common_death = register("common_death");
   public static final SoundEvent hunter_death = register("hunter_death");
   public static final SoundEvent hunter_pounce = register("hunter_pounce");
   public static final SoundEvent hunter_stalk = register("hunter_stalk");
   public static final SoundEvent hunter_shred = register("hunter_shred");
   public static final SoundEvent hunter_bacteria = register("hunter_bacteria");
   public static final SoundEvent hunter_music = register("hunter_music");
   public static final SoundEvent boomer_explode = register("boomer_explode");
   public static final SoundEvent boomer_idle = register("boomer_idle");
   public static final SoundEvent boomer_alert = register("boomer_alert");
   public static final SoundEvent boomer_vomit = register("boomer_vomit");
   public static final SoundEvent boomer_bacteria = register("boomer_bacteria");
   public static final SoundEvent boomer_music = register("boomer_music");
   public static final SoundEvent smoker_explode = register("smoker_explode");
   public static final SoundEvent smoker_death = register("smoker_death");
   public static final SoundEvent smoker_lurk = register("smoker_lurk");
   public static final SoundEvent smoker_spotprey = register("smoker_spotprey");
   public static final SoundEvent smoker_launchtongue = register("smoker_launchtongue");
   public static final SoundEvent smoker_warn = register("smoker_warn");
   public static final SoundEvent smoker_bacteria = register("smoker_bacteria");
   public static final SoundEvent smoker_music = register("smoker_music");
   public static final SoundEvent tank_angry = register("tank_angry");
   public static final SoundEvent tank_death = register("tank_death");
   public static final SoundEvent tank_hit = register("tank_hit");
   public static final SoundEvent tank_idle = register("tank_idle");
   public static final SoundEvent tank_step = register("tank_step");
   public static final SoundEvent tank_music = register("tank_music");
   public static final SoundEvent witch_angry = register("witch_angry");
   public static final SoundEvent witch_cry = register("witch_cry");
   public static final SoundEvent witch_cryangry = register("witch_cryangry");
   public static final SoundEvent witch_die = register("witch_die");
   public static final SoundEvent witch_flee = register("witch_flee");
   public static final SoundEvent witch_music1 = register("witch_music1");
   public static final SoundEvent witch_music2 = register("witch_music2");
   public static final SoundEvent horde_sound = register("horde_sound");
   public static final SoundEvent horde_germ = register("horde_germ");
   public static final SoundEvent death_music = register("death_music");
   public static final SoundEvent menu_music = register("menu_music");
   public static final SoundEvent game_lose1 = register("game_lose1");
   public static final SoundEvent game_lose2 = register("game_lose2");
   public static final SoundEvent game_win = register("game_win");
   public static final SoundEvent game_start = register("game_start");
   public static final SoundEvent saferoom = register("saferoom");
   public static final SoundEvent ricochet = register("ric");

   public static SoundEvent register(String name) {
      ResourceLocation loc = new ResourceLocation("left2mine", name);
      return (SoundEvent)(new SoundEvent(loc)).setRegistryName(loc);
   }

   @EventBusSubscriber(
      modid = "left2mine"
   )
   public static class RegistrationHandler {
      @SubscribeEvent
      public static void registerBlocks(Register<SoundEvent> event) {
         IForgeRegistry<SoundEvent> registry = event.getRegistry();
         SoundEvent[] sounds = new SoundEvent[]{InitSounds.ak47_fire, InitSounds.ak47_equip, InitSounds.ak47_reload, InitSounds.m16_fire, InitSounds.m16_equip, InitSounds.m16_reload, InitSounds.pumpshotgun_fire, InitSounds.pumpshotgun_equip, InitSounds.pumpshotgun_pump, InitSounds.pumpshotgun_reload, InitSounds.chromeshotgun_fire, InitSounds.combatshotgun_fire, InitSounds.autoshotgun_fire, InitSounds.autoshotgun_equip, InitSounds.autoshotgun_pump, InitSounds.smg_fire, InitSounds.smg_equip, InitSounds.smg_reload, InitSounds.silenced_smg_fire, InitSounds.silenced_smg_equip, InitSounds.pistol_fire, InitSounds.pistol_equip, InitSounds.pistol_reload, InitSounds.fire_loop, InitSounds.molotov_detonate, InitSounds.beep, InitSounds.flashlight, InitSounds.bandaging, InitSounds.item_pickup, InitSounds.misc_deploy, InitSounds.pills_deploy, InitSounds.pills_use, InitSounds.common_ambient, InitSounds.common_ambient_rage, InitSounds.common_death, InitSounds.hunter_death, InitSounds.hunter_pounce, InitSounds.hunter_stalk, InitSounds.hunter_shred, InitSounds.hunter_bacteria, InitSounds.hunter_music, InitSounds.boomer_explode, InitSounds.boomer_idle, InitSounds.boomer_alert, InitSounds.boomer_vomit, InitSounds.boomer_bacteria, InitSounds.boomer_music, InitSounds.smoker_explode, InitSounds.smoker_death, InitSounds.smoker_lurk, InitSounds.smoker_spotprey, InitSounds.smoker_warn, InitSounds.smoker_launchtongue, InitSounds.smoker_bacteria, InitSounds.smoker_music, InitSounds.tank_angry, InitSounds.tank_death, InitSounds.tank_hit, InitSounds.tank_idle, InitSounds.tank_step, InitSounds.tank_music, InitSounds.witch_angry, InitSounds.witch_cry, InitSounds.witch_cryangry, InitSounds.witch_die, InitSounds.witch_flee, InitSounds.witch_music1, InitSounds.witch_music2, InitSounds.horde_sound, InitSounds.horde_germ, InitSounds.death_music, InitSounds.menu_music, InitSounds.game_lose1, InitSounds.game_lose2, InitSounds.game_win, InitSounds.game_start, InitSounds.saferoom, InitSounds.ricochet};
         registry.registerAll(sounds);
      }
   }
}
