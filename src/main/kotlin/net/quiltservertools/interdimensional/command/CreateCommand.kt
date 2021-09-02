package net.quiltservertools.interdimensional.command

import net.quiltservertools.interdimensional.command.argument.ServerDimensionArgument
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.info
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.success
import net.quiltservertools.interdimensional.command.argument.DimensionOverrideArgumentType
import net.quiltservertools.interdimensional.customGenerator
import net.quiltservertools.interdimensional.world.RuntimeWorldManager
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.Difficulty
import net.minecraft.world.gen.chunk.ChunkGenerator
import xyz.nucleoid.fantasy.RuntimeWorldConfig

object CreateCommand : Command {
    override fun register(): LiteralCommandNode<ServerCommandSource> {
        return CommandManager.literal("create")
            .requires(Permissions.require("interdimensional.command.create", 3))
            .then(CommandManager.argument("identifier", IdentifierArgumentType.identifier())
                .then(
                    ServerDimensionArgument.dimension("maplike")
                    .executes { ctx: CommandContext<ServerCommandSource> ->
                        run(
                            ServerDimensionArgument.get(ctx, "maplike"),
                            "",
                            IdentifierArgumentType.getIdentifier(ctx, "identifier"),
                            ctx
                        )
                    }
                    .then(CommandManager.argument("overrides", StringArgumentType.greedyString())
                        .suggests(DimensionOverrideArgumentType)
                        .executes { ctx: CommandContext<ServerCommandSource> ->
                            run(
                                ServerDimensionArgument.get(ctx, "maplike"),
                                StringArgumentType.getString(ctx, "overrides"),
                                IdentifierArgumentType.getIdentifier(ctx, "identifier"),
                                ctx
                            )
                        })
                )
            )
            .build()
    }

    private fun run(
        maplike: ServerWorld,
        args: String,
        identifier: Identifier,
        ctx: CommandContext<ServerCommandSource>
    ): Int {
        val propertyMap: HashMap<String, Any> = DimensionOverrideArgumentType.rawProperties(args)
        val scs = ctx.source!!
        scs.sendFeedback(info("Creating dimension $identifier"), false)

        val config = RuntimeWorldConfig()
        val generator: ChunkGenerator =
            if (scs.player.customGenerator != null && propertyMap.containsKey("custom_generator") && propertyMap["custom_generator"] as Boolean) {
                scs.player.customGenerator!!
            } else {
                maplike.chunkManager.chunkGenerator
            }
        config.setDimensionType(maplike.dimension)
        config.generator = generator
        config.seed = scs.world.seed
        config.difficulty = scs.world.difficulty

        if (propertyMap.containsKey("type")) {
            config.setDimensionType(
                scs.server.registryManager.get(Registry.DIMENSION_TYPE_KEY).get(propertyMap["type"] as Identifier)
            )
        }
        if (propertyMap.containsKey("generator")) {
            config.generator =
                scs.server.saveProperties.generatorOptions.dimensions.get(propertyMap["generator"] as Identifier)
                    ?.chunkGenerator ?: maplike.chunkManager.chunkGenerator
        }

        if (propertyMap.containsKey("seed")) {
            config.seed = propertyMap["seed"] as Long
            if (config.generator != null) {
                config.generator = config.generator!!.withSeed(propertyMap["seed"] as Long)
            }
        }

        if (propertyMap.containsKey("difficulty")) {
            when (propertyMap["difficulty"] as String) {
                "peaceful" -> config.difficulty = Difficulty.PEACEFUL
                "easy" -> config.difficulty = Difficulty.EASY
                "normal" -> config.difficulty = Difficulty.NORMAL
                "hard" -> config.difficulty = Difficulty.HARD
            }
        }

        RuntimeWorldManager.add(config, identifier)
        scs.sendFeedback(success("Created new world: $identifier"), true)

        return 1
    }
}