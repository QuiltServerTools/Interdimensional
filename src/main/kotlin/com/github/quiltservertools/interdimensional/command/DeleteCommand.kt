package com.github.quiltservertools.interdimensional.command

import com.github.quiltservertools.interdimensional.world.RuntimeWorldManager.getHandle
import com.github.quiltservertools.interdimensional.world.RuntimeWorldManager.remove
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.argument.DimensionArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld

object DeleteCommand : Command {
    override fun register(): LiteralCommandNode<ServerCommandSource> {
        return CommandManager.literal("delete")
            .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                .requires(Permissions.require("interdimensional.commands.delete", 4))
                .executes { ctx: CommandContext<ServerCommandSource> ->
                    delete(
                        DimensionArgumentType.getDimensionArgument(ctx, "dimension"),
                        ctx.source
                    )
                })
            .build()
    }

    private fun delete(world: ServerWorld, scs: ServerCommandSource): Int {
        val id = world.registryKey.value
        remove(getHandle(id)!!)
        scs.sendFeedback(InterdimensionalCommand.success("Deleted dimension $id"), true)
        return 1
    }
}