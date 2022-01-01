package net.quiltservertools.interdimensional.gui.elements

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.gui.components.TextComponent
import net.quiltservertools.interdimensional.text
import org.apache.commons.lang3.StringUtils

class SeedInputGui(val handler: CreateGuiHandler) : TextComponent.TextInputGui(handler.player) {
    override fun getItemStack(icon: Item, displayName: String): ItemStack {
        if (handler.seed != 0L) {
            return ItemStack(icon).setCustomName("$displayName: ${handler.seed}".text())
        }
        return ItemStack(icon).setCustomName(displayName.text())
    }

    override fun onClose() {
        handler.seed = getSeed() ?: handler.player.getWorld().seed
        handler.open()
    }

    private fun getSeed(): Long? {
        val string: String = input
        return if (StringUtils.isEmpty(string)) {
            null
        } else {
            val optionalLong2 = tryParseLong(string)
            if (optionalLong2 != null && optionalLong2 != 0L) {
                optionalLong2
            } else {
                string.hashCode().toLong()
            }
        }
    }

    private fun tryParseLong(string: String): Long? {
        return try {
            string.toLong()
        } catch (e: NumberFormatException) {
            null
        }
    }
}