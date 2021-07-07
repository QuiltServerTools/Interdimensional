package com.github.quiltservertools.interdimensional;

import com.github.quiltservertools.interdimensional.command.CreateCommand;
import com.github.quiltservertools.interdimensional.command.DeleteCommand;
import com.github.quiltservertools.interdimensional.command.InterdimensionalCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.fantasy.Fantasy;


public class Interdimensional implements DedicatedServerModInitializer {
    public static Fantasy FANTASY;
    public static Logger LOGGER;
    public static Config CONFIG;

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::serverStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::serverStopping);
        LOGGER = LogManager.getLogger();
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void serverStarting(MinecraftServer server) {
        FANTASY = Fantasy.get(server);
        CONFIG = Config.createConfig(server.getSavePath(WorldSavePath.ROOT).resolve("dimensions.json"));
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        var root = InterdimensionalCommand.register(dispatcher);
        dispatcher.getRoot().addChild(root);
        root.addChild(new CreateCommand().register());
        root.addChild(new DeleteCommand().register());
    }

    private void serverStopping(MinecraftServer server) {
        CONFIG.shutdown();
    }
}
