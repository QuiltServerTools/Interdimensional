package net.quiltservertools.interdimensional.portals.networking;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class NetworkManager implements DedicatedServerModInitializer {
    public static final Identifier SYNC_PORTALS = new Identifier(InterdimensionalPortals.MOD_ID, "syncportals");
    public static final Identifier SYNC_SETTINGS = new Identifier(InterdimensionalPortals.MOD_ID, "syncsettings");

    public static boolean doesPlayerHaveMod(ServerPlayerEntity player) {
        return ServerPlayNetworking.canSend(player, SYNC_PORTALS);
    }

    @Override
    public void onInitializeServer() {
        PortalRegistrySync.registerSyncOnPlayerJoin();
    }
}
