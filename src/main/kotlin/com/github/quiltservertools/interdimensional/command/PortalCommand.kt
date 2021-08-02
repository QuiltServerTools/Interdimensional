package com.github.quiltservertools.interdimensional.command

import com.github.quiltservertools.interdimensional.command.InterdimensionalCommand.success
import com.github.quiltservertools.interdimensional.world.Portal
import com.github.quiltservertools.interdimensional.world.PortalManager
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import me.lucko.fabric.api.permissions.v0.Permissions
import net.kyrptonaught.customportalapi.portal.PortalIgnitionSource
import net.minecraft.command.argument.BlockStateArgument
import net.minecraft.command.argument.BlockStateArgumentType
import net.minecraft.command.argument.ColorArgumentType
import net.minecraft.command.argument.DimensionArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

object PortalCommand : Command {
    override fun register(): LiteralCommandNode<ServerCommandSource> {
        return literal("portal")
            .requires(Permissions.require("interdimensional.command.portal", 3))
            .then(
                literal("add").then(
                    argument("name", StringArgumentType.string()).then(
                        argument(
                            "destination",
                            DimensionArgumentType.dimension()
                        ).then(
                            argument("color", ColorArgumentType.color())
                                .then(
                                    argument(
                                        "frame_block",
                                        BlockStateArgumentType.blockState()
                                    ).then(
                                        argument("flat", BoolArgumentType.bool()).executes {
                                            add(
                                                it.source,
                                                StringArgumentType.getString(it, "name"),
                                                DimensionArgumentType.getDimensionArgument(
                                                    it,
                                                    "destination"
                                                ).registryKey.value,
                                                ColorArgumentType.getColor(it, "color"),
                                                BlockStateArgumentType.getBlockState(it, "frame_block"),
                                                BoolArgumentType.getBool(it, "flat")
                                            )
                                        }
                                    )
                                )
                        )
                    )
                )
            )
            .then(literal("remove").requires(Permissions.require("interdimensional.command.portal.delete", 4)).then(argument("name", StringArgumentType.string()).executes {
                remove(
                    it.source,
                    StringArgumentType.getString(it, "name")
                )
            }))
            .build()
    }

    private fun add(
        source: ServerCommandSource,
        name: String,
        destination: Identifier,
        color: Formatting,
        blockState: BlockStateArgument,
        flat: Boolean
    ): Int {
        //TODO add support for custom items
        val ignitionSource = PortalIgnitionSource.FIRE
        val red: Int = color.colorIndex shr 16 and 0xFF
        val green: Int = color.colorIndex shr 8 and 0xFF
        val blue: Int = color.colorIndex and 0xFF

        PortalManager.addPortal(Portal(name, blockState.blockState.block, destination, red.toByte(), green.toByte(), blue.toByte(), flat, ignitionSource))
        source.sendFeedback(success("Created portal to $destination with frame ${blockState.blockState.block}"), false)

        return 1
    }

    private fun remove(source: ServerCommandSource, name: String): Int {
        PortalManager.portals.removeIf{ it.name == name }
        source.sendFeedback(success("Removed portal $name"), false)
        return 1
    }
}