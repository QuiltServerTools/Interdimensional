package com.github.quiltservertools.interdimensional.command.argument;

import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;

public class BiomeSourceArgumentType extends AbstractInterdimensionalArgumentType {
    public static BiomeSourceArgumentType INSTANCE = new BiomeSourceArgumentType();

    private BiomeSourceArgumentType() {
        //TODO suggest all biomes (SuggestionProviders.ALL_BIOMES) once additional suggestions work
        criteriumSuggestors.put("single_biome", new Suggestor(IdentifierArgumentType.identifier()));
        criteriumSuggestors.put("vanilla_layered", new Suggestor(LongArgumentType.longArg()));
        criteriumSuggestors.put("multi_noise", new Suggestor(LongArgumentType.longArg()));
        criteriumSuggestors.put("the_end_biome_source", new Suggestor(LongArgumentType.longArg()));
        this.criteria = criteriumSuggestors.keySet();
    }
}
