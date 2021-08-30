package net.quiltservertools.interdimensional.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.DimensionArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.quiltservertools.interdimensional.command.argument.AbstractInterdimensionalArgumentType
import java.util.concurrent.CompletableFuture

object ServerDimensionSuggestor :
    AbstractInterdimensionalArgumentType.Suggestor(IdentifierArgumentType.identifier()) {
    override fun listSuggestions(
        context: CommandContext<ServerCommandSource>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return CommandSource.suggestIdentifiers(
            context.source.server.worldRegistryKeys.map { it.value },
            builder
        )
    }

    override fun parse(reader: StringReader): Any {
        return DimensionArgumentType.dimension().parse(reader)
    }
}