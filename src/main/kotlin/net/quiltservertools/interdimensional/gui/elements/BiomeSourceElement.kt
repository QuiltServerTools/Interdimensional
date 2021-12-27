package net.quiltservertools.interdimensional.gui.elements

import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.world.biome.source.MultiNoiseBiomeSource
import net.minecraft.world.biome.source.TheEndBiomeSource
import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.gui.biomeSource.BiomeSourceResult
import net.quiltservertools.interdimensional.gui.biomeSource.EndResult
import net.quiltservertools.interdimensional.gui.biomeSource.MultiNoiseResult
import net.quiltservertools.interdimensional.gui.components.LinkComponent
import net.quiltservertools.interdimensional.text

class BiomeSourceElement(val handler: CreateGuiHandler) : LinkComponent {

    val gui = SimpleGui(ScreenHandlerType.GENERIC_9X3, handler.player, false)
    lateinit var result: BiomeSourceResult

    init {
        handler.player.server.worlds.forEach {
            val source = it.chunkManager.chunkGenerator.biomeSource
            val identifier = it.registryKey.value
            if (source is MultiNoiseBiomeSource) {
                val noiseResult = MultiNoiseResult(identifier.path, this, identifier.equals(Identifier("the_nether")), it)
                gui.addSlot(noiseResult)
            } else if (source is TheEndBiomeSource) {
                gui.addSlot(EndResult(this, it))
            }
        }
    }

    override fun getItemStack(): ItemStack {
        return ItemStack(Items.OAK_SAPLING).setCustomName("Biome Source - ${handler.biomeSource}".text())
    }

    override fun createOptions() {
        handler.close()
        gui.open()
    }

    override fun close() {
        gui.close()
        handler.open()
    }

    override fun setResult(handler: CreateGuiHandler) {
        handler.biomeSource = result.biomeSource
    }
}
