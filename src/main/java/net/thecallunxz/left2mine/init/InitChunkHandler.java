package net.thecallunxz.left2mine.init;

import java.util.Iterator;
import java.util.List;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.thecallunxz.left2mine.blocks.nodes.tileentities.TileEntityChunkLoader;

public class InitChunkHandler implements LoadingCallback {
   public void ticketsLoaded(List<Ticket> tickets, World world) {
      Iterator var3 = tickets.iterator();

      while(var3.hasNext()) {
         Ticket ticket = (Ticket)var3.next();
         int x = ticket.getModData().getInteger("x");
         int y = ticket.getModData().getInteger("y");
         int z = ticket.getModData().getInteger("z");
         TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
         if (te instanceof TileEntityChunkLoader) {
            ((TileEntityChunkLoader)te).forceChunkLoading(ticket);
         }
      }

   }
}
