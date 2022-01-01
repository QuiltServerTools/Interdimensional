package net.quiltservertools.interdimensional.gui.elements

import eu.pb4.sgui.api.ClickType
import eu.pb4.sgui.api.elements.GuiElementInterface
import eu.pb4.sgui.api.gui.SimpleGui
import eu.pb4.sgui.api.gui.SlotGuiInterface
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.world.ServerWorld
import net.quiltservertools.interdimensional.gui.DeleteGuiHandler
import net.quiltservertools.interdimensional.gui.components.LinkComponent
import net.quiltservertools.interdimensional.text

class WorldDeleteElement(worlds: Iterable<ServerWorld>, val handler: DeleteGuiHandler) :
    LinkComponent {

    val gui = SimpleGui(ScreenHandlerType.GENERIC_9X3, handler.player, false)
    var handlerSlotIndex = 0

    init {
        worlds.forEach {
            gui.addSlot(WorldSelectorElement.getItem(it), ComponentCallback(it, this))
        }
    }

    override fun getItemStack(): ItemStack {
        var text = "World".text()
        if (handler.identifier != null) {
            text = "World: ${handler.identifier}".text()
        }
        return ItemStack(Items.FILLED_MAP).setCustomName(text)
    }

    override fun createOptions(index: Int) {
        this.handlerSlotIndex = index
        handler.close()
        this.gui.open()
    }

    override fun close() {
        this.gui.close()
        handler.open()
    }

    class ComponentCallback(val world: ServerWorld, private val element: WorldDeleteElement) :
        GuiElementInterface.ClickCallback {
        override fun click(index: Int, type: ClickType?, action: SlotActionType?, gui: SlotGuiInterface) {
            element.handler.identifier = world.registryKey.value
            element.handler.setSlot(element.handlerSlotIndex, element.createElement())
            element.close()
        }
    }
}