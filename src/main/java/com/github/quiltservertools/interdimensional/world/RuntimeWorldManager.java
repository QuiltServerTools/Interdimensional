package com.github.quiltservertools.interdimensional.world;

import com.github.quiltservertools.interdimensional.Interdimensional;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RuntimeWorldManager {
    private static final List<RuntimeWorldHandle> runtimeDimensionHandlers = new ArrayList<>();

    public static void add(RuntimeWorldConfig config, Identifier identifier) {
        RuntimeWorldHandle handle = Interdimensional.FANTASY.getOrOpenPersistentWorld(identifier, config);
        runtimeDimensionHandlers.add(handle);
    }

    public static void closeAll() {
        runtimeDimensionHandlers.removeIf(handle -> {
            Interdimensional.LOGGER.info("Closed world " + handle.asWorld().getRegistryKey().getValue().toString());
            handle.delete();
            return true;
        });
    }

    public static ServerWorld get(Identifier identifier, MinecraftServer server) {
        var result = runtimeDimensionHandlers.stream().filter(h -> h.asWorld().getRegistryKey().getValue().equals(identifier)).findFirst();
        if (result.isPresent()) {
            return result.get().asWorld();
        } else {
            return server.getOverworld();
        }
    }
}
