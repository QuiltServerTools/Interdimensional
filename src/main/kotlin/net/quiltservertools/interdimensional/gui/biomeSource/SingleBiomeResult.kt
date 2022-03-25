package net.quiltservertools.interdimensional.gui.biomeSource

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryEntry
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.biome.source.FixedBiomeSource
import net.quiltservertools.interdimensional.gui.elements.BiomeSourceElement
import net.quiltservertools.interdimensional.text

class SingleBiomeResult(private val element: BiomeSourceElement, private val biome: RegistryEntry<Biome>) : BiomeSourceResult(element) {
    override fun getItemStack(): ItemStack {
        return ItemStack(getItem(Biome.getCategory(biome))).setCustomName(element.handler.player.server.registryManager.get(Registry.BIOME_KEY).getId(biome.value())?.path?.text())
    }

    private fun getItem(category: Biome.Category): Item {
        return when (category) {
            Biome.Category.JUNGLE -> Items.JUNGLE_SAPLING
            Biome.Category.MESA -> Items.RED_TERRACOTTA
            Biome.Category.ICY -> Items.PACKED_ICE
            Biome.Category.NETHER -> Items.NETHERRACK
            Biome.Category.MUSHROOM -> Items.RED_MUSHROOM_BLOCK
            Biome.Category.MOUNTAIN -> Items.POWDER_SNOW_BUCKET
            Biome.Category.OCEAN, Biome.Category.RIVER -> Items.WATER_BUCKET
            Biome.Category.SAVANNA -> Items.ACACIA_SAPLING
            Biome.Category.TAIGA -> Items.SPRUCE_SAPLING
            Biome.Category.THEEND -> Items.CHORUS_FLOWER
            Biome.Category.UNDERGROUND -> Items.STONE
            Biome.Category.BEACH -> Items.SAND
            Biome.Category.DESERT -> Items.DEAD_BUSH
            Biome.Category.SWAMP -> Items.CORNFLOWER
            Biome.Category.FOREST -> Items.OAK_SAPLING
            else -> Items.GRASS_BLOCK
        }
    }

    override val biomeSource: BiomeSource = FixedBiomeSource(biome)
}