package net.quiltservertools.interdimensional.gui.elements

import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.gui.components.ShuffleComponent
import net.quiltservertools.interdimensional.gui.options.DifficultyOption

class DifficultySelectorElement(handler: CreateGuiHandler,
                                options: MutableList<DifficultyOption>
) : ShuffleComponent<DifficultyOption>(handler, options) {
    override fun setResult() {
        handler.difficulty = options[this.index].difficulty
    }
}