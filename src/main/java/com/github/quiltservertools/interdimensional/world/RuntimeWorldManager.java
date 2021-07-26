package com.github.quiltservertools.interdimensional.world;

import com.github.quiltservertools.interdimensional.Interdimensional;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.util.ArrayList;
import java.util.List;

public class RuntimeWorldManager {
    private static final List<RuntimeWorldHandle> runtimeDimensionHandlers = new ArrayList<>();

    public static void add(RuntimeWorldConfig config, Identifier identifier) {
        RuntimeWorldHandle handle = Interdimensional.FANTASY.getOrOpenPersistentWorld(identifier, config);
        runtimeDimensionHandlers.add(handle);
    }

    public static void remove(RuntimeWorldHandle handle) {
        runtimeDimensionHandlers.remove(handle);
        Interdimensional.LOGGER.info("Removed dimension" + handle.asWorld().getRegistryKey().getValue());
        handle.delete();
    }

    public static List<JsonObject> closeAll() {
        var list = new ArrayList<JsonObject>();
        runtimeDimensionHandlers.forEach(handle -> {
            var object = new JsonObject();
            object.addProperty("identifier", handle.asWorld().getRegistryKey().getValue().toString());
            list.add(object);
        });
        return list;
    }

    public static ServerWorld get(Identifier identifier, MinecraftServer server) {
        var result = runtimeDimensionHandlers.stream().filter(h -> h.asWorld().getRegistryKey().getValue().equals(identifier)).findFirst();
        if (result.isPresent()) {
            return result.get().asWorld();
        } else {
            return server.getOverworld();
        }
    }

    public static RuntimeWorldHandle getHandle(Identifier identifier, MinecraftServer server) {
        var result = runtimeDimensionHandlers.stream().filter(h -> h.asWorld().getRegistryKey().getValue().equals(identifier)).findFirst();
        return result.orElse(null);
    }
}
