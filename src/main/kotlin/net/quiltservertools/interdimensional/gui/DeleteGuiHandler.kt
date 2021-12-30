package net.quiltservertools.interdimensional.gui

import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.error
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.success
import net.quiltservertools.interdimensional.gui.components.ActionComponent
import net.quiltservertools.interdimensional.gui.elements.WorldDeleteElement
import net.quiltservertools.interdimensional.text
import net.quiltservertools.interdimensional.world.RuntimeWorldManager

class DeleteGuiHandler(val player: ServerPlayerEntity) {

    val gui = SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false)
    var identifier: Identifier? = null

    init {
        gui.addSlot(WorldDeleteElement(player.server.worlds, this).createElement())

        // Bottom row
        gui.setSlot(18, ActionComponent(Items.LIME_CONCRETE, "Submit") { submit() })
        gui.setSlot(26, ActionComponent(Items.RED_CONCRETE, "Close") { close() })

        gui.title = "Delete".text()
        open()
    }
    fun open() {
        gui.open()
    }
    fun close() {
        gui.close()
    }

    private fun submit() {
        val identifier = this.identifier
        if (identifier != null) {
            RuntimeWorldManager.remove(RuntimeWorldManager.getHandle(identifier))
            player.sendMessage("Deleted world: $identifier".success(), false)
        } else {
            player.sendMessage("No world selected for deletion".error(), false)
        }
        close()
    }
}