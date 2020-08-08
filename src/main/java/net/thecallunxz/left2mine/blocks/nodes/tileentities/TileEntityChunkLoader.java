package net.thecallunxz.left2mine.blocks.nodes.tileentities;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.thecallunxz.left2mine.Main;

public class TileEntityChunkLoader extends TileEntity implements ITickable {
   protected boolean forceClientUpdate = true;
   private Ticket chunkTicket;
   private int loadDistance;

   public TileEntityChunkLoader() {
      this.loadDistance = Main.distance;
   }

   public void update() {
      Main.proxy.renderNodeParticles(this.getWorld(), this.pos, Item.getItemFromBlock(this.getWorld().getBlockState(this.pos).getBlock()), true, 1.0F, 1.0F, 1.0F);
   }

   public List<ChunkPos> getLoadArea() {
      List<ChunkPos> loadArea = new LinkedList();

      for(int x = -this.loadDistance; x < this.loadDistance + 1; ++x) {
         for(int z = -this.loadDistance; z < this.loadDistance + 1; ++z) {
            ChunkPos chunkCoords = new ChunkPos((this.getPos().getX() >> 4) + x, (this.getPos().getZ() >> 4) + z);
            loadArea.add(chunkCoords);
         }
      }

      return loadArea;
   }

   public void validate() {
      super.validate();
      if (!this.world.isRemote && this.chunkTicket == null) {
         Ticket ticket = ForgeChunkManager.requestTicket(Main.instance, this.world, Type.NORMAL);
         if (ticket != null) {
            this.forceChunkLoading(ticket);
         }
      }

   }

   public void invalidate() {
      super.invalidate();
      this.stopChunkLoading();
   }

   public void setLoadDistance(int dist) {
      this.loadDistance = dist;
      this.forceChunkLoading(this.chunkTicket);
   }

   public void forceChunkLoading(Ticket ticket) {
      this.stopChunkLoading();
      this.chunkTicket = ticket;
      if (this.chunkTicket != null) {
         this.chunkTicket.getModData().setInteger("x", this.pos.getX());
         this.chunkTicket.getModData().setInteger("y", this.pos.getY());
         this.chunkTicket.getModData().setInteger("z", this.pos.getZ());
         Iterator var2 = this.getLoadArea().iterator();

         while(var2.hasNext()) {
            ChunkPos coord = (ChunkPos)var2.next();
            ForgeChunkManager.forceChunk(this.chunkTicket, coord);
         }
      }

   }

   public void unforceChunkLoading() {
      UnmodifiableIterator localUnmodifiableIterator = this.chunkTicket.getChunkList().iterator();

      while(localUnmodifiableIterator.hasNext()) {
         Object obj = localUnmodifiableIterator.next();
         ChunkPos coord = (ChunkPos)obj;
         ForgeChunkManager.unforceChunk(this.chunkTicket, coord);
      }

   }

   public void stopChunkLoading() {
      if (this.chunkTicket != null) {
         ForgeChunkManager.releaseTicket(this.chunkTicket);
         this.chunkTicket = null;
      }

   }

   public void readFromNBT(NBTTagCompound NBTC) {
      super.readFromNBT(NBTC);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound NBTC) {
      super.writeToNBT(NBTC);
      return NBTC;
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.getNbtCompound());
   }
}
