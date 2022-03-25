package net.quiltservertools.interdimensional.gui.elements

import net.minecraft.structure.StructureContext
import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.gui.components.ShuffleComponent
import net.quiltservertools.interdimensional.gui.options.StrongholdOptions
import java.util.*

/*class StrongholdEnableComponent(handler: CreateGuiHandler) :
    ShuffleComponent<StrongholdOptions>(handler, StrongholdOptions.values().toMutableList()) {
    override fun setResult() {
        val enable = this.options[this.index].enabled
        val stronghold = if (enable) {
            Optional.of(StructureContext.DEFAULT_STRONGHOLD)
        } else {
            Optional.empty()
        }
        if (handler.structuresConfig.isPresent) {
            handler.structuresConfig = Optional.of(StructuresConfig(stronghold, handler.structuresConfig.get().structures))
        } else {
            handler.structuresConfig = Optional.of(StructuresConfig(stronghold, StructuresConfig.DEFAULT_STRUCTURES))
        }
    }
}*/