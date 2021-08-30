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
    fun error(message: String): Text {
        return LiteralText("Error: ").formatted(Formatting.RED).append(LiteralText(message))
    }

    fun success(message: String): Text {
        return LiteralText("Success: ").formatted(Formatting.GREEN).append(LiteralText(message))
    }

    fun info(message: String): Text {
        return LiteralText("Interdimensional: ").formatted(Formatting.YELLOW).append(LiteralText(message))
    }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>): LiteralCommandNode<ServerCommandSource> {
        return dispatcher.register(
            CommandManager.literal("dim").requires(Permissions.require("interdimensional.command.root", 3))
        )
    }
}