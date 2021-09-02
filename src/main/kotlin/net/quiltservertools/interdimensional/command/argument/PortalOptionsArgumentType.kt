package net.quiltservertools.interdimensional.command.argument

import com.mojang.brigadier.arguments.BoolArgumentType
import net.minecraft.command.argument.ColorArgumentType
import net.minecraft.command.argument.DimensionArgumentType

class PortalOptionsArgumentType : AbstractInterdimensionalArgumentType() {
    init {
        criteriumSuggestors["color"] = Suggestor(ColorArgumentType.color())
        //fixme criteriumSuggestors["flat"] = Suggestor(BoolArgumentType.bool())
        criteriumSuggestors["source_world"] = Suggestor(DimensionArgumentType.dimension())

        criteria = criteriumSuggestors.keys
    }
}