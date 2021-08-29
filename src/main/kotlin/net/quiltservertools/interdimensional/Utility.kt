package net.quiltservertools.interdimensional

import net.quiltservertools.interdimensional.duck.ServerPlayerEntityAccess
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.gen.chunk.ChunkGenerator

var ServerPlayerEntity.customGenerator: ChunkGenerator?
    get() = (this as ServerPlayerEntityAccess).customGenerator
    set(value) {
        (this as ServerPlayerEntityAccess).customGenerator = value
    }
