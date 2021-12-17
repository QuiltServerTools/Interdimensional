package net.quiltservertools.interdimensional.gui.components

import eu.pb4.sgui.api.ClickType
import eu.pb4.sgui.api.elements.GuiElement
import eu.pb4.sgui.api.elements.GuiElementInterface
import eu.pb4.sgui.api.gui.SlotGuiInterface
import net.minecraft.screen.slot.SlotActionType
import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.text

abstract class ShuffleComponent<T : Option>(private val handler: CreateGuiHandler, val options: MutableList<T>)  {

    var index = 0

    init {
        handler.gui.addSlot(createElement(options.first()))
    }

    private fun showNext(slotIndex: Int) {
        if (index >= options.size) {
            index = 0
        }
        handler.gui.setSlot(slotIndex, createElement(options[index]))
    }

    private fun createElement(option: Option): GuiElementInterface {
        return GuiElement(option.getItemStack().setCustomName(option.getDisplayName().text())) {
                slotIndex: Int, type: ClickType?, action: SlotActionType?, gui: SlotGuiInterface ->
            index++
            showNext(slotIndex)
        }
    }

    abstract fun setResult()
}