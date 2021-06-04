package com.github.quiltservertools.interdimensional.command;

import com.github.quiltservertools.interdimensional.world.RuntimeWorldManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class TestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("open").then(argument("world", IdentifierArgumentType.identifier()).executes(ctx -> {
            ctx.getSource().getPlayer().teleport(RuntimeWorldManager.get(IdentifierArgumentType.getIdentifier(ctx, "world"), ctx.getSource().getMinecraftServer()), 0, 96, 0, 0F, 0F);
            return 1;
        })));
    }
}
