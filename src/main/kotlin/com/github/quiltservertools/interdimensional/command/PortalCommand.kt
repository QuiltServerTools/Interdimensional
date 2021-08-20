package com.github.quiltservertools.interdimensional.command

/* fixme
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
 */