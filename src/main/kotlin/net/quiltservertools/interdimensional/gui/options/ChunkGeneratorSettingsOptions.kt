package net.quiltservertools.interdimensional.gui.options

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings
import net.quiltservertools.interdimensional.gui.components.Option

enum class ChunkGeneratorSettingsOptions(val value: RegistryKey<ChunkGeneratorSettings>, val item: Item, private val displayedString: String) : Option {
    OVERWORLD(ChunkGeneratorSettings.OVERWORLD, Items.GRASS, "Overworld"),
    AMPLIFIED(ChunkGeneratorSettings.AMPLIFIED, Items.TALL_GRASS, "Amplified"),
    LARGE_BIOMES(ChunkGeneratorSettings.LARGE_BIOMES, Items.GRASS_BLOCK, "Large Biomes"),
    NETHER(ChunkGeneratorSettings.NETHER, Items.NETHERRACK, "Nether"),
    END(ChunkGeneratorSettings.END, Items.END_STONE, "End"),
    CAVES(ChunkGeneratorSettings.CAVES, Items.STONE, "Caves"),
    ISLANDS(ChunkGeneratorSettings.FLOATING_ISLANDS, Items.ELYTRA, "Floating Islands");

    override fun getItemStack(): ItemStack {
        return ItemStack(item)
    }

    override fun getDisplayName(): String {
        return "Generator settings: $displayedString"
    }
}