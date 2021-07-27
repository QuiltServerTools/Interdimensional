package com.github.quiltservertools.interdimensional.mixin;

import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
    @Accessor("biomeSource")
    void setBiomeSource(BiomeSource biomeSource);

    @Accessor("structuresConfig")
    StructuresConfig getStructuresConfig();

    @Accessor("structuresConfig")
    void setStructuresConfig(StructuresConfig config);
}
