package net.quiltservertools.interdimensional.portals.networking;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;

public class NetworkManager implements ModInitializer {
    public static final Identifier SYNC_PORTALS = new Identifier(InterdimensionalPortals.MOD_ID, "syncportals");
    public static final Identifier SYNC_SETTINGS = new Identifier(InterdimensionalPortals.MOD_ID, "syncsettings");

    public static boolean doesPlayerHaveMod(ServerPlayerEntity player) {
        return ServerPlayNetworking.canSend(player, SYNC_PORTALS);
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            PortalRegistrySync.registerSyncOnPlayerJoin();
        });
    }
}
