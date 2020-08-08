package net.thecallunxz.left2mine.capabilities;

import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.item.ItemStack;

public interface IEquip {
   void addEquipped(ItemStack var1);

   void removeEquipped(ItemStack var1);

   void setEquipped(ArrayList<ItemStack> var1);

   void clearEquipped();

   ArrayList<ItemStack> getEquippedList();

   void setLying(boolean var1);

   boolean getLying();

   void setPinned(boolean var1);

   boolean getPinned();

   void setAnimationAngle(float var1);

   float getAnimationAngle();

   void setTetheredUUID(UUID var1);

   UUID getTetheredUUID();

   void setLives(int var1);

   int getLives();

   void setPuked(boolean var1);

   boolean isPuked();

   void setFlashlight(boolean var1);

   boolean isFlashlightOn();
}
