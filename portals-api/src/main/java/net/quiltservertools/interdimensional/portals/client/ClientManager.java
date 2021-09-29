package net.quiltservertools.interdimensional.portals.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.BlockPos;
import net.quiltservertools.interdimensional.portals.networking.NetworkManager;

import java.util.HashMap;
import java.util.Map;

public class ClientManager {
    private static ClientManager instance;

    public static ClientManager getInstance() {
        return instance;
    }

    public static void init() {
        instance = new ClientManager();
    }

    private final Map<BlockPos, Integer> positions = new HashMap<>();

    private void addBlock(BlockPos pos, int color) {
        positions.put(pos, color);
    }

    private void clear() {
        positions.clear();
    }

    public int getColorAtPosition(BlockPos pos) {
        if (positions.containsKey(pos)) {
            return positions.get(pos);
        }
        //FIXME
        // I stole this colour from the previous portal API code
        // It doesn't seem quite right and I have no clue how to obtain the right one
        return 1908001;
    }

    public ClientManager() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkManager.SYNC_PORTALS, ((client, handler, buf, responseSender) -> {
            var pos = buf.readBlockPos();
            var color = buf.readInt();
            addBlock(pos, color);
        }));

        ClientPlayNetworking.registerGlobalReceiver(NetworkManager.CLEAR_PORTALS, ((client, handler, buf, responseSender) -> clear()));
    }
}
