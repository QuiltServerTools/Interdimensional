package com.github.quiltservertools.interdimensional.command.argument

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.world.gen.feature.StructureFeature
import java.util.concurrent.CompletableFuture

object StructureListArgumentType : SuggestionProvider<ServerCommandSource?> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource?>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val input = builder.remaining
        if (input.isEmpty()) {
            /*for ((key): Map.Entry<String?, StructureFeature<*>?> in StructureFeature.STRUCTURES) {
                builder.suggest(key)
            }*/
        } else {
            // Check if comma is present
            if (input[input.length - 1].code == 44) {
                for (entry in StructureFeature.STRUCTURES) {
                    builder.suggest(entry.key)
                }
            } else {
                // Comma is not present, we suggest the rest of the structure
                val lastComma = input.lastIndexOf(44.toChar())
                val current: String = if (lastComma == -1) {
                    input
                } else {
                    input.substring(lastComma)
                }
                for (entry in StructureFeature.STRUCTURES) {
                    var i = 0
                    while (i < current.length - 1 && i < entry.key.length - 1) {
                        if (current.endsWith(",")) {
                            for (entry1 in StructureFeature.STRUCTURES) {
                                builder.suggest(input + entry.key)
                            }
                        } else if (current == entry.key) {
                            for (entry1 in StructureFeature.STRUCTURES) {
                                builder.suggest("$input,$entry.key")
                            }
                        } else {
                            if (entry.key.startsWith(current)) {
                                if (lastComma == -1) {
                                    builder.suggest(entry.key)
                                } else {
                                    builder.suggest(input.substring(lastComma) + entry.key)
                                }
                            }
                        }
                        i++
                    }
                }
            }
        }
        return builder.buildFuture()
    }
}