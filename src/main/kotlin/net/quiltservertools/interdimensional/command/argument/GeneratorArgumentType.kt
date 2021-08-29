package net.quiltservertools.interdimensional.command.argument

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.command.suggestion.SuggestionProviders

class GeneratorArgumentType : AbstractInterdimensionalArgumentType() {

    init {
        criteriumSuggestors["seed"] = Suggestor(LongArgumentType.longArg())

        //Noise
        criteriumSuggestors["single_biome"] =
            Suggestor(IdentifierArgumentType.identifier(), SuggestionProviders.ALL_BIOMES)
        criteriumSuggestors["vanilla_layered"] = Suggestor(BoolArgumentType.bool())
        //criteriumSuggestors.put("multi_noise", new Suggestor(BoolArgumentType.bool()));
        criteriumSuggestors["the_end_biome_source"] = Suggestor(BoolArgumentType.bool())

        //Options
        criteriumSuggestors["large_biomes"] = Suggestor(BoolArgumentType.bool())
        criteriumSuggestors["biome_seed"] = Suggestor(LongArgumentType.longArg())
        criteriumSuggestors["superflat"] = Suggestor(BoolArgumentType.bool())

        //Structures
        criteriumSuggestors["generate_structures"] = Suggestor(BoolArgumentType.bool())
        criteriumSuggestors["generate_strongholds"] = Suggestor(BoolArgumentType.bool())
        criteriumSuggestors["exclude_structures"] = Suggestor(StringArgumentType.string(), StructureListArgumentType)
        criteriumSuggestors["include_structures"] = Suggestor(StringArgumentType.string(), StructureListArgumentType)
        criteria = criteriumSuggestors.keys
    }
}