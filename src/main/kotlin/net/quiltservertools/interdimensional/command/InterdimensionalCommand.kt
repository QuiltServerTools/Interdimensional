package net.quiltservertools.interdimensional.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.LiteralCommandNode
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object InterdimensionalCommand {
    fun String.error(): Text {
        return LiteralText("Error: ").formatted(Formatting.RED).append(LiteralText(this))
    }

    fun String.success(): Text {
        return LiteralText("Success: ").formatted(Formatting.GREEN).append(LiteralText(this))
    }

    fun String.info(): Text {
        return LiteralText("Interdimensional: ").formatted(Formatting.YELLOW).append(LiteralText(this))
    }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>): LiteralCommandNode<ServerCommandSource> {
        return dispatcher.register(
            CommandManager.literal("dim").requires(Permissions.require("interdimensional.command.root", 3))
        )
    }
}