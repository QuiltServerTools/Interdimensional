package net.quiltservertools.interdimensional.gui.elements

import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.elements.GuiElementInterface
import eu.pb4.sgui.api.gui.AnvilInputGui
import eu.pb4.sgui.api.gui.SlotGuiInterface
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.quiltservertools.interdimensional.text

class LongSelectElement(
    private val name: String,
    private val icon: ItemStack,
    private val parent: SlotGuiInterface,
    private val getLong: () -> Long,
    private val setLong: (Long) -> Any,
) : GuiElementInterface {
    override fun getItemStack(): ItemStack = icon.setCustomName("$name: ${getLong()}".text().styled {
        it.withItalic(false)
    })

    override fun getGuiCallback() =
        GuiElementInterface.ClickCallback { index, type, action, gui ->
            val inputGui = object : AnvilInputGui(gui.player, false) {
                override fun onInput(input: String) {
                    setDefaultInputValue(input.filter { it.isDigit() })
                    setSlot(
                        2,
                        GuiElementBuilder.from(Items.WHEAT_SEEDS.defaultStack).setName(getInput().text())
                            .setCallback { _, _, _ -> this.close() })
                }

                override fun onClose() {
                    input.toLongOrNull()?.let { setLong(it) }
                    parent.open()
                }
            }
            inputGui.setDefaultInputValue(getLong().toString())
            inputGui.title = name.text()
            inputGui.setSlot(
                2,
                GuiElementBuilder.from(Items.WHEAT_SEEDS.defaultStack).setName(inputGui.input.text())
                    .setCallback { _, _, _ -> gui.close() })
            parent.close()
            inputGui.open()
        }
}