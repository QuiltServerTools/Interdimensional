package net.quiltservertools.interdimensional.command.argument

import com.mojang.brigadier.arguments.BoolArgumentType
import net.minecraft.command.argument.ColorArgumentType

class PortalOptionsArgumentType : AbstractInterdimensionalArgumentType() {
    init {
        criteriumSuggestors["color"] = Suggestor(ColorArgumentType.color())
        criteriumSuggestors["flat"] = Suggestor(BoolArgumentType.bool())

        criteria = criteriumSuggestors.keys
    }
}