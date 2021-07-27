package com.github.quiltservertools.interdimensional.mixin;

import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkGenerator.class)
public class MixinChunkGenerator {
    @Final
    @Mutable
    @Shadow
    protected BiomeSource biomeSource;

    @Final
    @Mutable
    @Shadow
    private StructuresConfig structuresConfig;
}
