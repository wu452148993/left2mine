package net.thecallunxz.left2mine.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class NodeProcessorBlock {
   protected IBlockAccess blockaccess;
   protected final IntHashMap<PathPoint> pointMap = new IntHashMap();
   protected BlockPos pos;
   protected boolean canEnterDoors;
   protected boolean canOpenDoors;
   protected boolean canSwim;

   public void initProcessor(IBlockAccess sourceIn, BlockPos pos) {
      this.blockaccess = sourceIn;
      this.pos = pos;
      this.pointMap.clearMap();
   }

   public void postProcess() {
      this.blockaccess = null;
   }

   protected PathPoint openPoint(int x, int y, int z) {
      int i = PathPoint.makeHash(x, y, z);
      PathPoint pathpoint = (PathPoint)this.pointMap.lookup(i);
      if (pathpoint == null) {
         pathpoint = new PathPoint(x, y, z);
         this.pointMap.addKey(i, pathpoint);
      }

      return pathpoint;
   }

   public abstract PathPoint getStart();

   public abstract PathPoint getPathPointToCoords(double var1, double var3, double var5);

   public abstract int findPathOptions(PathPoint[] var1, PathPoint var2, PathPoint var3, float var4);

   public abstract PathNodeType getPathNodeType(IBlockAccess var1, int var2, int var3, int var4, EntityLiving var5, int var6, int var7, int var8, boolean var9, boolean var10);

   public abstract PathNodeType getPathNodeType(IBlockAccess var1, int var2, int var3, int var4);

   public void setCanEnterDoors(boolean canEnterDoorsIn) {
      this.canEnterDoors = canEnterDoorsIn;
   }

   public void setCanOpenDoors(boolean canOpenDoorsIn) {
      this.canOpenDoors = canOpenDoorsIn;
   }

   public void setCanSwim(boolean canSwimIn) {
      this.canSwim = canSwimIn;
   }

   public boolean getCanEnterDoors() {
      return this.canEnterDoors;
   }

   public boolean getCanOpenDoors() {
      return this.canOpenDoors;
   }

   public boolean getCanSwim() {
      return this.canSwim;
   }
}
