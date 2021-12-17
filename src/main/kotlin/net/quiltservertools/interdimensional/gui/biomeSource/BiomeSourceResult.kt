package net.quiltservertools.interdimensional.gui.biomeSource

import eu.pb4.sgui.api.ClickType
import eu.pb4.sgui.api.elements.GuiElementInterface
import eu.pb4.sgui.api.gui.SlotGuiInterface
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.world.biome.source.BiomeSource
import net.quiltservertools.interdimensional.gui.elements.BiomeSourceElement

abstract class BiomeSourceResult(private val element: BiomeSourceElement) : GuiElementInterface, GuiElementInterface.ClickCallback {
    abstract override fun getItemStack(): ItemStack

    override fun click(index: Int, type: ClickType?, action: SlotActionType?, gui: SlotGuiInterface?) {
        element.result = this
        element.setResult(element.handler)
        element.close()
    }
    abstract val biomeSource: BiomeSource
}