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
        var input = builder.getInput();
        input = input.substring(input.lastIndexOf("exclude_structures:") != -1 ? input.lastIndexOf("exclude_structures:") : (input.lastIndexOf("include_structures:") != -1 ? input.lastIndexOf("include_structures:") : (0 )));
        var server = context.getSource().getServer();
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
                    //TODO fix this
                }
            }
        }
        return builder.buildFuture();
    }
}
