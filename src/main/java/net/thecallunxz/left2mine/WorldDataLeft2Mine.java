package net.thecallunxz.left2mine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.thecallunxz.left2mine.init.InitBlocks;
import net.thecallunxz.left2mine.util.NodeSorter;

public class WorldDataLeft2Mine extends WorldSavedData {
   private static final String IDENTIFIER = "thecallunxzleft2mine";
   private boolean inGame = false;
   private boolean survivalInGame = false;
   private boolean readyToPanic = false;
   private boolean readyToSpecial = false;
   private boolean readyToBoss = false;
   private boolean inPanicEvent = false;
   private boolean survivalStarted = false;
   private boolean directorEnabled = true;
   private ArrayList<BlockPos> activeList = new ArrayList();
   private ArrayList<BlockPos> hordeList = new ArrayList();
   private ArrayList<BlockPos> bossList = new ArrayList();
   private ArrayList<String> lurkingSpecialList = new ArrayList();
   private long lastPanicTime = 0L;
   private long lastSpecialTime = 0L;
   private long lastBossTime = 0L;
   private long gameStartTime = 0L;
   private long bestSurvivalTime = 0L;
   private int difficulty = 0;
   private int timesRestarted = 0;
   private int gameSeed = -1;
   private int spawnX = 0;
   private int spawnY = 0;
   private int spawnZ = 0;
   private int survivalspawnX = 0;
   private int survivalspawnY = 0;
   private int survivalspawnZ = 0;
   private int lastspawnX = 0;
   private int lastspawnY = 0;
   private int lastspawnZ = 0;
   private int respawnX = 0;
   private int respawnY = 0;
   private int respawnZ = 0;
   private int wanderingCount = 0;
   private int lurkingCount = 0;
   private int hordeCount = 0;
   private int specialCount = 0;
   private int bossCount = 0;
   private long pauseTime = -1L;

   public WorldDataLeft2Mine() {
      super("thecallunxzleft2mine");
   }

   public WorldDataLeft2Mine(String identifier) {
      super(identifier);
   }

