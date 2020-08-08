package net.thecallunxz.left2mine.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class StatsProvider implements ICapabilitySerializable<NBTBase> {
   @CapabilityInject(IStats.class)
   public static final Capability<IStats> STATS = null;
   private IStats instance;

   public StatsProvider() {
      this.instance = (IStats)STATS.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == STATS;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return capability == STATS ? STATS.cast(this.instance) : null;
   }

   public NBTBase serializeNBT() {
      return STATS.getStorage().writeNBT(STATS, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      STATS.getStorage().readNBT(STATS, this.instance, (EnumFacing)null, nbt);
   }
}
