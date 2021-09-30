package net.quiltservertools.interdimensional.portals.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;

public class NetworkManager {
    public static final Identifier SYNC_PORTALS = new Identifier(InterdimensionalPortals.MOD_ID, "syncportals");

    public static boolean isVanilla(ServerPlayerEntity player) {
        return !ServerPlayNetworking.canSend(player, SYNC_PORTALS);
    }

    public static void sendPortalInfo(ServerPlayerEntity player, BlockPos pos, int color) {
        if (isVanilla(player)) return;
        var buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeInt(color);
        ServerPlayNetworking.send(player, SYNC_PORTALS, buf);
    }
}
