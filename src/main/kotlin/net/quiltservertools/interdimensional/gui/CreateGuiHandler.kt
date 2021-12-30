package net.quiltservertools.interdimensional.gui

import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.Difficulty
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.gen.chunk.*
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.success
import net.quiltservertools.interdimensional.gui.components.ActionComponent
import net.quiltservertools.interdimensional.gui.components.TextComponent
import net.quiltservertools.interdimensional.gui.elements.*
import net.quiltservertools.interdimensional.gui.options.DifficultyOption
import net.quiltservertools.interdimensional.gui.options.GeneratorTypes
import net.quiltservertools.interdimensional.mixin.ChunkGeneratorSettingsAccessor
import net.quiltservertools.interdimensional.mixin.NoiseChunkGeneratorAccessor
import net.quiltservertools.interdimensional.text
import net.quiltservertools.interdimensional.world.RuntimeWorldManager
import xyz.nucleoid.fantasy.RuntimeWorldConfig
import xyz.nucleoid.fantasy.util.VoidChunkGenerator
import java.util.*

class CreateGuiHandler(val player: ServerPlayerEntity) {
    val gui = SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false)
    private val maplikeSelector = WorldSelectorElement(player.server.worlds, this)
    private val biomeSourceSelector = BiomeSourceElement(this)

    var maplike: ServerWorld = player.getWorld()
    var type: GeneratorTypes = GeneratorTypes.NOISE
    var biomeSource: BiomeSource = player.getWorld().chunkManager.chunkGenerator.biomeSource
    var seed: Long = player.getWorld().seed
    var identifier: Identifier = Identifier(player.gameProfile.name)
    var difficulty: Difficulty = Difficulty.NORMAL
    var generatorSettings: ChunkGeneratorSettings =
        BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.get(ChunkGeneratorSettings.OVERWORLD)
            ?: ChunkGeneratorSettings.getInstance()
    var structuresConfig: Optional<StructuresConfig> = Optional.empty()

    init {
        val generatorTypes = GeneratorTypes.values().toMutableList()

        // World info
        gui.addSlot(maplikeSelector.createElement())
        // Identifier
        gui.addSlot(TextComponent("Identifier", Items.WARPED_SIGN, this, IdentifierInputGui(this)))
        // Difficulty
        DifficultySelectorElement(this, DifficultyOption.values().toMutableList())

        // Generation
        // Seed selector
        gui.addSlot(TextComponent("Seed", Items.WHEAT_SEEDS, this, SeedInputGui(this)))
        // Generator type selector
        GeneratorTypeElement(this, generatorTypes)
        // Biome source selector
        gui.addSlot(biomeSourceSelector.createElement())
        // Generator settings
        ChunkGeneratorSettingsElement(this)

        // Structures config
        StructureSelectorElement(this)
        StrongholdEnableComponent(this)

        // Bottom row
        gui.setSlot(18, ActionComponent(Items.LIME_CONCRETE, "Submit") { submit() })
        gui.setSlot(26, ActionComponent(Items.RED_CONCRETE, "Close") { close() })

        gui.title = "Create".text()
        open()
    }

    fun open() {
        gui.open()
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun submit() {
        val biomeSource = biomeSourceSelector.result.biomeSource

        val generator = when (type) {
            GeneratorTypes.NOISE -> {
                if (structuresConfig.isPresent) {
                    (generatorSettings as ChunkGeneratorSettingsAccessor).setStructuresConfig(structuresConfig.get())
                }
                NoiseChunkGenerator(
                    (maplike.chunkManager.chunkGenerator as NoiseChunkGeneratorAccessor).noiseParameters,
                    biomeSource,
                    seed
                ) { generatorSettings }
            }
            GeneratorTypes.FLAT -> {
                val config = FlatChunkGeneratorConfig.getDefaultConfig(player.server.registryManager.get(Registry.BIOME_KEY))
                if (structuresConfig.isPresent) {
                    config.withStructuresConfig(structuresConfig.get())
                }
                config.setBiome {
                    return@setBiome biomeSource.biomes.first()
                }
                FlatChunkGenerator(config)
            }
            GeneratorTypes.VOID -> {
                VoidChunkGenerator(player.server.registryManager.get(Registry.BIOME_KEY))
            }
        }

        val config = RuntimeWorldConfig()
        config.generator = generator
        config.setDimensionType(maplike.dimension)
        config.difficulty = maplike.difficulty
        config.seed = seed

        RuntimeWorldManager.add(config, identifier)

        player.sendMessage("Created dimension $identifier".success(), false)

        close()
    }

    fun close() {
        gui.close()
    }
}