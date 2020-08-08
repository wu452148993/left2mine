package net.thecallunxz.left2mine.capabilities;

public interface IStats {
   int getDeaths();

   int getIncapacitations();

   int getCommonKilled();

   int getHuntersKilled();

   int getBoomersKilled();

   int getSmokersKilled();

   int getTankDamage();

   int getWitchDamage();

   int getPillsUsed();

   int getMedpacksUsed();

   int getPipebombsUsed();

   int getMolotovsUsed();

   void setDeaths(int var1);

   void setIncapacitations(int var1);

   void setCommonKilled(int var1);

   void setHuntersKilled(int var1);

   void setBoomersKilled(int var1);

   void setSmokersKilled(int var1);

   void setTankDamage(int var1);

   void setWitchDamage(int var1);

   void setPillsUsed(int var1);

   void setMedpacksUsed(int var1);

   void setPipebombsUsed(int var1);

   void setMolotovsUsed(int var1);

   void setStatFromEnum(StatsEnum var1, int var2);

   void clearStats();
}
