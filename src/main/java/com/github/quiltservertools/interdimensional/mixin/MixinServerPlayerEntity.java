package com.github.quiltservertools.interdimensional.mixin;

import com.github.quiltservertools.interdimensional.duck.ServerPlayerEntityAccess;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity implements ServerPlayerEntityAccess {

    @Unique
    private ChunkGenerator customGenerator;

    @Override
    public ChunkGenerator getCustomGenerator() {
        return customGenerator;
    }

    @Override
    public void setCustomGenerator(ChunkGenerator generator) {
        customGenerator = generator;
    }

}
