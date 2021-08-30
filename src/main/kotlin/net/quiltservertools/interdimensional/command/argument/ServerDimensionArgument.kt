package com.github.quiltservertools.interdimensional.command.argument

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.DimensionArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.world.dimension.DimensionOptions
import net.minecraft.world.gen.GeneratorOptions


object ServerDimensionArgument {
    fun dimension(name: String): RequiredArgumentBuilder<ServerCommandSource, Identifier> {
        return CommandManager.argument(name, IdentifierArgumentType.identifier())
            .suggests { context, builder ->
                return@suggests CommandSource.suggestIdentifiers(context.source.server.worldRegistryKeys.map { it.value }, builder)
            }
    }

    fun get(context: CommandContext<ServerCommandSource>, name: String): ServerWorld {
        return DimensionArgumentType.getDimensionArgument(context, name)
    }
}