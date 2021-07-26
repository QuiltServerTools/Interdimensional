package com.github.quiltservertools.interdimensional;

import com.github.quiltservertools.interdimensional.world.RuntimeWorldManager;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.util.Identifier;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Config {
    private static Path path;
    private Config(JsonElement json, Path newPath) {
        var dims = json.getAsJsonArray();
        dims.forEach(e -> {
            // Loading logic
            var object = e.getAsJsonObject();
            var identifier = new Identifier(object.get("identifier").getAsString());
            RuntimeWorldManager.add(new RuntimeWorldConfig(), identifier);
        });
        path = newPath;
    }

    public void shutdown() {
        var json = new JsonArray();
        RuntimeWorldManager.closeAll().forEach(json::add);
        try {
            Files.writeString(path, new GsonBuilder().setPrettyPrinting().create().toJson(json));
        } catch (IOException e) {
            Interdimensional.LOGGER.error("Unable to save Interdimensional config file");
        }
    }

    public static Config createConfig(Path path) {
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
        return new Config(json, path);
    }
}
