package net.thecallunxz.left2mine.gui;

public class GuiHurtArrow {
   private double x;
   private double y;
   private double z;
   private int lifeLeft;

   public GuiHurtArrow(double x, double y, double z, int lifeLeft) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.lifeLeft = lifeLeft;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public int getLifeLeft() {
      return this.lifeLeft;
   }

   public void tick() {
      --this.lifeLeft;
   }
}
