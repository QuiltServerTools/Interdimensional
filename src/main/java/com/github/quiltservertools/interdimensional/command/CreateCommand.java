package com.github.quiltservertools.interdimensional.command;

import com.github.quiltservertools.interdimensional.command.argument.DimensionOverrideArgumentType;
import com.github.quiltservertools.interdimensional.command.argument.GeneratorArgumentType;
import com.github.quiltservertools.interdimensional.duck.ServerPlayerEntityAccess;
import com.github.quiltservertools.interdimensional.world.RuntimeWorldManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

import java.util.HashMap;
import java.util.Objects;

import static com.github.quiltservertools.interdimensional.command.InterdimensionalCommand.info;
import static com.github.quiltservertools.interdimensional.command.InterdimensionalCommand.success;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CreateCommand implements Command {
    @Override
    public LiteralCommandNode<ServerCommandSource> register() {
        return literal("create")
                .requires(Permissions.require("interdimensional.command.create", 3))
                .then(argument("identifier", IdentifierArgumentType.identifier())
                        .then(argument("maplike", DimensionArgumentType.dimension())
                                .executes(ctx -> run(DimensionArgumentType.getDimensionArgument(ctx, "maplike"), "", IdentifierArgumentType.getIdentifier(ctx, "identifier"), ctx))
                                .then(argument("overrides", StringArgumentType.greedyString()).suggests(DimensionOverrideArgumentType.INSTANCE)
                                        .executes(ctx -> run(DimensionArgumentType.getDimensionArgument(ctx, "maplike"), StringArgumentType.getString(ctx, "overrides"), IdentifierArgumentType.getIdentifier(ctx, "identifier"), ctx)))))
                .build();
    }

    private int run(ServerWorld like, String args, Identifier identifier, CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        HashMap<String, Object> propertyMap = DimensionOverrideArgumentType.INSTANCE.rawProperties(args);
        var scs = ctx.getSource();
        scs.sendFeedback(info("Creating dimension " + identifier), false);

        RuntimeWorldConfig config = new RuntimeWorldConfig();
        config.setDimensionType(like.getDimension());
        var generator = ((ServerPlayerEntityAccess) scs.getPlayer()).getCustomGenerator() == null || !((boolean) propertyMap.get("custom_generator")) ? like.getChunkManager().getChunkGenerator() : ((ServerPlayerEntityAccess) scs.getPlayer()).getCustomGenerator();
        config.setGenerator(generator);
        config.setSeed(scs.getWorld().getSeed());
        config.setDifficulty(scs.getWorld().getDifficulty());

        if (propertyMap.containsKey("type")) {
            config.setDimensionType(scs.getServer().getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).get((Identifier) propertyMap.get("type")));
        }
        if (propertyMap.containsKey("generator")) {
            config.setGenerator(Objects.requireNonNull(scs.getServer().getSaveProperties().getGeneratorOptions().getDimensions().get((Identifier) propertyMap.get("generator"))).getChunkGenerator());
        }

        if (propertyMap.containsKey("seed")) {
            config.setSeed((long) propertyMap.get("seed"));
            if (config.getGenerator() != null) {
                config.setGenerator(config.getGenerator().withSeed((long) propertyMap.get("seed")));
            }
        }

        if (propertyMap.containsKey("difficulty")) {
            String difficulty = (String) propertyMap.get("difficulty");
            switch (difficulty) {
                case "peaceful" -> config.setDifficulty(Difficulty.PEACEFUL);
                case "easy" -> config.setDifficulty(Difficulty.EASY);
                case "normal" -> config.setDifficulty(Difficulty.NORMAL);
                case "hard" -> config.setDifficulty(Difficulty.HARD);
            }
        }

        RuntimeWorldManager.add(config, identifier);
        scs.sendFeedback(success("Created new world: " + identifier), true);

        return 1;
    }
}
