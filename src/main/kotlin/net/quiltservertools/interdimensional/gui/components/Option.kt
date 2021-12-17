package net.quiltservertools.interdimensional.gui.components

import net.minecraft.item.ItemStack

interface Option {
    fun getItemStack(): ItemStack
    fun getDisplayName(): String
}