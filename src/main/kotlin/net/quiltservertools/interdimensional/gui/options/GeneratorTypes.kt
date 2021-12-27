package net.quiltservertools.interdimensional.gui.options

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.quiltservertools.interdimensional.gui.components.Option

enum class GeneratorTypes(val type: String, private val icon: Item) : Option {
    NOISE("Chunk Generator - Noise", Items.GRASS_BLOCK),
    FLAT("Chunk Generator - Flat", Items.LIGHT_GRAY_CONCRETE),
    VOID("Chunk Generator - Void", Items.ELYTRA);

    override fun getDisplayName() = this.type

    override fun getItemStack() = ItemStack(this.icon)
}