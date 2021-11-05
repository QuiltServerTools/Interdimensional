package net.quiltservertools.interdimensional.world

import net.quiltservertools.interdimensional.portals.portal.PortalIgnitionSource
import net.minecraft.block.Block
import net.minecraft.util.Identifier

data class Portal(val name: String, val frameBlock: Block, val destination: Identifier, val sourceWorld: Identifier, val r: UByte, val g: UByte, val b: UByte, val horizontal: Boolean, val source: PortalIgnitionSource)