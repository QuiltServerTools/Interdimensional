package net.quiltservertools.interdimensional.command

import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.command.suggestion.SuggestionProviders
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.source.FixedBiomeSource
import net.minecraft.world.biome.source.TheEndBiomeSource
import net.minecraft.world.gen.chunk.*
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.error
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.info
import net.quiltservertools.interdimensional.command.argument.ServerDimensionArgument
import net.quiltservertools.interdimensional.customGenerator
import net.quiltservertools.interdimensional.mixin.ChunkGeneratorAccessor

object GeneratorCommand : Command {
    override fun register(): LiteralCommandNode<ServerCommandSource> {
        return literal("generator")
            .requires(Permissions.require("interdimensional.commands.generator", 3))
            .then(literal("clear")
                .executes { resetGenerator(it, null) }
                .then(ServerDimensionArgument.dimension("default")
                    .executes { resetGenerator(it, ServerDimensionArgument.get(it, "default")) }
                )
            )
            .then(
                literal("biome_seed")
                    .then(argument("biome_seed", LongArgumentType.longArg())
                        .executes { updateBiomeSeed(it, LongArgumentType.getLong(it, "biome_seed")) })
            )
            .then(
                literal("biome_source")
                    .then(literal("multi_noise")
                        .then(ServerDimensionArgument.dimension("multi_noise_like")
                            .executes {
                                addMultiNoise(
                                    it,
                                    ServerDimensionArgument.get(it, "multi_noise_like"),
                                    null
                                )
                            }
                            .then(argument("multi_noise_seed", LongArgumentType.longArg())
                                .executes {
                                    addMultiNoise(
                                        it,
                                        ServerDimensionArgument.get(it, "multi_noise_like"),
                                        LongArgumentType.getLong(it, "multi_noise_like")
                                    )
                                }
                            )
                        )
                    )
                    .then(
                        literal("single_biome")
                            .then(
                                argument(
                                    "biome",
                                    IdentifierArgumentType.identifier()
                                ).suggests(SuggestionProviders.ALL_BIOMES)
                                    .executes {
                                        addSingleBiome(
                                            it,
                                            it.source.registryManager.get(Registry.BIOME_KEY)
                                                .get(IdentifierArgumentType.getIdentifier(it, "biome"))
                                        )
                                    }
                            )
                    )
                    .then(
                        literal("end_biome_source")
                            .then(
                                ServerDimensionArgument.dimension("biomes")
                                    .then(argument("seed", LongArgumentType.longArg())
                                        .executes {
                                            addEndBiomeSource(
                                                it,
                                                ServerDimensionArgument.get(it, "biomes"),
                                                LongArgumentType.getLong(it, "seed")
                                            )
                                        }
                                    )
                            )
                            .then(argument("seed", LongArgumentType.longArg())
                                .executes {
                                    addEndBiomeSource(
                                        it,
                                        null,
                                        LongArgumentType.getLong(it, "seed")
                                    )
                                }
                            )
                    )
            )
            .build()
    }

    private fun getGenerator(scs: ServerCommandSource): ChunkGenerator {
        return scs.player.customGenerator ?: scs.world.chunkManager.chunkGenerator
    }

    private fun setGenerator(generator: ChunkGenerator, scs: ServerCommandSource) {
        scs.player.customGenerator = generator
    }

    private fun updateBiomeSeed(ctx: CommandContext<ServerCommandSource>, seed: Long): Int {
        val scs = ctx.source
        val generator = getGenerator(scs)
        val biomeSource = generator.biomeSource.withSeed(seed)
        (generator as ChunkGeneratorAccessor).setBiomeSource(biomeSource)
        setGenerator(generator, scs)
        scs.sendFeedback("Updated biome source to have seed $seed".info(), false)
        return 0
    }

    private fun addMultiNoise(ctx: CommandContext<ServerCommandSource>, noiseLike: ServerWorld, seed: Long?): Int {
        val scs = ctx.source
        var biomeSource = noiseLike.chunkManager.chunkGenerator.biomeSource
        if (seed != null) {
            biomeSource = biomeSource.withSeed(seed)
        }
        val generator = getGenerator(scs)
        (generator as ChunkGeneratorAccessor).setBiomeSource(biomeSource)
        scs.sendFeedback(
            "Set biome source to multi-noise biomes of ${noiseLike.registryKey.value} with seed ${seed ?: noiseLike.seed}".info(),
            false
        )
        return 0
    }

    private fun addSingleBiome(ctx: CommandContext<ServerCommandSource>, biome: Biome?): Int {
        val scs = ctx.source
        if (biome == null) {
            scs.sendFeedback("No biome provided for single biome source".error(), false)
            return 0
        }
        val biomeSource = FixedBiomeSource(biome)
        (getGenerator(scs) as ChunkGeneratorAccessor).setBiomeSource(biomeSource)
        scs.sendFeedback(
            "Set biome source to single biome of type ${
                scs.registryManager.get(Registry.BIOME_KEY).getId(biome)
            }".info(), false
        )
        return 0
    }

    private fun addEndBiomeSource(
        ctx: CommandContext<ServerCommandSource>,
        biomeWorld: ServerWorld?,
        seed: Long?
    ): Int {
        val scs = ctx.source
        val biomes = if (biomeWorld != null) {
            biomeWorld.registryManager.get(Registry.BIOME_KEY)
        } else {
            scs.world.registryManager.get(Registry.BIOME_KEY)
        }
        val generator = getGenerator(scs)

        val biomeSource = TheEndBiomeSource(biomes, seed ?: scs.world.seed)
        (generator as ChunkGeneratorAccessor).setBiomeSource(biomeSource)
        setGenerator(generator, scs)
        return 0
    }

    private fun resetGenerator(ctx: CommandContext<ServerCommandSource>, defaultWorld: ServerWorld?): Int {
        val scs = ctx.source
        scs.player.customGenerator = if (defaultWorld != null) {
            defaultWorld.chunkManager.chunkGenerator
        } else {
            scs.world.chunkManager.chunkGenerator
        }
        return 0
    }
}