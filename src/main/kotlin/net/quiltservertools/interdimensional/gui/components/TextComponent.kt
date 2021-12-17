package net.quiltservertools.interdimensional.gui.components

import eu.pb4.sgui.api.elements.GuiElementInterface
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.quiltservertools.interdimensional.text

class TextComponent(val displayName: String, val icon: Item) : GuiElementInterface {
    override fun getItemStack(): ItemStack {
        return ItemStack(icon).setCustomName(displayName.text())
    }
}