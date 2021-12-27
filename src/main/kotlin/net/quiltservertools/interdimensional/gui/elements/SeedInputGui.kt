package net.quiltservertools.interdimensional.gui.elements

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.gui.components.TextComponent
import net.quiltservertools.interdimensional.text

class SeedInputGui(handler: CreateGuiHandler) : TextComponent.TextInputGui(handler) {
    override fun getItemStack(icon: Item, displayName: String): ItemStack {
        if (handler.seed != 0L) {
            return ItemStack(icon).setCustomName("$displayName: ${handler.seed}".text())
        }
        return ItemStack(icon).setCustomName(displayName.text())
    }

    override fun onClose() {
        if (this.input.isNotEmpty()) {
            handler.seed = this.input.toLong()
        } else {
            handler.seed = handler.player.getWorld().seed
        }
        handler.open()
    }
}