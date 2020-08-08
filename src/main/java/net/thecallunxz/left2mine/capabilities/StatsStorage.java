package net.thecallunxz.left2mine.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class StatsStorage implements IStorage<IStats> {
   public NBTBase writeNBT(Capability<IStats> capability, IStats instance, EnumFacing side) {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setInteger("commonKilled", instance.getCommonKilled());
      nbt.setInteger("huntersKilled", instance.getHuntersKilled());
      nbt.setInteger("boomersKilled", instance.getBoomersKilled());
      nbt.setInteger("smokersKilled", instance.getSmokersKilled());
      nbt.setInteger("tankDamage", instance.getTankDamage());
      nbt.setInteger("witchDamage", instance.getWitchDamage());
      nbt.setInteger("deaths", instance.getDeaths());
      nbt.setInteger("incapacitations", instance.getIncapacitations());
      nbt.setInteger("pillsUsed", instance.getPillsUsed());
      nbt.setInteger("medpacksUsed", instance.getMedpacksUsed());
      nbt.setInteger("pipebombsUsed", instance.getPipebombsUsed());
      nbt.setInteger("molotovsUsed", instance.getMolotovsUsed());
      return nbt;
   }

   public void readNBT(Capability<IStats> capability, IStats instance, EnumFacing side, NBTBase nbtBase) {
      NBTTagCompound nbt = (NBTTagCompound)nbtBase;
      instance.setCommonKilled(nbt.getInteger("commonKilled"));
      instance.setHuntersKilled(nbt.getInteger("huntersKilled"));
      instance.setBoomersKilled(nbt.getInteger("boomersKilled"));
      instance.setSmokersKilled(nbt.getInteger("smokersKilled"));
      instance.setTankDamage(nbt.getInteger("tankDamage"));
      instance.setWitchDamage(nbt.getInteger("witchDamage"));
      instance.setDeaths(nbt.getInteger("deaths"));
      instance.setIncapacitations(nbt.getInteger("incapacitations"));
      instance.setPillsUsed(nbt.getInteger("pillsUsed"));
      instance.setMedpacksUsed(nbt.getInteger("medpacksUsed"));
      instance.setPipebombsUsed(nbt.getInteger("pipebombsUsed"));
      instance.setMolotovsUsed(nbt.getInteger("molotovsUsed"));
   }
}
