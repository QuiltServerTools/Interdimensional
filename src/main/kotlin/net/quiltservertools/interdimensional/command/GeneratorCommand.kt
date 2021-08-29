package net.quiltservertools.interdimensional.command

import net.quiltservertools.interdimensional.command.argument.GeneratorArgumentType
import net.quiltservertools.interdimensional.customGenerator
import net.quiltservertools.interdimensional.mixin.ChunkGeneratorAccessor
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.tree.LiteralCommandNode
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.source.*
import net.minecraft.world.gen.chunk.*
import net.minecraft.world.gen.feature.StructureFeature
import java.util.*

object GeneratorCommand : Command {
    override fun register(): LiteralCommandNode<ServerCommandSource> {
        return CommandManager.literal("generator")
            .requires(Permissions.require("interdimensional.commands.generator", 3))
            .then(CommandManager.argument("args", StringArgumentType.greedyString())
                .suggests(GeneratorArgumentType())
                .executes { ctx: CommandContext<ServerCommandSource> ->
                    updateGenerator(
                        ctx.source,
                        GeneratorArgumentType().rawProperties(StringArgumentType.getString(ctx, "args"))
                    )
                })
            .build()
    }

    @Throws(CommandSyntaxException::class)
    private fun updateGenerator(scs: ServerCommandSource, propertyMap: HashMap<String, Any>): Int {
        var generator = scs.player.customGenerator
        if (generator == null) generator = scs.world.chunkManager.chunkGenerator
        var seed = scs.world.seed
        if (propertyMap.containsKey("seed")) {
            seed = propertyMap["seed"] as Long
        }

        // Handle biome sources. Only one is allowed at any one time
        var biomeSource = generator!!.biomeSource
        val biomeSeed = if (propertyMap.containsKey("biome_seed")) propertyMap["biome_seed"] as Long else seed
        if (propertyMap.containsKey("single_biome")) {
            biomeSource = FixedBiomeSource(
                scs.registryManager.get(Registry.BIOME_KEY)[Identifier(
                    propertyMap["single_biome"] as String?
                )]
            )
            scs.sendFeedback(InterdimensionalCommand.info("Set biome source to Single Biome"), false)
        } else if (propertyMap.containsKey("vanilla_layered")) {
            val largeBiomes = propertyMap.containsKey("large_biomes") && propertyMap["large_biomes"] as Boolean
            biomeSource =
                VanillaLayeredBiomeSource(biomeSeed, false, largeBiomes, scs.registryManager.get(Registry.BIOME_KEY))
            scs.sendFeedback(InterdimensionalCommand.info("Set biome source to Vanilla Layered"), false)
        } else if (propertyMap.containsKey("multi_noise")) {
            MultiNoiseBiomeSource.method_35242(scs.registryManager.get(Registry.BIOME_KEY), biomeSeed)
            scs.sendFeedback(InterdimensionalCommand.error("This option is not supported yet"), false)
        } else if (propertyMap.containsKey("the_end_biome_source")) {
            biomeSource = TheEndBiomeSource(scs.registryManager.get(Registry.BIOME_KEY), biomeSeed)
            scs.sendFeedback(InterdimensionalCommand.info("Set biome source to End Biome Source"), false)
        } else {
            scs.sendFeedback(InterdimensionalCommand.info("No biome source option specified, using default"), false)
        }
        (generator as ChunkGeneratorAccessor).setBiomeSource(biomeSource)
        val structuresConfig: StructuresConfig
        // Handle structures
        if (!propertyMap.containsKey("generate_structures") || propertyMap["generate_structures"] as Boolean) {
            val generateStrongholds =
                !propertyMap.containsKey("generate_strongholds") || propertyMap["generate_strongholds"] as Boolean
            structuresConfig = if (propertyMap.containsKey("exclude_structures")) {
                generateStructuresConfig(generateStrongholds, propertyMap["exclude_structures"] as String?, true)
            } else if (propertyMap.containsKey("include_structures")) {
                generateStructuresConfig(generateStrongholds, propertyMap["include_structures"] as String?, false)
            } else {
                generateStructuresConfig(generateStrongholds, "", true)
            }
            scs.sendFeedback(
                InterdimensionalCommand.info("Set structures config to default" + (if (generateStrongholds) " with strongholds" else "") + " and overrides"),
                false
            )
        } else {
            structuresConfig = StructuresConfig(Optional.empty(), HashMap())
        }
        (generator as ChunkGeneratorAccessor).structuresConfig = structuresConfig
        if (!(propertyMap.containsKey("superflat") && propertyMap["superflat"] as Boolean)) {
            scs.player.customGenerator = generator
        } else {
            scs.player.customGenerator =
                FlatChunkGenerator(
                    FlatChunkGeneratorConfig(
                        structuresConfig,
                        scs.registryManager.get(Registry.BIOME_KEY)
                    )
                )
        }
        scs.sendFeedback(InterdimensionalCommand.success("Updated generator configuration"), false)
        return 1
    }

    private fun generateStructuresConfig(
        generateStrongholds: Boolean,
        list: String?,
        exclude: Boolean
    ): StructuresConfig {
        val strongholds =
            if (generateStrongholds) Optional.of(StructuresConfig.DEFAULT_STRONGHOLD) else Optional.empty()
        val split = list!!.split(",".toRegex()).toTypedArray()
        val strings: List<String> = ArrayList(listOf(*split))
        val features: MutableMap<StructureFeature<*>, StructureConfig> = HashMap()
        StructuresConfig.DEFAULT_STRUCTURES.forEach { (structureFeature: StructureFeature<*>, structureConfig: StructureConfig) ->
            if (exclude && !strings.contains(structureFeature.name) || !exclude && strings.contains(
                    structureFeature.name
                )
            ) {
                features[structureFeature] = structureConfig
            }
        }
        return StructuresConfig(strongholds, features)
    }
}