package net.thecallunxz.left2mine.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.thecallunxz.left2mine.networking.Left2MinePacket;
import net.thecallunxz.left2mine.networking.client.StatsMessage;

public class CapabilityStats implements IStats {
   private int commonKilled = 0;
   private int huntersKilled = 0;
   private int boomersKilled = 0;
   private int smokersKilled = 0;
   private int tankDamage = 0;
   private int witchDamage = 0;
   private int deaths = 0;
   private int incapacitations = 0;
   private int pillsUsed = 0;
   private int medpacksUsed = 0;
   private int pipebombsUsed = 0;
   private int molotovsUsed = 0;

   public int getDeaths() {
      return this.deaths;
   }

   public int getIncapacitations() {
      return this.incapacitations;
   }

   public int getCommonKilled() {
      return this.commonKilled;
   }

   public int getHuntersKilled() {
      return this.huntersKilled;
   }

   public int getBoomersKilled() {
      return this.boomersKilled;
   }

   public int getSmokersKilled() {
      return this.smokersKilled;
   }

   public int getTankDamage() {
      return this.tankDamage;
   }

   public int getWitchDamage() {
      return this.witchDamage;
   }

   public int getPillsUsed() {
      return this.pillsUsed;
   }

   public int getMedpacksUsed() {
      return this.medpacksUsed;
   }

   public int getPipebombsUsed() {
      return this.pipebombsUsed;
   }

   public int getMolotovsUsed() {
      return this.molotovsUsed;
   }

   public void setDeaths(int amount) {
      this.deaths = amount;
   }

   public void setIncapacitations(int amount) {
      this.incapacitations = amount;
   }

   public void setCommonKilled(int amount) {
      this.commonKilled = amount;
   }

   public void setHuntersKilled(int amount) {
      this.huntersKilled = amount;
   }

   public void setBoomersKilled(int amount) {
      this.boomersKilled = amount;
   }

   public void setSmokersKilled(int amount) {
      this.smokersKilled = amount;
   }

   public void setTankDamage(int amount) {
      this.tankDamage = amount;
   }

   public void setWitchDamage(int amount) {
      this.witchDamage = amount;
   }

   public void setPillsUsed(int amount) {
      this.pillsUsed = amount;
   }

   public void setMedpacksUsed(int amount) {
      this.medpacksUsed = amount;
   }

   public void setPipebombsUsed(int amount) {
      this.pipebombsUsed = amount;
   }

   public void setMolotovsUsed(int amount) {
      this.molotovsUsed = amount;
   }

   public void setStatFromEnum(StatsEnum stat, int amount) {
      switch(stat) {
      case CLEARALL:
         this.clearStats();
         break;
      case COMMONKILLED:
         this.setCommonKilled(amount);
         break;
      case HUNTERSKILLED:
         this.setHuntersKilled(amount);
         break;
      case BOOMERSKILLED:
         this.setBoomersKilled(amount);
         break;
      case SMOKERSKILLED:
         this.setSmokersKilled(amount);
         break;
      case TANKDAMAGE:
         this.setTankDamage(amount);
         break;
      case WITCHDAMAGE:
         this.setWitchDamage(amount);
         break;
      case PILLSUSED:
         this.setPillsUsed(amount);
         break;
      case MEDPACKSUSED:
         this.setMedpacksUsed(amount);
         break;
      case PIPEBOMBSUSED:
         this.setPipebombsUsed(amount);
         break;
      case MOLOTOVSUSED:
         this.setMolotovsUsed(amount);
         break;
      case DEATHS:
         this.setDeaths(amount);
         break;
      case INCAPACITATIONS:
         this.setIncapacitations(amount);
      }

   }

   public void clearStats() {
      this.commonKilled = 0;
      this.huntersKilled = 0;
      this.boomersKilled = 0;
      this.smokersKilled = 0;
      this.tankDamage = 0;
      this.witchDamage = 0;
      this.deaths = 0;
      this.incapacitations = 0;
      this.pillsUsed = 0;
      this.medpacksUsed = 0;
      this.pipebombsUsed = 0;
      this.molotovsUsed = 0;
   }

   public static void updateStats(IStats stats, EntityPlayer player) {
      Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.COMMONKILLED, stats.getCommonKilled(), player));
      Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.HUNTERSKILLED, stats.getHuntersKilled(), player));
      Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.BOOMERSKILLED, stats.getBoomersKilled(), player));
      Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.SMOKERSKILLED, stats.getSmokersKilled(), player));
      Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.TANKDAMAGE, stats.getTankDamage(), player));
      Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.WITCHDAMAGE, stats.getWitchDamage(), player));
      Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.DEATHS, stats.getDeaths(), player));
      Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.INCAPACITATIONS, stats.getIncapacitations(), player));
      Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.PILLSUSED, stats.getPillsUsed(), player));
      Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.MEDPACKSUSED, stats.getMedpacksUsed(), player));
      Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.PIPEBOMBSUSED, stats.getPipebombsUsed(), player));
      Left2MinePacket.INSTANCE.sendToAll(new StatsMessage(StatsEnum.MOLOTOVSUSED, stats.getMolotovsUsed(), player));
   }

   public static void syncStats(IStats statsOrig, IStats statsNew, EntityPlayer player) {
      statsNew.setCommonKilled(statsOrig.getCommonKilled());
      statsNew.setHuntersKilled(statsOrig.getHuntersKilled());
      statsNew.setBoomersKilled(statsOrig.getBoomersKilled());
      statsNew.setSmokersKilled(statsOrig.getSmokersKilled());
      statsNew.setTankDamage(statsOrig.getTankDamage());
      statsNew.setWitchDamage(statsOrig.getWitchDamage());
      statsNew.setDeaths(statsOrig.getDeaths());
      statsNew.setIncapacitations(statsOrig.getIncapacitations());
      statsNew.setPillsUsed(statsOrig.getPillsUsed());
      statsNew.setMedpacksUsed(statsOrig.getMedpacksUsed());
      statsNew.setPipebombsUsed(statsOrig.getPipebombsUsed());
      statsNew.setMolotovsUsed(statsOrig.getMolotovsUsed());
   }
}
