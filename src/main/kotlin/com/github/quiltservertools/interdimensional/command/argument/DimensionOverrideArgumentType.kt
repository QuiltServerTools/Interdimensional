package com.github.quiltservertools.interdimensional.command.argument

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType

object DimensionOverrideArgumentType : AbstractInterdimensionalArgumentType() {

    init {
        criteriumSuggestors["seed"] = Suggestor(LongArgumentType.longArg())
        criteriumSuggestors["type"] = ServerDimensionSuggestor
        criteriumSuggestors["generator"] = ServerDimensionSuggestor
        criteriumSuggestors["difficulty"] = Suggestor(StringArgumentType.string())
        criteriumSuggestors["custom_generator"] = Suggestor(BoolArgumentType.bool())
        criteria = criteriumSuggestors.keys
    }
}