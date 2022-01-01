package net.quiltservertools.interdimensional.gui.elements

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.gui.components.TextComponent
import net.quiltservertools.interdimensional.text

class IdentifierInputGui(val handler: CreateGuiHandler, current: Identifier) : TextComponent.TextInputGui(handler.player) {
    init {
        this.setDefaultInputValue(current.toString())
    }

    override fun onClose() {
        if (this.input.isNotEmpty() && Identifier.isValid(input)) {
            handler.identifier = Identifier(this.input)
            handler.open()
        } else {
            this.open()
        }
    }

    override fun getItemStack(icon: Item, displayName: String): ItemStack {
        return ItemStack(icon).setCustomName("$displayName: ${handler.identifier}".text())
    }
}