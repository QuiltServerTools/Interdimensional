package com.github.quiltservertools.interdimensional.command;

import com.github.quiltservertools.interdimensional.command.argument.DimensionOverrideArgumentType;
import com.github.quiltservertools.interdimensional.mixin.ChunkGeneratorAccessor;
import com.github.quiltservertools.interdimensional.duck.ServerPlayerEntityAccess;
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
import net.minecraft.world.Difficulty;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

import java.util.HashMap;
import java.util.Objects;

import static com.github.quiltservertools.interdimensional.command.InterdimensionalCommand.*;

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
        //FIXME suggestions don't appear unless key is fully typed
        HashMap<String, Object> propertyMap = DimensionOverrideArgumentType.INSTANCE.rawProperties(args);
        var scs = ctx.getSource();
        scs.sendFeedback(info("Creating dimension " + identifier), false);

        RuntimeWorldConfig config = new RuntimeWorldConfig();
        config.setDimensionType(like.getDimension());
        var generator = ((ServerPlayerEntityAccess) scs.getPlayer()).getCustomGenerator() == null ? like.getChunkManager().getChunkGenerator() : ((ServerPlayerEntityAccess) scs.getPlayer()).getCustomGenerator();
        config.setGenerator(generator);
        config.setSeed(scs.getWorld().getSeed());
        config.setDifficulty(scs.getWorld().getDifficulty());

        if (propertyMap.containsKey("type")) {
            config.setDimensionType(((ServerWorld) propertyMap.get("type")).getDimension());
        }

        var biomeSource = ((ServerPlayerEntityAccess) scs.getPlayer()).getCustomGenerator().getBiomeSource();

        if (propertyMap.containsKey("generator")) {
            var overriddenGenerator = (Objects.requireNonNull(scs.getServer().getSaveProperties().getGeneratorOptions().getDimensions().get((Identifier) propertyMap.get("generator")))).getChunkGenerator();
            if (propertyMap.containsKey("custom_biome_source")) {
                if (biomeSource != null) {
                    ((ChunkGeneratorAccessor) overriddenGenerator).setBiomeSource(biomeSource);
                } else {
                    scs.sendFeedback(error("You do not have a custom biome source initialized"), false);
                }
            }
            config.setGenerator(overriddenGenerator);
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
