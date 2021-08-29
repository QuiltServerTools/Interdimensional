package net.quiltservertools.interdimensional.command.argument

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.command.argument.DimensionArgumentType

object DimensionOverrideArgumentType : AbstractInterdimensionalArgumentType() {

    init {
        criteriumSuggestors["seed"] = Suggestor(LongArgumentType.longArg())
        criteriumSuggestors["type"] = Suggestor(DimensionArgumentType.dimension())
        criteriumSuggestors["generator"] = Suggestor(DimensionArgumentType.dimension())
        criteriumSuggestors["difficulty"] = Suggestor(StringArgumentType.string())
        criteriumSuggestors["custom_generator"] = Suggestor(BoolArgumentType.bool())
        criteria = criteriumSuggestors.keys
    }
}