package com.github.quiltservertools.interdimensional.command;

import com.github.quiltservertools.interdimensional.command.argument.DimensionOverrideArgumentType;
import com.github.quiltservertools.interdimensional.world.RuntimeWorldManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

import java.util.HashMap;
import java.util.Objects;

public class CreateCommand implements Command {
    @Override
    public LiteralCommandNode<ServerCommandSource> register() {
        return CommandManager.literal("create")
                .requires(Permissions.require("interdimensional.command.create", 3))
                .then(CommandManager.argument("identifier", IdentifierArgumentType.identifier())
                .then(CommandManager.argument("maplike", DimensionArgumentType.dimension())
                        .then(CommandManager.argument("overrides", StringArgumentType.greedyString()).suggests(DimensionOverrideArgumentType.INSTANCE)
                                .executes(ctx -> run(DimensionArgumentType.getDimensionArgument(ctx, "maplike"), StringArgumentType.getString(ctx, "overrides"), IdentifierArgumentType.getIdentifier(ctx, "identifier"), ctx)))))
                .build();
    }

    private int run(ServerWorld like, String args, Identifier identifier, CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        HashMap<String, Object> propertyMap = DimensionOverrideArgumentType.INSTANCE.rawProperties(args);
        var scs = ctx.getSource();

        RuntimeWorldConfig config = new RuntimeWorldConfig();
        config.setDimensionType(like.getDimension());
        config.setGenerator(like.getChunkManager().getChunkGenerator());
        config.setSeed(scs.getWorld().getSeed());
        config.setDifficulty(scs.getWorld().getDifficulty());

        if (propertyMap.containsKey("seed")) {
            config.setSeed((long) propertyMap.get("seed"));
        }

        if (propertyMap.containsKey("type")) {
            config.setDimensionType(((ServerWorld) propertyMap.get("type")).getDimension());
        }

        if (propertyMap.containsKey("generator")) {
            config.setGenerator((Objects.requireNonNull(scs.getMinecraftServer().getSaveProperties().getGeneratorOptions().getDimensions().get((Identifier) propertyMap.get("generator")))).getChunkGenerator());
        }

        RuntimeWorldManager.add(config, identifier);

        return 1;
    }
}
