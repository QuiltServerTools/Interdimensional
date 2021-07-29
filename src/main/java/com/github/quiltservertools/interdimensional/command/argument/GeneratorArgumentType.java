package com.github.quiltservertools.interdimensional.command.argument;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;

public class GeneratorArgumentType extends AbstractInterdimensionalArgumentType {
    public static GeneratorArgumentType INSTANCE = new GeneratorArgumentType();

    private GeneratorArgumentType() {
        criteriumSuggestors.put("seed", new Suggestor(LongArgumentType.longArg()));

        //Noise
        criteriumSuggestors.put("single_biome", new Suggestor(IdentifierArgumentType.identifier(), SuggestionProviders.ALL_BIOMES));
        criteriumSuggestors.put("vanilla_layered", new Suggestor(BoolArgumentType.bool()));
        //criteriumSuggestors.put("multi_noise", new Suggestor(BoolArgumentType.bool()));
        criteriumSuggestors.put("the_end_biome_source", new Suggestor(BoolArgumentType.bool()));

        //Options
        criteriumSuggestors.put("large_biomes", new Suggestor(BoolArgumentType.bool()));
        criteriumSuggestors.put("biome_seed", new Suggestor(LongArgumentType.longArg()));
        criteriumSuggestors.put("superflat", new Suggestor(BoolArgumentType.bool()));

        //Structures
        criteriumSuggestors.put("generate_structures", new Suggestor(BoolArgumentType.bool()));
        criteriumSuggestors.put("generate_strongholds", new Suggestor(BoolArgumentType.bool()));
        criteriumSuggestors.put("exclude_structures", new Suggestor(StringArgumentType.string(), new StructureListArgumentType()));
        criteriumSuggestors.put("include_structures", new Suggestor(StringArgumentType.string(), new StructureListArgumentType()));

        this.criteria = criteriumSuggestors.keySet();
    }
}
