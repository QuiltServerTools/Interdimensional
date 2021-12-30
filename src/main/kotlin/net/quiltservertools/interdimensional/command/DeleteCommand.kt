package net.quiltservertools.interdimensional.command

import com.mojang.brigadier.tree.LiteralCommandNode
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.quiltservertools.interdimensional.gui.DeleteGuiHandler

object DeleteCommand : Command {
    override fun register(): LiteralCommandNode<ServerCommandSource> {
        return literal("delete")
            .requires(Permissions.require("interdimensional.commands.delete", 3))
            .executes {
                delete(it.source)
            }
            .build()
    }

    private fun delete(scs: ServerCommandSource): Int {
        DeleteGuiHandler(scs.player)
        return 0
    }
}