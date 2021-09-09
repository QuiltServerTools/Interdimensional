package net.quiltservertools.interdimensional.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.argument.BlockStateArgument
import net.minecraft.command.argument.BlockStateArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.success
import net.quiltservertools.interdimensional.command.argument.PortalOptionsArgumentType
import net.quiltservertools.interdimensional.command.argument.ServerDimensionArgument
import net.quiltservertools.interdimensional.portals.portal.PortalIgnitionSource
import net.quiltservertools.interdimensional.world.Portal
import net.quiltservertools.interdimensional.world.PortalManager

object PortalCommand : Command {
    override fun register(): LiteralCommandNode<ServerCommandSource> {
        return literal("portal")
            .requires(Permissions.require("interdimensional.command.portal", 3))
            .then(
                literal("add").then(
                    argument("name", StringArgumentType.string()).then(
                        ServerDimensionArgument.dimension("destination")
                            .then(
                                argument(
                                    "frame_block",
                                    BlockStateArgumentType.blockState()
                                )
                                    .executes {
                                        return@executes add(
                                            it.source,
                                            StringArgumentType.getString(it, "name"),
                                            ServerDimensionArgument.get(it, "destination").registryKey.value,
                                            BlockStateArgumentType.getBlockState(it, "frame_block"),
                                            ""
                                        )
                                    }
                                    .then(
                                        argument("options", StringArgumentType.greedyString()).suggests(
                                            PortalOptionsArgumentType()
                                        ).executes {
                                            return@executes add(
                                                it.source,
                                                StringArgumentType.getString(it, "name"),
                                                ServerDimensionArgument.get(it, "destination").registryKey.value,
                                                BlockStateArgumentType.getBlockState(it, "frame_block"),
                                                StringArgumentType.getString(it, "options")
                                            )
                                        }
                                    )
                            )
                    )
                ))
            .then(
                literal("remove").requires(Permissions.require("interdimensional.command.portal.delete", 4))
                    .then(argument("name", StringArgumentType.string()).executes {
                        remove(
                            it.source,
                            StringArgumentType.getString(it, "name")
                        )
                    })
            )
            .build()
    }

    private fun add(
        source: ServerCommandSource,
        name: String,
        destination: Identifier,
        blockState: BlockStateArgument,
        properties: String
    ): Int {
        val props = PortalOptionsArgumentType().rawProperties(properties)
        val flat = props.containsKey("flat") && (props["flat"] as Boolean)

        val ignitionSource = PortalIgnitionSource.FIRE


        val sourceWorld = if (props.containsKey("source_world")) {
            props["source_world"] as Identifier
        } else {
            source.server.overworld.registryKey.value
        }

        if (props.containsKey("color")) {
            val color = props["color"] as Formatting
            val red: Int = color.colorIndex shr 16 and 0xFF
            val green: Int = color.colorIndex shr 8 and 0xFF
            val blue: Int = color.colorIndex and 0xFF
                PortalManager.addPortal(
                    Portal(
                        name,
                        blockState.blockState.block,
                        destination,
                        sourceWorld,
                        red.toByte(),
                        green.toByte(),
                        blue.toByte(),
                        flat,
                        ignitionSource
                    )
                )
        } else {
            PortalManager.addPortal(
                Portal(
                    name,
                    blockState.blockState.block,
                    destination,
                    sourceWorld,
                    0,
                    0,
                    0,
                    flat,
                    ignitionSource
                )
            )
        }

        source.sendFeedback(success("Created portal from $sourceWorld to $destination with frame ${blockState.blockState.block}"), false)

        return 1
    }

    private fun remove(source: ServerCommandSource, name: String): Int {
        PortalManager.portals.removeIf { it.name == name }
        source.sendFeedback(success("Removed portal $name"), false)
        return 1
    }
}