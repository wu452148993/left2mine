package net.thecallunxz.left2mine.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class EquipStorage implements IStorage<IEquip> {
   public NBTBase writeNBT(Capability<IEquip> capability, IEquip instance, EnumFacing side) {
      NBTTagCompound nbt = new NBTTagCompound();
      NBTTagList tagList = new NBTTagList();

      for(int i = 0; i < instance.getEquippedList().size(); ++i) {
         ItemStack stack = (ItemStack)instance.getEquippedList().get(i);
         NBTTagCompound nbttagcompound = stack.serializeNBT();
         tagList.appendTag(nbttagcompound);
      }

      nbt.setTag("equipList", tagList);
      nbt.setBoolean("lying", instance.getLying());
      nbt.setBoolean("pinned", instance.getPinned());
      nbt.setBoolean("puked", instance.isPuked());
      nbt.setBoolean("flashlight", instance.isFlashlightOn());
      nbt.setFloat("animationangle", instance.getAnimationAngle());
      nbt.setUniqueId("tetheredUUID", instance.getTetheredUUID());
      nbt.setInteger("lives", instance.getLives());
      return nbt;
   }

   public void readNBT(Capability<IEquip> capability, IEquip instance, EnumFacing side, NBTBase nbt) {
      NBTTagList tagList = (NBTTagList)((NBTTagCompound)nbt).getTag("equipList");
      instance.clearEquipped();

      for(int i = 0; i < tagList.tagCount(); ++i) {
         NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
         instance.addEquipped(new ItemStack(nbttagcompound));
      }

      instance.setLying(((NBTTagCompound)nbt).getBoolean("lying"));
      instance.setPinned(((NBTTagCompound)nbt).getBoolean("pinned"));
      instance.setPuked(((NBTTagCompound)nbt).getBoolean("puked"));
      instance.setFlashlight(((NBTTagCompound)nbt).getBoolean("flashlight"));
      instance.setAnimationAngle(((NBTTagCompound)nbt).getFloat("animationangle"));
      instance.setTetheredUUID(((NBTTagCompound)nbt).getUniqueId("tetheredUUID"));
      instance.setLives(((NBTTagCompound)nbt).getInteger("lives"));
   }
}
