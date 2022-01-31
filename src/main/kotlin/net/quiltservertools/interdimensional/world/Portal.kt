package net.quiltservertools.interdimensional.world

import net.minecraft.block.Block
import net.minecraft.util.Identifier
import net.quiltservertools.interdimensional.portals.portal.PortalIgnitionSource

data class Portal(val name: String, val frameBlock: Block, val destination: Identifier, val sourceWorld: Identifier, val r: Int, val g: Int, val b: Int,
                  val horizontal: Boolean, val source: PortalIgnitionSource, val permission: Int)