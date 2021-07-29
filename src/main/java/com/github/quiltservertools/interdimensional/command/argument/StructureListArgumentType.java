package com.github.quiltservertools.interdimensional.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StructureListArgumentType implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        var input = builder.getRemaining();
        if (input.length() == 0) {
            for (Map.Entry<String, StructureFeature<?>> entry : StructureFeature.STRUCTURES.entrySet()) {
                builder.suggest(entry.getKey());
            }
        } else {
            // Check if comma is present
            if (input.charAt(input.length() - 1) == 44) {
                for (Map.Entry<String, StructureFeature<?>> entry : StructureFeature.STRUCTURES.entrySet()) {
                    builder.suggest(entry.getKey());
                }
            } else {
                // Comma is not present, we suggest the rest of the structure
                var lastComma = input.lastIndexOf(44);
                String current;
                if (lastComma == -1) {
                    current = input;
                } else {
                    current = input.substring(lastComma);
                }
                for (Map.Entry<String, StructureFeature<?>> entry : StructureFeature.STRUCTURES.entrySet()) {
                    for (int i = 0; i < current.length() - 1 && i < entry.getKey().length() - 1; i++) {
                        if (current.endsWith(",")) {
                            for (Map.Entry<String, StructureFeature<?>> entry1 : StructureFeature.STRUCTURES.entrySet()) {
                                builder.suggest(input + entry1.getKey());
                            }
                        } else if (current.equals(entry.getKey())) {
                            for (Map.Entry<String, StructureFeature<?>> entry1 : StructureFeature.STRUCTURES.entrySet()) {
                                builder.suggest(input + "," + entry1.getKey());
                            }
                        } else {
                            if (entry.getKey().startsWith(current)) {
                                if (lastComma == -1) {
                                    builder.suggest(entry.getKey());
                                } else {
                                    builder.suggest(input.substring(lastComma) + entry.getKey());
                                }
                            }
                        }
                    }
                }
            }
        }
        return builder.buildFuture();
    }
}