   public static WorldDataLeft2Mine get(World world) {
      WorldDataLeft2Mine data = (WorldDataLeft2Mine)world.loadData(WorldDataLeft2Mine.class, "thecallunxzleft2mine");
      if (data == null) {
         data = new WorldDataLeft2Mine();
         world.setData("thecallunxzleft2mine", data);
      }

      return data;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.inGame = nbt.getBoolean("inGame");
      this.survivalInGame = nbt.getBoolean("survivalInGame");
      this.readyToPanic = nbt.getBoolean("readyToPanic");
      this.readyToSpecial = nbt.getBoolean("readyToSpecial");
      this.readyToBoss = nbt.getBoolean("readyToBoss");
      this.inPanicEvent = nbt.getBoolean("inPanicEvent");
      this.survivalStarted = nbt.getBoolean("survivalStarted");
      this.gameSeed = nbt.getInteger("gameSeed");
      this.spawnX = nbt.getInteger("spawnX");
      this.spawnY = nbt.getInteger("spawnY");
      this.spawnZ = nbt.getInteger("spawnZ");
      this.survivalspawnX = nbt.getInteger("survivalspawnX");
      this.survivalspawnY = nbt.getInteger("survivalspawnY");
      this.survivalspawnZ = nbt.getInteger("survivalspawnZ");
      this.lastspawnX = nbt.getInteger("lastspawnX");
      this.lastspawnY = nbt.getInteger("lastspawnY");
      this.lastspawnZ = nbt.getInteger("lastspawnZ");
      this.respawnX = nbt.getInteger("respawnX");
      this.respawnY = nbt.getInteger("respawnY");
      this.respawnZ = nbt.getInteger("respawnZ");
      this.wanderingCount = nbt.getInteger("wanderingCount");
      this.lurkingCount = nbt.getInteger("lurkingCount");
      this.hordeCount = nbt.getInteger("hordeCount");
      this.specialCount = nbt.getInteger("specialCount");
      this.bossCount = nbt.getInteger("bossCount");
      this.difficulty = nbt.getInteger("difficulty");
      this.timesRestarted = nbt.getInteger("timesRestarted");
      this.lastPanicTime = nbt.getLong("lastPanicTime");
      this.lastSpecialTime = nbt.getLong("lastSpecialTime");
      this.lastBossTime = nbt.getLong("lastBossTime");
      this.pauseTime = nbt.getLong("pauseTime");
      this.gameStartTime = nbt.getLong("gameStartTime");
      this.bestSurvivalTime = nbt.getLong("bestSurvivalTime");
      this.directorEnabled = nbt.getBoolean("directorEnabled");
      NBTTagList tagList = (NBTTagList)nbt.getTag("activeList");
      this.activeList.clear();

      for(int i = 0; i < tagList.tagCount(); ++i) {
         NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
         this.activeList.add(NBTUtil.getPosFromTag(nbttagcompound));
      }

      NBTTagList tagList2 = (NBTTagList)nbt.getTag("hordeList");
      this.hordeList.clear();

      for(int i = 0; i < tagList2.tagCount(); ++i) {
         NBTTagCompound nbttagcompound = tagList2.getCompoundTagAt(i);
         this.hordeList.add(NBTUtil.getPosFromTag(nbttagcompound));
      }

      NBTTagList tagList3 = (NBTTagList)nbt.getTag("lurkingSpecialList");
      this.lurkingSpecialList.clear();

      for(int i = 0; i < tagList3.tagCount(); ++i) {
         NBTTagCompound tag = tagList3.getCompoundTagAt(i);
         this.lurkingSpecialList.add(tag.getString("name"));
      }

      NBTTagList tagList4 = (NBTTagList)nbt.getTag("bossList");
      this.bossList.clear();

      for(int i = 0; i < tagList4.tagCount(); ++i) {
         NBTTagCompound nbttagcompound = tagList4.getCompoundTagAt(i);
         this.bossList.add(NBTUtil.getPosFromTag(nbttagcompound));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setBoolean("inGame", this.inGame);
      nbt.setBoolean("survivalInGame", this.survivalInGame);
      nbt.setBoolean("readyToPanic", this.readyToPanic);
      nbt.setBoolean("readyToSpecial", this.readyToSpecial);
      nbt.setBoolean("readyToBoss", this.readyToBoss);
      nbt.setBoolean("inPanicEvent", this.inPanicEvent);
      nbt.setBoolean("survivalStarted", this.survivalStarted);
      nbt.setInteger("gameSeed", this.gameSeed);
      nbt.setInteger("spawnX", this.spawnX);
      nbt.setInteger("spawnY", this.spawnY);
      nbt.setInteger("spawnZ", this.spawnZ);
      nbt.setInteger("survivalspawnX", this.survivalspawnX);
      nbt.setInteger("survivalspawnY", this.survivalspawnY);
      nbt.setInteger("survivalspawnZ", this.survivalspawnZ);
      nbt.setInteger("lastspawnX", this.lastspawnX);
      nbt.setInteger("lastspawnY", this.lastspawnY);
      nbt.setInteger("lastspawnZ", this.lastspawnZ);
      nbt.setInteger("respawnX", this.respawnX);
      nbt.setInteger("respawnY", this.respawnY);
      nbt.setInteger("respawnZ", this.respawnZ);
      nbt.setInteger("wanderingCount", this.wanderingCount);
      nbt.setInteger("lurkingCount", this.lurkingCount);
      nbt.setInteger("hordeCount", this.hordeCount);
      nbt.setInteger("specialCount", this.specialCount);
      nbt.setInteger("bossCount", this.bossCount);
      nbt.setInteger("difficulty", this.difficulty);
      nbt.setInteger("timesRestarted", this.timesRestarted);
      nbt.setLong("lastPanicTime", this.lastPanicTime);
      nbt.setLong("lastSpecialTime", this.lastSpecialTime);
      nbt.setLong("lastBossTime", this.lastBossTime);
      nbt.setLong("pauseTime", this.pauseTime);
      nbt.setLong("gameStartTime", this.gameStartTime);
      nbt.setLong("bestSurvivalTime", this.bestSurvivalTime);
      nbt.setBoolean("directorEnabled", this.directorEnabled);
      NBTTagList tagList = new NBTTagList();

      for(int i = 0; i < this.activeList.size(); ++i) {
         BlockPos nodePos = (BlockPos)this.activeList.get(i);
         NBTTagCompound nbttagcompound = NBTUtil.createPosTag(nodePos);
         tagList.appendTag(nbttagcompound);
      }

      nbt.setTag("activeList", tagList);
      NBTTagList tagList2 = new NBTTagList();

      NBTTagCompound tag;
      for(int i = 0; i < this.hordeList.size(); ++i) {
         BlockPos nodePos = (BlockPos)this.hordeList.get(i);
         tag = NBTUtil.createPosTag(nodePos);
         tagList2.appendTag(tag);
      }

      nbt.setTag("hordeList", tagList2);
      NBTTagList tagList3 = new NBTTagList();

      for(int i = 0; i < this.lurkingSpecialList.size(); ++i) {
         tag = new NBTTagCompound();
         String lurker = (String)this.lurkingSpecialList.get(i);
         tag.setString("name", lurker);
         tagList3.appendTag(tag);
      }

      nbt.setTag("lurkingSpecialList", tagList3);
      NBTTagList tagList4 = new NBTTagList();

      for(int i = 0; i < this.bossList.size(); ++i) {
         BlockPos nodePos = (BlockPos)this.bossList.get(i);
         NBTTagCompound nbttagcompound = NBTUtil.createPosTag(nodePos);
         tagList4.appendTag(nbttagcompound);
      }

      nbt.setTag("bossList", tagList4);
      return nbt;
   }

   public long getLastPanicTime() {
      return this.lastPanicTime;
   }

   public void setLastPanicTime(long lastPanicTime) {
      this.lastPanicTime = lastPanicTime;
      this.markDirty();
   }

   public long getLastSpecialTime() {
      return this.lastSpecialTime;
   }

   public void setLastSpecialTime(long lastSpecialTime) {
      this.lastSpecialTime = lastSpecialTime;
      this.markDirty();
   }

   public long getLastBossTime() {
      return this.lastBossTime;
   }

   public void setLastBossTime(long lastBossTime) {
      this.lastBossTime = lastBossTime;
      this.markDirty();
   }

   public long getPauseTime() {
      return this.pauseTime;
   }

   public void setPauseTime(long pauseTime) {
      this.pauseTime = pauseTime;
      this.markDirty();
   }

   public long getGameStartTime() {
      return this.gameStartTime;
   }

   public void setGameStartTime(long gameStartTime) {
      this.gameStartTime = gameStartTime;
      this.markDirty();
   }

   public long getBestSurvivalTime() {
      return this.bestSurvivalTime;
   }

   public void setBestSurvivalTime(long time) {
      this.bestSurvivalTime = time;
      this.markDirty();
   }

   public void randomizeGameSeed() {
      this.gameSeed = (new Random()).nextInt(2147483646) + 1;
      this.markDirty();
   }

   public int getGameSeed() {
      return this.gameSeed;
   }

   public boolean getReadyToSpecial() {
      return this.readyToSpecial;
   }

   public void setReadyToSpecial(boolean bool) {
      this.readyToSpecial = bool;
      this.markDirty();
   }

   public boolean getReadyToBoss() {
      return this.readyToBoss;
   }

   public void setReadyToBoss(boolean bool) {
      this.readyToBoss = bool;
      this.markDirty();
   }

   public boolean getDirectorEnabled() {
      return this.directorEnabled;
   }

   public void setDirectorEnabled(boolean bool) {
      this.directorEnabled = bool;
      if (bool) {
         this.pauseTime = -1L;
      }

      this.markDirty();
   }

   public int getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(int difficulty) {
      this.difficulty = difficulty;
      this.markDirty();
   }

   public int getTimesRestarted() {
      return this.timesRestarted;
   }

   public void setTimesRestarted(int timesRestarted) {
      this.timesRestarted = timesRestarted;
      this.markDirty();
   }

   public void addBossNode(BlockPos pos) {
      this.bossList.add(pos);
      this.markDirty();
   }

   public void removeBossNode(BlockPos pos) {
      this.bossList.remove(pos);
      this.markDirty();
   }

   public void clearBossNodes() {
      this.bossList.clear();
      this.markDirty();
   }

   public ArrayList<BlockPos> getBossNodes() {
      return this.bossList;
   }

   public void addHordeNode(BlockPos pos) {
      this.hordeList.add(pos);
      this.markDirty();
   }

   public void removeHordeNode(BlockPos pos) {
      this.hordeList.remove(pos);
      this.markDirty();
   }

   public void clearHordeNodes() {
      this.hordeList.clear();
      this.markDirty();
   }

   public ArrayList<BlockPos> getHordeNodes() {
      return this.hordeList;
   }

   public void addActiveNode(BlockPos pos) {
      this.activeList.add(pos);
      this.markDirty();
   }

   public void removeActiveNode(BlockPos pos) {
      this.activeList.remove(pos);
      this.markDirty();
   }

   public void removeActiveNode(int numb) {
      this.activeList.remove(numb);
      this.markDirty();
   }

   public void clearActiveNodes() {
      this.activeList.clear();
      this.markDirty();
   }

   public ArrayList<BlockPos> getActiveNodes() {
      return this.activeList;
   }

   public void sortActiveNodes(BlockPos startNode) {
      Collections.sort(this.activeList, new NodeSorter(startNode));
      this.markDirty();
   }

   public ArrayList<String> getLurkingSpecialList() {
      return this.lurkingSpecialList;
   }

   public void removeLurkingSpecialID(int id) {
      this.lurkingSpecialList.remove(id);
      this.markDirty();
   }

   public void addLurkingSpecial(String lurkerName) {
      this.lurkingSpecialList.add(lurkerName);
      this.markDirty();
   }

   public void clearLurkerSpecial() {
      this.lurkingSpecialList.clear();
      this.markDirty();
   }

   public boolean isReadyToPanic() {
      return this.readyToPanic;
   }

   public void setReadyToPanic(boolean readyToPanic) {
      this.readyToPanic = readyToPanic;
      this.markDirty();
   }

   public boolean isInPanicEvent() {
      return this.inPanicEvent;
   }

   public void setInPanicEvent(boolean inPanicEvent) {
      this.inPanicEvent = inPanicEvent;
      this.markDirty();
   }

   public boolean isInGame() {
      return this.inGame;
   }

   public void setInGame(boolean inGame) {
      this.inGame = inGame;
      this.markDirty();
   }

   public boolean hasSurvivalStarted() {
      return this.survivalStarted;
   }

   public void setSurvivalStarted(boolean survivalStarted) {
      this.survivalStarted = survivalStarted;
      this.markDirty();
   }

   public boolean isSurvivalInGame() {
      return this.survivalInGame;
   }

   public void setSurvivalInGame(boolean inGame) {
      this.survivalInGame = inGame;
      this.markDirty();
   }

   public void setWanderingCount(int num) {
      this.wanderingCount = Math.max(0, num);
      this.markDirty();
   }

   public int getWanderingCount() {
      return this.wanderingCount;
   }

   public void setHordeCount(int num) {
      this.hordeCount = Math.max(0, num);
      this.markDirty();
   }

   public int getHordeCount() {
      return this.hordeCount;
   }

   public void setSpecialCount(int num) {
      this.specialCount = Math.max(0, num);
      this.markDirty();
   }

   public int getSpecialCount() {
      return this.specialCount;
   }

   public void setBossCount(int num) {
      this.bossCount = Math.max(0, num);
      this.markDirty();
   }

   public int getBossCount() {
      return this.bossCount;
   }

   public void setLurkingCount(int num) {
      this.lurkingCount = Math.max(0, num);
      this.markDirty();
   }

   public int getLurkingCount() {
      return this.lurkingCount;
   }

   public void resetInfectedCount() {
      this.wanderingCount = 0;
      this.hordeCount = 0;
      this.specialCount = 0;
      this.bossCount = 0;
      this.lurkingCount = 0;
      this.clearLurkerSpecial();
      this.markDirty();
   }

   public boolean isSpawnSet(World world) {
      return world.getBlockState(this.getSpawnPos()) == InitBlocks.playerspawn.getDefaultState();
   }

   public boolean isSurvivalSpawnSet(World world) {
      return world.getBlockState(this.getSurvivalSpawnPos()) == InitBlocks.survivalspawn.getDefaultState();
   }

   public BlockPos getSpawnPos() {
      return new BlockPos(this.spawnX, this.spawnY, this.spawnZ);
   }

   public void setSpawnPos(BlockPos pos) {
      this.spawnX = pos.getX();
      this.spawnY = pos.getY();
      this.spawnZ = pos.getZ();
      this.markDirty();
   }

   public BlockPos getSurvivalSpawnPos() {
      return new BlockPos(this.survivalspawnX, this.survivalspawnY, this.survivalspawnZ);
   }

   public void setSurvivalSpawnPos(BlockPos pos) {
      this.survivalspawnX = pos.getX();
      this.survivalspawnY = pos.getY();
      this.survivalspawnZ = pos.getZ();
      this.markDirty();
   }

   public BlockPos getRespawnPos() {
      return new BlockPos(this.respawnX, this.respawnY, this.respawnZ);
   }

   public void setRespawnPos(BlockPos pos) {
      this.respawnX = pos.getX();
      this.respawnY = pos.getY();
      this.respawnZ = pos.getZ();
      this.markDirty();
   }

   public BlockPos getLastSpawnPos() {
      return new BlockPos(this.lastspawnX, this.lastspawnY, this.lastspawnZ);
   }

   public void setLastSpawnPos(BlockPos pos) {
      this.lastspawnX = pos.getX();
      this.lastspawnY = pos.getY();
      this.lastspawnZ = pos.getZ();
      this.markDirty();
   }
}
