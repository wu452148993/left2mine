package net.thecallunxz.left2mine.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class EquipProvider implements ICapabilitySerializable<NBTBase> {
   @CapabilityInject(IEquip.class)
   public static final Capability<IEquip> EQUIPPED = null;
   private IEquip instance;

   public EquipProvider() {
      this.instance = (IEquip)EQUIPPED.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == EQUIPPED;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return capability == EQUIPPED ? EQUIPPED.cast(this.instance) : null;
   }

   public NBTBase serializeNBT() {
      return EQUIPPED.getStorage().writeNBT(EQUIPPED, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      EQUIPPED.getStorage().readNBT(EQUIPPED, this.instance, (EnumFacing)null, nbt);
   }
}
