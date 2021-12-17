package net.quiltservertools.interdimensional.gui

import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.BuiltinBiomes
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.gen.chunk.*
import net.quiltservertools.interdimensional.gui.components.ActionComponent
import net.quiltservertools.interdimensional.gui.elements.BiomeSourceElement
import net.quiltservertools.interdimensional.gui.elements.GeneratorTypeElement
import net.quiltservertools.interdimensional.gui.elements.WorldSelectorElement
import net.quiltservertools.interdimensional.mixin.NoiseChunkGeneratorAccessor
import net.quiltservertools.interdimensional.world.RuntimeWorldManager
import xyz.nucleoid.fantasy.RuntimeWorldConfig
import xyz.nucleoid.fantasy.util.VoidChunkGenerator
import java.util.*

class CreateGuiHandler(val player: ServerPlayerEntity) {
    val gui = SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false)
    private val maplikeSelector = WorldSelectorElement(player.server.worlds, this)
    private val biomeSourceSelector = BiomeSourceElement(this)

    lateinit var maplike: ServerWorld
    lateinit var type: GeneratorTypes
    lateinit var biomeSource: BiomeSource
    var seed = player.getWorld().seed

    init {
        val generatorTypes = GeneratorTypes.values().toMutableList()

        gui.addSlot(maplikeSelector.createElement())
        // Add generator type selector
        GeneratorTypeElement(this, generatorTypes)
        // Biome source selector
        gui.addSlot(biomeSourceSelector.createElement())

        // Bottom row
        gui.setSlot(18, ActionComponent(Items.LIME_CONCRETE, "Submit") { submit() })
        gui.setSlot(26, ActionComponent(Items.RED_CONCRETE, "Close") { close() })
        open()
    }

    fun open() {
        gui.open()
    }

    private fun submit() {
        val identifier = Identifier("interdimensional", "")

        val biomeSource = biomeSourceSelector.result.biomeSource
        val structures = StructuresConfig(Optional.of(StructuresConfig.DEFAULT_STRONGHOLD), StructuresConfig.DEFAULT_STRUCTURES)

        val generator = when (type) {
            GeneratorTypes.NOISE -> {
                NoiseChunkGenerator(
                    (maplike.chunkManager.chunkGenerator as NoiseChunkGeneratorAccessor).noiseParameters, biomeSource, seed
                ) { ChunkGeneratorSettings.getInstance() }
            }
            GeneratorTypes.FLAT -> {
                //todo biome logic
                FlatChunkGenerator(FlatChunkGeneratorConfig(structures, player.server.registryManager.get(
                    RegistryKey.ofRegistry(Registry.BIOME_KEY.value))))
            }
            GeneratorTypes.VOID -> {
                VoidChunkGenerator { BuiltinBiomes.THE_VOID }
            }
        }

        val config = RuntimeWorldConfig()
        config.generator = generator
        config.setDimensionType(maplike.dimension)
        config.difficulty = maplike.difficulty
        config.seed = seed

        RuntimeWorldManager.add(config, identifier)

        close()
    }

    fun close() {
        gui.close()
    }
}