package com.github.quiltservertools.interdimensional;

import com.github.quiltservertools.interdimensional.world.RuntimeWorldManager;
import com.google.gson.*;
import net.minecraft.util.Identifier;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Config {
    private static Path path;

    private final int version;

    private Config(JsonElement json, Path newPath) {
        var object = json.getAsJsonObject();
        var worlds = object.get("worlds").getAsJsonArray();
        worlds.forEach(e -> {
            // Loading logic
            var o = e.getAsJsonObject();
            var identifier = new Identifier(o.get("identifier").getAsString());
            RuntimeWorldManager.add(new RuntimeWorldConfig(), identifier);
        });

        this.version = object.get("version").getAsInt();
        path = newPath;
    }

    public int getConfigVersion() {
        return this.version;
    }

    public void shutdown() {
        var json = new JsonObject();
        var worlds = new JsonArray();
        RuntimeWorldManager.closeAll().forEach(worlds::add);
        json.addProperty("version", version);
        json.add("worlds", worlds);
        try {
            Files.writeString(path, new GsonBuilder().setPrettyPrinting().create().toJson(json));
        } catch (IOException e) {
            Interdimensional.LOGGER.error("Unable to save Interdimensional config file");
        }
    }

    public static Config createConfig(Path path) {
        JsonObject json;
        try {
            json = new JsonParser().parse(Files.readString(path)).getAsJsonObject();
        } catch (IOException e) {
            try {
                Files.copy(Objects.requireNonNull(Interdimensional.class.getResourceAsStream("/data/interdimensional/default_config.json")), path);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                Interdimensional.LOGGER.error("Unable to create default config file for Interdimensional");
            }
            json = new JsonObject();
            json.addProperty("version", 1);
            json.add("worlds", new JsonArray());
        }
        return new Config(json, path);
    }
}
