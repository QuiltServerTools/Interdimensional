package net.quiltservertools.interdimensional.gui

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.quiltservertools.interdimensional.gui.components.Option

enum class GeneratorTypes(val type: String, private val icon: Item) : Option {
    NOISE("Noise", Items.GRASS_BLOCK),
    FLAT("Flat", Items.LIGHT_GRAY_CONCRETE),
    VOID("Void", Items.ELYTRA);

    override fun getDisplayName() = this.type

    override fun getItemStack() = ItemStack(this.icon)
}