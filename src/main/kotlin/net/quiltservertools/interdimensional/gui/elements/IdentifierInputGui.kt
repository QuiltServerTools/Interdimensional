package net.quiltservertools.interdimensional.gui.elements

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.gui.components.TextComponent
import net.quiltservertools.interdimensional.text

class IdentifierInputGui(handler: CreateGuiHandler) : TextComponent.TextInputGui(handler) {
    override fun onClose() {
        if (this.input.isNotEmpty()) {
            handler.identifier = Identifier(this.input)
        }
        handler.open()
    }

    override fun getItemStack(icon: Item, displayName: String): ItemStack {
        return ItemStack(icon).setCustomName("$displayName - ${handler.identifier}".text())
    }
}