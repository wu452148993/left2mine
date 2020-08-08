package net.thecallunxz.left2mine.items.weapons.properties;

import net.minecraft.util.SoundEvent;

public class GunProperties {
   public boolean auto;
   public boolean infinite;
   public boolean refillable;
   public boolean shellreload;
   public boolean doubleBloomSub;
   public int maxFireCooldown;
   public int maxReloadTime;
   public int magazineSize;
   public int storageSize;
   public float recoil;
   public float accuracy;
   public int shots;
   public int bloomAdd;
   public int bloomSubtract;
   public int maxBloom;
   public SoundEvent fireSound;
   public SoundEvent equipSound;
   public SoundEvent reloadSound;
   public SoundEvent pumpSound;
   public boolean canZoom;
   public float zoomFovModifier;
   public double zoomXOffset;
   public double zoomYOffset;
   public double zoomZOffset;
   public boolean zoomSmooth;
   public double flashXOffset;
   public double flashYOffset;
   public double flashZOffset;
   public double scopeXOffset;
   public double scopeYOffset;
   public double scopeZOffset;

   public GunProperties() {
      this.setProperties();
   }

   public void setProperties() {
   }
}
