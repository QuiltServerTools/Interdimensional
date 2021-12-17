package net.quiltservertools.interdimensional.gui.biomeSource

import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.biome.source.MultiNoiseBiomeSource
import net.quiltservertools.interdimensional.gui.elements.BiomeSourceElement
import net.quiltservertools.interdimensional.text

class MultiNoiseResult(private val displayName: String, private val element: BiomeSourceElement, private val nether: Boolean, private val worldLike: ServerWorld) : BiomeSourceResult(element) {
    override fun getItemStack(): ItemStack {
        return ItemStack(Blocks.FLOWERING_AZALEA.asItem()).setCustomName(("$displayName biome source").text())
    }

    override val biomeSource: BiomeSource
        get() {
            val biomes = worldLike.registryManager.get(Registry.BIOME_KEY)
            return if (nether) {
                MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(biomes).withSeed(element.handler.seed)
            } else {
                MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(biomes).withSeed(element.handler.seed)
            }
        }
}