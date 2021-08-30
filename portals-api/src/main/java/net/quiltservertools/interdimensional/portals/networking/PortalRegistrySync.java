package net.quiltservertools.interdimensional.portals.networking;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.quiltservertools.interdimensional.portals.CustomPortalApiRegistry;
import net.quiltservertools.interdimensional.portals.PerWorldPortals;
import net.quiltservertools.interdimensional.portals.util.PortalLink;

public class PortalRegistrySync {

    @Deprecated
    public static void enableSyncOnPlayerJoin() {

    }

    public static void registerSyncOnPlayerJoin() {
        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            if (NetworkManager.doesPlayerHaveMod(serverPlayNetworkHandler.player)) {
                sendSyncSettings(packetSender);
                for (PortalLink link : CustomPortalApiRegistry.getAllPortalLinks()) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeIdentifier(link.block);
                    buf.writeIdentifier(link.dimID);
                    buf.writeInt(link.colorID);
                    packetSender.sendPacket(NetworkManager.SYNC_PORTALS, buf);
                }
            }
        });
    }

    public static void sendSyncSettings(PacketSender packetSender) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packetSender.sendPacket(NetworkManager.SYNC_SETTINGS, buf);
    }

    public static void registerReceivePortalData() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkManager.SYNC_PORTALS, (client, handler, packet, sender) -> {
            Identifier frameBlock = packet.readIdentifier();
            Identifier dimID = packet.readIdentifier();
            int colorId = packet.readInt();
            PerWorldPortals.registerWorldPortal(new PortalLink(frameBlock, dimID, colorId));
        });
        ClientPlayNetworking.registerGlobalReceiver(NetworkManager.SYNC_SETTINGS, (client, handler, packet, sender) -> {
        });
    }
}
