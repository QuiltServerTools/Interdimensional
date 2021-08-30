package net.quiltservertools.interdimensional

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.WorldSavePath
import net.quiltservertools.interdimensional.command.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import xyz.nucleoid.fantasy.Fantasy

object Interdimensional : ModInitializer {
    
    lateinit var FANTASY: Fantasy
    val LOGGER: Logger = LogManager.getLogger()
    private lateinit var CONFIG: Config

    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerStarted { server: MinecraftServer ->
            serverStarting(
                server
            )
        })
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerStopping {
            serverStopping()
        })
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _: Boolean ->
            registerCommands(
                dispatcher
            )
        })
    }

    private fun serverStarting(server: MinecraftServer) {
        FANTASY = Fantasy.get(server)
        CONFIG = Config.createConfig(server.getSavePath(WorldSavePath.ROOT).resolve("dimensions.json"))
    }

    private fun registerCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = InterdimensionalCommand.register(dispatcher)
        dispatcher.root.addChild(root)
        root.addChild(CreateCommand.register())
        root.addChild(DeleteCommand.register())
        root.addChild(GeneratorCommand.register())
        root.addChild(PortalCommand.register())
    }

    private fun serverStopping() {
        CONFIG.shutdown()
    }
}