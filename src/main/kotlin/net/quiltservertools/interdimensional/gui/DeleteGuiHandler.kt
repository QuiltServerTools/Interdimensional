package net.quiltservertools.interdimensional.gui

import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry
import net.minecraft.world.dimension.DimensionType
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.error
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.success
import net.quiltservertools.interdimensional.gui.components.ActionComponent
import net.quiltservertools.interdimensional.gui.elements.WorldDeleteElement
import net.quiltservertools.interdimensional.text
import net.quiltservertools.interdimensional.world.RuntimeWorldManager
import xyz.nucleoid.fantasy.RemoveFromRegistry

class DeleteGuiHandler(player: ServerPlayerEntity): SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false) {
    var identifier: Identifier? = null

    init {
        addSlot(WorldDeleteElement(player.server.worlds, this).createElement())

        // Bottom row
        setSlot(18, ActionComponent(Items.LIME_CONCRETE, "Submit") { submit() })
        setSlot(26, ActionComponent(Items.RED_CONCRETE, "Close") { close() })

        title = "Delete".text()
        open()
    }

    private fun submit() {
        val identifier = this.identifier
        if (identifier != null) {
            RuntimeWorldManager.remove(RuntimeWorldManager.getHandle(identifier))
            var registry = this.player.server.registryManager.get(Registry.DIMENSION_TYPE_KEY)

            var id = Identifier("interdimensional", identifier.namespace + "/" + identifier.path)
            RemoveFromRegistry.remove(registry as SimpleRegistry<*>, id)

            player.sendMessage("Deleted world: $identifier".success(), false)
        } else {
            player.sendMessage("No world selected for deletion".error(), false)
        }
        close()
    }
}