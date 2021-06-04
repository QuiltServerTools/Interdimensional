package com.github.quiltservertools.interdimensional;

import com.github.quiltservertools.interdimensional.world.RuntimeWorldManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Config {
    private Config(JsonElement json, MinecraftServer server) {
        var dims = json.getAsJsonArray();
        dims.forEach(e -> {
            // Loading logic
        });
    }

    public void shutdown() {
        RuntimeWorldManager.closeAll();
    }

    public static Config createConfig(Path path, MinecraftServer server) {
        JsonElement json;
        try {
            json = new JsonParser().parse(Files.readString(path));
        } catch (IOException e) {
            try {
                Files.copy(Objects.requireNonNull(Interdimensional.class.getResourceAsStream("/data/interdimensional/default_config.json")), path);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                Interdimensional.LOGGER.error("Unable to create default config file for Interdimensional");
            }
            json = new JsonArray();
        }
        return new Config(json, server);
    }
}
