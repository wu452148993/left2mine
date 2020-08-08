package net.thecallunxz.left2mine.gui;

public class GuiTipHandler {
   private float x;
   private float y;
   private float z;
   private String text;
   private int lifeLeft;

   public GuiTipHandler(float x, float y, float z, int lifeLeft, String text) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.lifeLeft = lifeLeft;
      this.text = text;
   }

   public String getText() {
      return this.text;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }

   public int getLifeLeft() {
      return this.lifeLeft;
   }

   public void tick() {
      if (this.lifeLeft > 0) {
         --this.lifeLeft;
      }
   }
}
