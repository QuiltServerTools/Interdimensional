package com.github.quiltservertools.interdimensional.command;

import com.github.quiltservertools.interdimensional.command.argument.GeneratorArgumentType;
import com.github.quiltservertools.interdimensional.duck.ServerPlayerEntityAccess;
import com.github.quiltservertools.interdimensional.mixin.ChunkGeneratorAccessor;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.*;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.*;

import static com.github.quiltservertools.interdimensional.command.InterdimensionalCommand.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GeneratorCommand implements Command {
    @Override
    public LiteralCommandNode<ServerCommandSource> register() {
        return literal("generator")
                .requires(Permissions.require("interdimensional.commands.generator", 3))
                .then(argument("args", StringArgumentType.greedyString()).suggests(GeneratorArgumentType.INSTANCE)
                        .executes(ctx -> updateGenerator(ctx.getSource(), GeneratorArgumentType.INSTANCE.rawProperties(StringArgumentType.getString(ctx, "args")))))
                .build();
    }

    private int updateGenerator(ServerCommandSource scs, HashMap<String, Object> propertyMap) throws CommandSyntaxException {
        ChunkGenerator generator = ((ServerPlayerEntityAccess) scs.getPlayer()).getCustomGenerator();
        if (generator == null) generator = scs.getWorld().getChunkManager().getChunkGenerator();
        long seed = scs.getWorld().getSeed();

        if (propertyMap.containsKey("seed")) {
            seed = (long) propertyMap.get("seed");
        }

        // Handle biome sources. Only one is allowed at any one time
        BiomeSource biomeSource = generator.getBiomeSource();
        long biomeSeed = propertyMap.containsKey("biome_seed") ? (long) propertyMap.get("biome_seed") : seed;

        if (propertyMap.containsKey("single_biome")) {
            biomeSource = new FixedBiomeSource(scs.getRegistryManager().get(Registry.BIOME_KEY).get(new Identifier((String) propertyMap.get("single_biome"))));
            scs.sendFeedback(info("Set biome source to Single Biome"), false);

        } else if (propertyMap.containsKey("vanilla_layered")) {
            var largeBiomes = propertyMap.containsKey("large_biomes") && (boolean) propertyMap.get("large_biomes");
            biomeSource = new VanillaLayeredBiomeSource(biomeSeed, false, largeBiomes, scs.getRegistryManager().get(Registry.BIOME_KEY));
            scs.sendFeedback(info("Set biome source to Vanilla Layered"), false);

        } else if (propertyMap.containsKey("multi_noise")) {
            MultiNoiseBiomeSource.method_35242(scs.getRegistryManager().get(Registry.BIOME_KEY), biomeSeed);
            scs.sendFeedback(error("This option is not supported yet"), false);

        } else if (propertyMap.containsKey("the_end_biome_source")) {
            biomeSource = new TheEndBiomeSource(scs.getRegistryManager().get(Registry.BIOME_KEY), biomeSeed);
            scs.sendFeedback(info("Set biome source to End Biome Source"), false);

        } else {
            scs.sendFeedback(info("No biome source option specified, using default"), false);
        }

        ((ChunkGeneratorAccessor) generator).setBiomeSource(biomeSource);

        StructuresConfig structuresConfig;
        // Handle structures
        if (!propertyMap.containsKey("generate_structures") || (boolean) propertyMap.get("generate_structures")) {
            var generateStrongholds = !propertyMap.containsKey("generate_strongholds") || (boolean) propertyMap.get("generate_strongholds");
            if (propertyMap.containsKey("exclude_structures")) {
                structuresConfig = generateStructuresConfig(generateStrongholds, (String) propertyMap.get("exclude_structures"), true);
            } else if (propertyMap.containsKey("include_structures")) {
                structuresConfig = generateStructuresConfig(generateStrongholds, (String) propertyMap.get("include_structures"), false);
            } else {
                structuresConfig = generateStructuresConfig(generateStrongholds, "", true);
            }
            scs.sendFeedback(info("Set structures config to default" + (generateStrongholds ? " with strongholds" : "") + " and overrides"), false);

        } else {
            structuresConfig = new StructuresConfig(Optional.empty(), new HashMap<>());
        }

        ((ChunkGeneratorAccessor) generator).setStructuresConfig(structuresConfig);

        if (!(propertyMap.containsKey("superflat") && (boolean) propertyMap.get("superflat"))) {
            ((ServerPlayerEntityAccess) scs.getPlayer()).setCustomGenerator(generator);
        } else {
            ((ServerPlayerEntityAccess) scs.getPlayer()).setCustomGenerator(new FlatChunkGenerator(new FlatChunkGeneratorConfig(structuresConfig, scs.getRegistryManager().get(Registry.BIOME_KEY))));
        }

        scs.sendFeedback(success("Updated generator configuration"), false);

        return 1;
    }

    private StructuresConfig generateStructuresConfig(boolean generateStrongholds, String list, boolean exclude) {
        Optional<StrongholdConfig> strongholds = generateStrongholds ? Optional.of(StructuresConfig.DEFAULT_STRONGHOLD) : Optional.empty();
        String[] split = list.split(",");
        List<String> strings = new ArrayList<>(Arrays.asList(split));
        Map<StructureFeature<?>, StructureConfig> features = new HashMap<>();
        StructuresConfig.DEFAULT_STRUCTURES.forEach(((structureFeature, structureConfig) -> {
            if ((exclude && !strings.contains(structureFeature.getName())) || (!exclude && strings.contains(structureFeature.getName()))) {
                features.put(structureFeature, structureConfig);
            }
        }));
        return new StructuresConfig(strongholds, features);
    }
}
