package com.github.quiltservertools.interdimensional.command

import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.server.command.ServerCommandSource

interface Command {
    fun register(): LiteralCommandNode<ServerCommandSource>
}