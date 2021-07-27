package com.github.quiltservertools.interdimensional.command;

import com.github.quiltservertools.interdimensional.world.RuntimeWorldManager;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

import static com.github.quiltservertools.interdimensional.command.InterdimensionalCommand.success;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DeleteCommand implements Command {
    @Override
    public LiteralCommandNode<ServerCommandSource> register() {
        return literal("delete")
                .then(argument("dimension", DimensionArgumentType.dimension())
                        .requires(Permissions.require("interdimensional.commands.delete", 4))
                        .executes(ctx -> delete(DimensionArgumentType.getDimensionArgument(ctx, "dimension"), ctx.getSource())))
                .build();
    }

    private int delete(ServerWorld world, ServerCommandSource scs) {
        var id = world.getRegistryKey().getValue();
        RuntimeWorldManager.remove(RuntimeWorldManager.getHandle(id, scs.getServer()));

        scs.sendFeedback(success("Deleted dimension " + id), true);

        return 1;
    }
}
