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
import org.apache.commons.lang3.RandomStringUtils
import xyz.nucleoid.fantasy.RuntimeWorldConfig
import xyz.nucleoid.fantasy.util.VoidChunkGenerator
import java.util.*

class CreateGuiHandler(player: ServerPlayerEntity) : SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false) {
    private val maplikeSelector = WorldSelectorElement(this.player.server.worlds, this)
    private val biomeSourceSelector = BiomeSourceElement(this)

    var maplike: ServerWorld = this.player.getWorld()
    var genType: GeneratorTypes = GeneratorTypes.NOISE
    var biomeSource: BiomeSource = this.player.getWorld().chunkManager.chunkGenerator.biomeSource
    var seed: Long = this.player.getWorld().seed
    var identifier: Identifier = Identifier(this.player.gameProfile.name.lowercase(), RandomStringUtils.randomNumeric(5))
    var difficulty: Difficulty = Difficulty.NORMAL
    var generatorSettings: ChunkGeneratorSettings =
        BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.get(ChunkGeneratorSettings.OVERWORLD)
            ?: ChunkGeneratorSettings.getInstance()
    var structuresConfig: Optional<StructuresConfig> = Optional.empty()

    init {
        val generatorTypes = GeneratorTypes.values().toMutableList()

        // World info
        addSlot(maplikeSelector.createElement())
        // Identifier
        addSlot(TextComponent("Identifier", Items.WARPED_SIGN, this, IdentifierInputGui(this, identifier)))
        // Difficulty
        DifficultySelectorElement(this, DifficultyOption.values().toMutableList())

        // Generation
        // Seed selector
        addSlot(TextComponent("Seed", Items.WHEAT_SEEDS, this, SeedInputGui(this)))
        // Generator type selector
        GeneratorTypeElement(this, generatorTypes)
        // Biome source selector
        addSlot(biomeSourceSelector.createElement())
        // Generator settings
        ChunkGeneratorSettingsElement(this)

        // Structures config
        StructureSelectorElement(this)
        StrongholdEnableComponent(this)

        // Bottom row
        setSlot(18, ActionComponent(Items.LIME_CONCRETE, "Submit") { submit() })
        setSlot(26, ActionComponent(Items.RED_CONCRETE, "Close") { close() })

        title = "Create".text()
        open()
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun submit() {
        val biomeSource = biomeSourceSelector.result.biomeSource

        val generator = when (genType) {
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
}