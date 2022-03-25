package net.quiltservertools.interdimensional.gui.elements

import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings
import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.gui.components.ShuffleComponent
import net.quiltservertools.interdimensional.gui.options.ChunkGeneratorSettingsOptions

class ChunkGeneratorSettingsElement(handler: CreateGuiHandler) : ShuffleComponent<ChunkGeneratorSettingsOptions>(handler, ChunkGeneratorSettingsOptions.values().toMutableList()) {
    override fun setResult() {
        handler.generatorSettings =
                BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getEntry(this.options[this.index].value).orElse(ChunkGeneratorSettings.getInstance())
    }
}