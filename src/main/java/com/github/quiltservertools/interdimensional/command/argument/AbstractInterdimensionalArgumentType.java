package com.github.quiltservertools.interdimensional.command.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractInterdimensionalArgumentType implements SuggestionProvider<ServerCommandSource> {

    protected Set<String> criteria;
    protected HashMap<String, Suggestor> criteriumSuggestors = new HashMap<>();
    //TODO allow custom suggestions in a suggestor

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context,
                                                         SuggestionsBuilder builder) {
        String input = builder.getInput();
        int lastSpaceIndex = input.lastIndexOf(' ');
        char[] inputArr = input.toCharArray();
        int lastColonIndex = -1;
        for (int i = inputArr.length - 1; i >= 0; i--) {
            char c = inputArr[i];
            if (c == ':') { // encountered a colon
                lastColonIndex = i;
            } else if (lastColonIndex != -1 && c == ' ') { // we have encountered a space after our colon
                break;
            }
        }
        if (lastColonIndex == -1) { // no colon, just suggest criteria
            SuggestionsBuilder offsetBuilder = builder.createOffset(lastSpaceIndex + 1);
            builder.add(suggestCriteria(offsetBuilder));
        } else { // take last colon
            String[] spaceSplit = input.substring(0, lastColonIndex).split(" ");
            String criterium = spaceSplit[spaceSplit.length - 1];
            String criteriumArg = input.substring(lastColonIndex + 1);

            if (!criteriumSuggestors.containsKey(criterium)) {
                return builder.buildFuture();
            } else { // check if suggestor consumes the rest
                Suggestor suggestor = criteriumSuggestors.get(criterium);

                int remaining = suggestor.getRemaining(criteriumArg);
                if (remaining > 0) { // suggest new criterium
                    SuggestionsBuilder offsetBuilder = builder.createOffset(input.length() - remaining + 1);
                    return suggestCriteria(offsetBuilder).buildFuture();
                } else {
                    SuggestionsBuilder offsetBuilder = builder.createOffset(lastColonIndex + 1);
                    return suggestor.listSuggestions(context, offsetBuilder);
                }
            }
        }

        return builder.buildFuture();
    }
    public HashMap<String, Object> rawProperties(String s) throws CommandSyntaxException {
        StringReader reader = new StringReader(s);
        HashMap<String, Object> result = new HashMap<>();
        while (reader.canRead()) {
            String propertyName = reader.readStringUntil(':').trim();
            Suggestor suggestor = this.criteriumSuggestors.get(propertyName);
            if (suggestor == null) {
                throw new SimpleCommandExceptionType(new LiteralMessage("Unknown property value: " + propertyName))
                        .create();
            }
            result.put(propertyName, suggestor.parse(reader));
        }
        return result;
    }


    private SuggestionsBuilder suggestCriteria(SuggestionsBuilder builder) {
        String input = builder.getRemaining().toLowerCase();
        for (String criterium : criteria) {
            if (criterium.startsWith(input)) {
                builder.suggest(criterium + ":");
            }
        }
        return builder;
    }

    static class Suggestor {
        private final List<SuggestionProvider<ServerCommandSource>> suggestionList = new ArrayList<>();
        boolean useSuggestionProvider = false;
        private SuggestionProvider<ServerCommandSource> suggestionProvider;
        private ArgumentType<?> argumentType;

        public Suggestor(SuggestionProvider<ServerCommandSource> suggestionProvider) {
            this.suggestionProvider = suggestionProvider;
            this.useSuggestionProvider = true;
        }

        public Suggestor(ArgumentType<?> argumentType) {
            this.argumentType = argumentType;
        }

        public CompletableFuture<Suggestions> listSuggestions(CommandContext<ServerCommandSource> context,
                                                              SuggestionsBuilder builder) {
            if (this.useSuggestionProvider) {
                try {
                    return this.suggestionProvider.getSuggestions(context, builder);
                } catch (CommandSyntaxException e) {
                    return builder.buildFuture();
                }
            } else {
                return this.argumentType.listSuggestions(context, builder);
            }
        }

        public void putSuggestions(SuggestionProvider<ServerCommandSource> provider) {
            suggestionList.add(provider);
        }

        public int getRemaining(String s) {
            if (this.useSuggestionProvider) {
                int spaceIndex = s.lastIndexOf(' ');
                if (spaceIndex == -1)
                    return -1;
                return s.length() - s.lastIndexOf(' ');
            }
            try {
                StringReader reader = new StringReader(s);
                this.argumentType.parse(reader);
                return reader.getRemainingLength();
            } catch (CommandSyntaxException e) {
                return -1;
            }
        }

        public Object parse(StringReader reader) throws CommandSyntaxException {
            if (this.useSuggestionProvider) {
                int startPos = reader.getCursor();
                try {
                    return reader.readStringUntil(' ');
                } catch (CommandSyntaxException e) {
                    return reader.getString().substring(startPos);
                }
            } else {
                return this.argumentType.parse(reader);
            }
        }
    }
}


