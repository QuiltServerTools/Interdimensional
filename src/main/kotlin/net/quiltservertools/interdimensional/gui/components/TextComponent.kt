package net.quiltservertools.interdimensional.gui.components

import eu.pb4.sgui.api.ClickType
import eu.pb4.sgui.api.elements.GuiElementInterface
import eu.pb4.sgui.api.gui.AnvilInputGui
import eu.pb4.sgui.api.gui.SlotGuiInterface
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.text

class TextComponent(private val displayName: String, private val icon: Item, private val handler: CreateGuiHandler, private val anvilGui: TextInputGui) :
    GuiElementInterface, GuiElementInterface.ClickCallback {
    override fun getItemStack() = anvilGui.getItemStack(icon, displayName)

    override fun getGuiCallback(): GuiElementInterface.ClickCallback {
        return this
    }

    override fun click(index: Int, type: ClickType?, action: SlotActionType?, currentGui: SlotGuiInterface?) {
        handler.close()
        anvilGui.open()
    }

    abstract class TextInputGui(player: ServerPlayerEntity) : AnvilInputGui(player, false) {
        init {
            this.setSlot(2, ActionComponent(Items.LIME_CONCRETE, "Submit") {
                this.close()
            })
        }
        abstract fun getItemStack(icon: Item, displayName: String): ItemStack
    }
}