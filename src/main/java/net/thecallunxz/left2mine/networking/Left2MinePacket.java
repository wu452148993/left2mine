package net.thecallunxz.left2mine.networking;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class Left2MinePacket {
   public static final SimpleNetworkWrapper INSTANCE;

   static {
      INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("left2mine");
   }
}
