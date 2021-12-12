package net.quiltservertools.interdimensional.command

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.success
import net.quiltservertools.interdimensional.command.argument.ServerDimensionArgument
import net.quiltservertools.interdimensional.world.RuntimeWorldManager.getHandle
import net.quiltservertools.interdimensional.world.RuntimeWorldManager.remove

object DeleteCommand : Command {
    override fun register(): LiteralCommandNode<ServerCommandSource> {
        return CommandManager.literal("delete")
            .then(ServerDimensionArgument.dimension("dimension")
                .requires(Permissions.require("interdimensional.commands.delete", 4))
                .executes { ctx: CommandContext<ServerCommandSource> ->
                    delete(
                        ServerDimensionArgument.get(ctx, "dimension"),
                        ctx.source
                    )
                })
            .build()
    }

    private fun delete(world: ServerWorld, scs: ServerCommandSource): Int {
        val id = world.registryKey.value
        remove(getHandle(id))
        scs.sendFeedback("Deleted dimension $id".success(), true)
        return 1
    }
}