package net.thecallunxz.left2mine.items.weapons.properties;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class ProjectileProperties implements INBTSerializable<NBTTagCompound> {
   public String type;
   public boolean visible;
   public float damage;
   public float size;
   public float power;
   public double speed;
   public int life;
   public boolean gravity;
   public boolean damageReduceOverLife;
   public boolean damageReduceIfNotZoomed;

   public ProjectileProperties(NBTTagCompound tag) {
      this.type = tag.getString("type");
      this.visible = tag.getBoolean("visible");
      this.damage = tag.getFloat("damage");
      this.size = tag.getFloat("size");
      this.power = tag.getFloat("power");
      this.speed = tag.getDouble("speed");
      this.life = tag.getInteger("life");
      this.gravity = tag.getBoolean("gravity");
      this.damageReduceOverLife = tag.getBoolean("damageReduceOverLife");
      this.damageReduceIfNotZoomed = tag.getBoolean("damageReduceIfNotZoomed");
   }

   public ProjectileProperties() {
      this.setProperties();
   }

   public void setProperties() {
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("type", this.type);
      tag.setBoolean("visible", this.visible);
      tag.setFloat("damage", this.damage);
      tag.setFloat("size", this.size);
      tag.setFloat("power", this.power);
      tag.setDouble("speed", this.speed);
      tag.setInteger("life", this.life);
      tag.setBoolean("gravity", this.gravity);
      tag.setBoolean("damageReduceOverLife", this.damageReduceOverLife);
      tag.setBoolean("damageReduceIfNotZoomed", this.damageReduceIfNotZoomed);
      return tag;
   }

   public void deserializeNBT(NBTTagCompound tag) {
      this.type = tag.getString("type");
      this.visible = tag.getBoolean("visible");
      this.damage = tag.getFloat("damage");
      this.size = tag.getFloat("size");
      this.power = tag.getFloat("power");
      this.speed = tag.getDouble("speed");
      this.life = tag.getInteger("life");
      this.gravity = tag.getBoolean("gravity");
      this.damageReduceOverLife = tag.getBoolean("damageReduceOverLife");
      this.damageReduceIfNotZoomed = tag.getBoolean("damageReduceIfNotZoomed");
   }
}
