package com.github.quiltservertools.interdimensional.duck;

import net.minecraft.world.gen.chunk.ChunkGenerator;

public interface ServerPlayerEntityAccess {
    ChunkGenerator getCustomGenerator();
    void setCustomGenerator(ChunkGenerator generator);
}
