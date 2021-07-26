package com.github.quiltservertools.interdimensional.command.argument;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;

public class DimensionOverrideArgumentType extends AbstractInterdimensionalArgumentType {

    public static DimensionOverrideArgumentType INSTANCE = new DimensionOverrideArgumentType();

    private DimensionOverrideArgumentType() {
        criteriumSuggestors.put("seed", new Suggestor(LongArgumentType.longArg()));
        criteriumSuggestors.put("type", new Suggestor(DimensionArgumentType.dimension()));
        criteriumSuggestors.put("generator", new Suggestor(DimensionArgumentType.dimension()));
        criteriumSuggestors.put("difficulty", new Suggestor(StringArgumentType.string()));
        criteriumSuggestors.put("custom_biome_source", new Suggestor(BoolArgumentType.bool()));
        this.criteria = criteriumSuggestors.keySet();
    }
}
