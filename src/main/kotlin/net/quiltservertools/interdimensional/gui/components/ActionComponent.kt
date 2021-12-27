package net.quiltservertools.interdimensional.gui.components

import eu.pb4.sgui.api.ClickType
import eu.pb4.sgui.api.elements.GuiElementInterface
import eu.pb4.sgui.api.gui.SlotGuiInterface
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType
import net.quiltservertools.interdimensional.text

class ActionComponent(private val item: Item, private val name: String, private val onClick: () -> Unit) : GuiElementInterface, GuiElementInterface.ClickCallback {
    override fun getItemStack(): ItemStack = ItemStack(item).setCustomName(name.text())

    override fun getGuiCallback(): GuiElementInterface.ClickCallback {
        return this
    }

    override fun click(index: Int, type: ClickType?, action: SlotActionType?, gui: SlotGuiInterface?) {
        onClick.invoke()
    }
}