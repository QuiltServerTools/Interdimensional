package net.quiltservertools.interdimensional.gui.biomeSource

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.source.TheEndBiomeSource
import net.quiltservertools.interdimensional.gui.elements.BiomeSourceElement
import net.quiltservertools.interdimensional.text

class EndResult(element: BiomeSourceElement, world: ServerWorld) : BiomeSourceResult(element) {
    override fun getItemStack(): ItemStack {
        return ItemStack(Items.CHORUS_FLOWER).setCustomName("End biome source".text())
    }

    override val biomeSource = TheEndBiomeSource(world.registryManager.get(Registry.BIOME_KEY), element.handler.seed)
}