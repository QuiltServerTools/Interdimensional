package net.quiltservertools.interdimensional.world

import net.quiltservertools.interdimensional.portals.portal.PortalIgnitionSource
import net.minecraft.block.Block
import net.minecraft.util.Identifier

data class Portal(val name: String, val frameBlock: Block, val destination: Identifier, val sourceWorld: Identifier, val r: Byte, val g: Byte, val b: Byte, val horizontal: Boolean, val source: PortalIgnitionSource)