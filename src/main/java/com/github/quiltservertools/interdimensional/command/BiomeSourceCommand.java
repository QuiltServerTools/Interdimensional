package com.github.quiltservertools.interdimensional.command;

import com.github.quiltservertools.interdimensional.command.argument.BiomeSourceArgumentType;
import com.github.quiltservertools.interdimensional.mixin.ChunkGeneratorAccessor;
import com.github.quiltservertools.interdimensional.duck.ServerPlayerEntityAccess;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.*;
import static com.github.quiltservertools.interdimensional.command.InterdimensionalCommand.*;

import java.util.HashMap;

import static com.github.quiltservertools.interdimensional.command.InterdimensionalCommand.error;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BiomeSourceCommand implements Command {
    //TODO make this for all chunk generator features, not just biome source
    @Override
    public LiteralCommandNode<ServerCommandSource> register() {
        return literal("source")
                .requires(Permissions.require("interdimensional.commands.source", 3))
                    .then(argument("args", StringArgumentType.greedyString()).suggests(BiomeSourceArgumentType.INSTANCE)
                        .executes(ctx -> updateBiomeSource(ctx.getSource(), BiomeSourceArgumentType.INSTANCE.rawProperties(StringArgumentType.getString(ctx, "args")))))
                .build();
    }

    private int updateBiomeSource(ServerCommandSource scs, HashMap<String, Object> propertyMap) throws CommandSyntaxException {
        var generator = ((ServerPlayerEntityAccess) scs.getPlayer()).getCustomGenerator();
        if (generator == null) generator = scs.getWorld().getChunkManager().getChunkGenerator();
        BiomeSource source = generator.getBiomeSource();

        if (propertyMap.containsKey("single_biome")) {
            source = new FixedBiomeSource(scs.getServer().getRegistryManager().get(Registry.BIOME_KEY).get((Identifier) propertyMap.get("single_biome")));
        } else if (propertyMap.containsKey("vanilla_layered")) {
            var largeBiomes = propertyMap.containsKey("large_biomes") && (boolean) propertyMap.get("large_biomes");
            source = new VanillaLayeredBiomeSource((long) propertyMap.get("vanilla_layered"), false, largeBiomes, scs.getRegistryManager().get(Registry.BIOME_KEY));
        } else if (propertyMap.containsKey("multi_noise")) {
            //TODO make this work
            //source = new MultiNoiseBiomeSource((long) propertyMap.get("multi_noise"), scs.getServer().getRegistryManager().get(Registry.BIOME_KEY))
            scs.sendFeedback(error("This option is not supported yet"), false);
        } else if (propertyMap.containsKey("the_end_biome_source")) {
            source = new TheEndBiomeSource(scs.getServer().getRegistryManager().get(Registry.BIOME_KEY), (long) propertyMap.get("the_end_biome_source"));
        } else {
            scs.sendFeedback(error("No biome source option specified"), false);
        }

        //TODO make this not cursed
        scs.sendFeedback(success("Set biome source to " + source.getClass().getName()), false);

        ((ChunkGeneratorAccessor) generator).setBiomeSource(source);

        ((ServerPlayerEntityAccess) scs.getPlayer()).setCustomGenerator(generator);
        return 1;
    }
}
