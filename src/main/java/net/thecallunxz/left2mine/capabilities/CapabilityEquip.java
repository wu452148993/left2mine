package net.thecallunxz.left2mine.capabilities;

import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CapabilityEquip implements IEquip {
   private ArrayList<ItemStack> equipped = new ArrayList();
   private boolean lying = false;
   private boolean pinned = false;
   private boolean puked = false;
   private boolean flashlight = false;
   private float animationAngle = 0.0F;
   private int lives = 3;
   private UUID tetheredUUID = UUID.randomUUID();

   public boolean getLying() {
      return this.lying;
   }

   public void setLying(boolean lying) {
      this.lying = lying;
   }

   public boolean getPinned() {
      return this.pinned;
   }

   public void setPinned(boolean pinned) {
      this.pinned = pinned;
   }

   public int getLives() {
      return this.lives;
   }

   public void setLives(int lives) {
      this.lives = lives;
   }

   public void addEquipped(ItemStack stack) {
      if (stack.getItem() != Items.AIR) {
         if (!this.equipped.contains(stack) && stack.getItem() != Items.AIR) {
            this.equipped.add(stack);
         }

      }
   }

   public void removeEquipped(ItemStack stack) {
      for(int i = 0; i < this.equipped.size(); ++i) {
         if (((ItemStack)this.equipped.get(i)).getItem() == stack.getItem() || ((ItemStack)this.equipped.get(i)).getItem() == Items.AIR) {
            this.equipped.remove(i);
         }
      }

   }

   public void clearEquipped() {
      this.equipped.clear();
   }

   public ArrayList<ItemStack> getEquippedList() {
      return this.equipped;
   }

   public void setEquipped(ArrayList<ItemStack> stack) {
      this.equipped = stack;
   }

   public void setAnimationAngle(float angle) {
      this.animationAngle = angle;
   }

   public float getAnimationAngle() {
      return this.animationAngle;
   }

   public void setTetheredUUID(UUID uuid) {
      this.tetheredUUID = uuid;
   }

   public UUID getTetheredUUID() {
      return this.tetheredUUID;
   }

   public void setPuked(boolean puked) {
      this.puked = puked;
   }

   public boolean isPuked() {
      return this.puked;
   }

   public void setFlashlight(boolean flashlight) {
      this.flashlight = flashlight;
   }

   public boolean isFlashlightOn() {
      return this.flashlight;
   }
}
