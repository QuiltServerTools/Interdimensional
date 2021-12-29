package net.quiltservertools.interdimensional.gui.elements

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.BuiltinBiomes
import net.quiltservertools.interdimensional.gui.biomeSource.SingleBiomeResult
import net.quiltservertools.interdimensional.gui.components.TextComponent

class BiomeIdentifierInputGui(private val element: BiomeSourceElement) : TextComponent.TextInputGui(element.handler.player) {
    override fun getItemStack(icon: Item, displayName: String): ItemStack {
        return ItemStack(icon).setCustomName(element.result.itemStack.name)
    }

    override fun close() {
        element.result = SingleBiomeResult(element, element.handler.player.server.registryManager.get(Registry.BIOME_KEY).get(
            Identifier(this.input)
        )?: BuiltinBiomes.PLAINS)
        super.close()
        element.handler.open()
    }
}