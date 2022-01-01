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
import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.gui.components.LinkComponent
import net.quiltservertools.interdimensional.text

class WorldSelectorElement(worlds: Iterable<ServerWorld>, private val handler: CreateGuiHandler) :
    LinkComponent {

    private val gui = SimpleGui(ScreenHandlerType.GENERIC_9X1, handler.player, false)
    private var result: ServerWorld? = null
    private var handlerSlotIndex: Int = 0

    init {
        worlds.forEach {
            gui.addSlot(
                getItem(it),
                ComponentCallback(it, this)
            )
        }
    }

    override fun getItemStack(): ItemStack {
        return ItemStack(Items.MAP).setCustomName("World like: ${handler.maplike.registryKey.value.path}".text())
    }

    override fun createOptions(index: Int) {
        handler.close()
        this.handlerSlotIndex = index
        gui.open()
    }

    override fun setResult(handler: CreateGuiHandler) {
        handler.maplike = this.result ?: handler.player.getWorld()
        handler.setSlot(handlerSlotIndex, this.createElement())
    }

    override fun close() {
        gui.close()
        handler.open()
    }

    class ComponentCallback(val world: ServerWorld, private val component: WorldSelectorElement) :
        GuiElementInterface.ClickCallback {
        override fun click(index: Int, type: ClickType?, action: SlotActionType?, gui: SlotGuiInterface) {
            component.result = world
            component.setResult(component.handler)
            component.close()
        }
    }

    companion object {
        fun getItem(world: ServerWorld?): ItemStack {
            val path = world?.registryKey?.value?.path ?: ""
            val stack = when (path) {
                "overworld" -> {
                    ItemStack(Items.STONE)
                }
                "the_nether" -> {
                    ItemStack(Items.NETHERRACK)
                }
                "the_end" -> {
                    ItemStack(Items.END_STONE)
                }
                else -> {
                    ItemStack(Items.FILLED_MAP)
                }
            }

            return stack.setCustomName(path.text())
        }
    }
}