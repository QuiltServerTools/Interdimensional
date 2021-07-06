package com.github.quiltservertools.interdimensional;

import com.github.quiltservertools.interdimensional.util.WorldData;
import com.github.quiltservertools.interdimensional.world.RuntimeWorldManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Config {
    private Config(JsonElement json, MinecraftServer server) {
        var dims = json.getAsJsonArray();
        dims.forEach(e -> {
            // Loading logic
            var object = e.getAsJsonObject();
            var identifier = new Identifier(object.get("identifier").getAsString());

            var worldLike = new Identifier(object.get("world_like").getAsString());
            var generator = new Identifier(object.get("generator_dimension").getAsString());
            var seed = object.get("seed").getAsLong();
            var difficulty = object.get("difficulty").getAsInt();
            var gamerules = object.get("gamerules").getAsJsonObject();
            var data = new WorldData(seed, worldLike, generator, difficulty, gamerules);
            var config = data.toRuntimeWorldConfig(server);
            RuntimeWorldManager.add(config, identifier);
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
