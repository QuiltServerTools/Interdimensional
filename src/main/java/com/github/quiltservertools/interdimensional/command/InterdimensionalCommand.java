package com.github.quiltservertools.interdimensional.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import static net.minecraft.server.command.CommandManager.*;

public class InterdimensionalCommand {
    public static LiteralCommandNode<ServerCommandSource> register(CommandDispatcher<ServerCommandSource> dispatcher) {
        return dispatcher.register(literal("dim").requires(Permissions.require("interdimensional.command.root", 3 )));
    }
}
