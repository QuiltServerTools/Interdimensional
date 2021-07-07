package com.github.quiltservertools.interdimensional.util;

import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

public record WorldData(long seed, Identifier dimension, Identifier chunkGeneratorDimId, int difficulty, JsonObject gamerules) {

    public RuntimeWorldConfig toRuntimeWorldConfig(MinecraftServer server) {
        var config = new RuntimeWorldConfig();
        config.setSeed(seed);

        config.setDimensionType(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, dimension));

        var generatorDim = server.getSaveProperties().getGeneratorOptions().getDimensions().get(chunkGeneratorDimId);
        var generator = generatorDim == null ? server.getSaveProperties().getGeneratorOptions().getChunkGenerator() : generatorDim.getChunkGenerator();

        config.setGenerator(generator.withSeed(seed));

        config.setDifficulty(WorldConfigUtils.getDifficulty(difficulty));

        return config;
    }
}
