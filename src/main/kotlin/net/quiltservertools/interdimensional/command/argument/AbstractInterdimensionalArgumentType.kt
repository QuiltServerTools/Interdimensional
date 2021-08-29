package net.quiltservertools.interdimensional.command.argument

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.HashSet

abstract class AbstractInterdimensionalArgumentType : SuggestionProvider<ServerCommandSource?> {
    var criteria: Set<String>? = HashSet<String>()
    var criteriumSuggestors = HashMap<String, Suggestor>()
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource?>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val input = builder.input
        val lastSpaceIndex = input.lastIndexOf(' ')
        val inputArr = input.toCharArray()
        var lastColonIndex = -1
        for (i in inputArr.indices.reversed()) {
            val c = inputArr[i]
            if (c == ':') { // encountered a colon
                lastColonIndex = i
            } else if (lastColonIndex != -1 && c == ' ') { // we have encountered a space after our colon
                break
            }
        }
        if (lastColonIndex == -1) { // no colon, just suggest criteria
            val offsetBuilder = builder.createOffset(lastSpaceIndex + 1)
            builder.add(suggestCriteria(offsetBuilder))
        } else { // take last colon
            val spaceSplit = input.substring(0, lastColonIndex).split(" ".toRegex()).toTypedArray()
            val criterium = spaceSplit[spaceSplit.size - 1]
            val criteriumArg = input.substring(lastColonIndex + 1)
            return if (!criteriumSuggestors.containsKey(criterium)) {
                builder.buildFuture()
            } else { // check if suggestor consumes the rest
                val suggestor = criteriumSuggestors[criterium]
                val remaining = suggestor!!.getRemaining(criteriumArg)
                if (remaining > 0) { // suggest new criterium
                    val offsetBuilder = builder.createOffset(input.length - remaining + 1)
                    suggestCriteria(offsetBuilder).buildFuture()
                } else {
                    val offsetBuilder = builder.createOffset(lastColonIndex + 1)
                    suggestor.listSuggestions(context, offsetBuilder)
                }
            }
        }
        return builder.buildFuture()
    }

    @Throws(CommandSyntaxException::class)
    fun rawProperties(s: String?): HashMap<String, Any> {
        val reader = StringReader(s)
        val result = HashMap<String, Any>()
        while (reader.canRead()) {
            val propertyName = reader.readStringUntil(':').trim { it <= ' ' }
            val suggestor = criteriumSuggestors[propertyName]
                ?: throw SimpleCommandExceptionType(LiteralMessage("Unknown property value: $propertyName"))
                    .create()
            result[propertyName] = suggestor.parse(reader)
        }
        return result
    }

    private fun suggestCriteria(builder: SuggestionsBuilder): SuggestionsBuilder {
        val input = builder.remaining.lowercase(Locale.getDefault())
        for (criterium in criteria!!) {
            if (criterium.startsWith(input)) {
                builder.suggest("$criterium:")
            }
        }
        return builder
    }

    class Suggestor {
        var useSuggestionProvider = false
        private var suggestionProvider: SuggestionProvider<ServerCommandSource?>? = null
        private val argumentType: ArgumentType<*>

        constructor(argumentType: ArgumentType<*>, suggestionProvider: SuggestionProvider<ServerCommandSource?>?) {
            this.argumentType = argumentType
            this.suggestionProvider = suggestionProvider
            useSuggestionProvider = true
        }

        constructor(argumentType: ArgumentType<*>) {
            this.argumentType = argumentType
        }

        fun listSuggestions(
            context: CommandContext<ServerCommandSource?>?,
            builder: SuggestionsBuilder
        ): CompletableFuture<Suggestions> {
            return if (useSuggestionProvider) {
                try {
                    suggestionProvider!!.getSuggestions(context, builder)
                } catch (e: CommandSyntaxException) {
                    builder.buildFuture()
                }
            } else {
                argumentType.listSuggestions(context, builder)
            }
        }

        fun getRemaining(s: String): Int {
            if (useSuggestionProvider) {
                val spaceIndex = s.lastIndexOf(' ')
                return if (spaceIndex == -1) -1 else s.length - s.lastIndexOf(' ')
            }
            return try {
                val reader = StringReader(s)
                argumentType.parse(reader)
                reader.remainingLength
            } catch (e: CommandSyntaxException) {
                -1
            }
        }

        @Throws(CommandSyntaxException::class)
        fun parse(reader: StringReader): Any {
            return if (useSuggestionProvider) {
                val startPos = reader.cursor
                try {
                    reader.readStringUntil(' ')
                } catch (e: CommandSyntaxException) {
                    reader.string.substring(startPos)
                }
            } else {
                argumentType.parse(reader)
            }
        }
    }
}