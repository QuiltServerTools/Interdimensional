package com.github.quiltservertools.interdimensional.world

import net.kyrptonaught.customportalapi.portal.PortalIgnitionSource
import net.minecraft.block.Block
import net.minecraft.util.Identifier

data class Portal(val name: String, val frameBlock: Block, val destination: Identifier, val color: Int, val horizontal: Boolean, val source: PortalIgnitionSource)