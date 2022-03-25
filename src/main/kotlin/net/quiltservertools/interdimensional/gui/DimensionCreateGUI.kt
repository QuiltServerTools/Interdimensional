package com.github.quiltservertools.interdimensional.gui

import eu.pb4.sgui.api.ClickType
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.Difficulty
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.gen.GeneratorOptions
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.success
import net.quiltservertools.interdimensional.gui.elements.ClassSelectElement
import net.quiltservertools.interdimensional.gui.elements.EnumSelectElement
import net.quiltservertools.interdimensional.gui.elements.IdentifierSelectElement
import net.quiltservertools.interdimensional.gui.elements.LongSelectElement
import net.quiltservertools.interdimensional.text
import net.quiltservertools.interdimensional.world.RuntimeWorldManager
import org.apache.commons.lang3.RandomStringUtils
import xyz.nucleoid.fantasy.RuntimeWorldConfig

class DimensionCreateGUI(player: ServerPlayerEntity) : SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false) {
    private val dimConfig = RuntimeWorldConfig().apply {
        seed = player.server.overworld.seed
        setDimensionType(DimensionType.OVERWORLD_REGISTRY_KEY)
    }
    var identifier = Identifier(this.player.gameProfile.name.lowercase(), RandomStringUtils.randomNumeric(5))
    var genSettings: ChunkGeneratorSettings = player.server.registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY)
        .getEntry(ChunkGeneratorSettings.OVERWORLD).get().value()

    private val dimRegistry = player.server.registryManager.get(Registry.DIMENSION_TYPE_KEY)
    private val genRegistry = player.server.registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY)

    override fun onUpdate(firstUpdate: Boolean) {
        this.setSlot(
            0,
            ClassSelectElement(
                "Dimension Type",
                { dimConfig.createDimensionOptions(player.server).dimensionTypeSupplier.value() },
                dimConfig::setDimensionType,
                dimRegistry.stream().toList(),
                { dimensionType -> dimRegistry.getId(dimensionType).toString().text() },
                mapOf(
                    dimRegistry.get(DimensionType.OVERWORLD_REGISTRY_KEY) to Items.GRASS_BLOCK.defaultStack,
                    dimRegistry.get(DimensionType.THE_NETHER_REGISTRY_KEY) to Items.NETHERRACK.defaultStack,
                    dimRegistry.get(DimensionType.THE_END_REGISTRY_KEY) to Items.END_STONE.defaultStack,
                    dimRegistry.get(DimensionType.OVERWORLD_CAVES_REGISTRY_KEY) to Items.STONE.defaultStack,
                )
            )
        )

        this.setSlot(
            1,
            IdentifierSelectElement(
                "Identifier",
                Items.WARPED_SIGN.defaultStack,
                this,
                this::identifier.getter,
                this::identifier.setter
            )
        )

        this.setSlot(
            2,
            EnumSelectElement<Difficulty>(
                "Difficulty",
                dimConfig::getDifficulty,
                dimConfig::setDifficulty,
                mapOf(
                    Difficulty.EASY to Items.GREEN_CONCRETE,
                    Difficulty.NORMAL to Items.ORANGE_CONCRETE,
                    Difficulty.HARD to Items.RED_CONCRETE,
                    Difficulty.PEACEFUL to Items.BLUE_CONCRETE
                )
            )
        )

        this.setSlot(
            3,
            LongSelectElement(
                "Seed",
                Items.WHEAT_SEEDS.defaultStack,
                this,
                dimConfig::getSeed,
                dimConfig::setSeed
            )
        )

        this.setSlot(
            4,
            ClassSelectElement(
                "Chunk Generator",
                this::genSettings.getter,
                this::genSettings.setter,
                genRegistry.stream().toList(),
                { genSettings -> genRegistry.getId(genSettings).toString().text() },
                mapOf(
                    genRegistry.get(ChunkGeneratorSettings.OVERWORLD)!! to Items.GRASS_BLOCK.defaultStack,
                    genRegistry.get(ChunkGeneratorSettings.NETHER)!! to Items.NETHERRACK.defaultStack,
                    genRegistry.get(ChunkGeneratorSettings.END)!! to Items.END_STONE.defaultStack,
                    genRegistry.get(ChunkGeneratorSettings.CAVES)!! to Items.STONE.defaultStack,
                    genRegistry.get(ChunkGeneratorSettings.AMPLIFIED)!! to Items.SNOW_BLOCK.defaultStack,
                    genRegistry.get(ChunkGeneratorSettings.FLOATING_ISLANDS)!! to Items.ELYTRA.defaultStack,
                    genRegistry.get(ChunkGeneratorSettings.LARGE_BIOMES)!! to Items.SAND.defaultStack,
                )
            )
        )

        this.setSlot(
            26,
            GuiElementBuilder()
                .setItem(Items.GREEN_CONCRETE)
                .setName("Create".text())
                .setCallback(this::create)
        )
    }

    private fun create(index: Int, type: ClickType, action: SlotActionType) {
        //val biomeSource = biomeSourceSelector.result.biomeSource

        val config = RuntimeWorldConfig()

        val generator = GeneratorOptions.createGenerator(
            player.server.registryManager,
            config.seed,
            genRegistry.getKey(genSettings).get()
        )
        config.generator = generator

        RuntimeWorldManager.add(config, identifier)

        player.sendMessage("Created dimension $identifier".success(), false)

        close()
    }
}