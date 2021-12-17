package net.quiltservertools.interdimensional.gui.elements

import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.gui.GeneratorTypes
import net.quiltservertools.interdimensional.gui.components.ShuffleComponent

class GeneratorTypeElement(private val handler: CreateGuiHandler, options: MutableList<GeneratorTypes>) : ShuffleComponent<GeneratorTypes>(handler, options) {
    override fun setResult() {
        handler.type = options[this.index]
    }
}