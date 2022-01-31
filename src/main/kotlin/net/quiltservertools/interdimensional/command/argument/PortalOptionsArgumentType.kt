package net.quiltservertools.interdimensional.command.argument

import com.mojang.brigadier.arguments.IntegerArgumentType
import net.minecraft.command.argument.ColorArgumentType
import net.minecraft.command.argument.DimensionArgumentType

class PortalOptionsArgumentType : AbstractInterdimensionalArgumentType() {
    init {
        criteriumSuggestors["color"] = Suggestor(ColorArgumentType.color())
        criteriumSuggestors["permission"] = Suggestor(IntegerArgumentType.integer())
        criteriumSuggestors["source_world"] = Suggestor(DimensionArgumentType.dimension())

        criteria = criteriumSuggestors.keys
    }
}