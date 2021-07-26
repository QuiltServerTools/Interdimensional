package com.github.quiltservertools.interdimensional.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.*;

public class InterdimensionalCommand {

    public static Text error(String message) {
        return new LiteralText("Error: ").formatted(Formatting.RED).append(new LiteralText(message));
    }

    public static Text success(String message) {
        return new LiteralText("Success: ").formatted(Formatting.GREEN).append(new LiteralText(message));
    }

    public static Text info(String message) {
        return new LiteralText("Interdimensional: ").formatted(Formatting.YELLOW).append(new LiteralText(message));
    }

    public static LiteralCommandNode<ServerCommandSource> register(CommandDispatcher<ServerCommandSource> dispatcher) {
        return dispatcher.register(literal("dim").requires(Permissions.require("interdimensional.command.root", 3 )));
    }
}
