package net.quiltservertools.interdimensional.gui.elements

import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeKeys
import net.minecraft.world.biome.source.MultiNoiseBiomeSource
import net.minecraft.world.biome.source.TheEndBiomeSource
import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.gui.biomeSource.BiomeSourceResult
import net.quiltservertools.interdimensional.gui.biomeSource.EndResult
import net.quiltservertools.interdimensional.gui.biomeSource.MultiNoiseResult
import net.quiltservertools.interdimensional.gui.biomeSource.SingleBiomeResult
import net.quiltservertools.interdimensional.gui.components.ActionComponent
import net.quiltservertools.interdimensional.gui.components.LinkComponent
import net.quiltservertools.interdimensional.text

class BiomeSourceElement(val handler: CreateGuiHandler) : LinkComponent {

    val gui = SimpleGui(ScreenHandlerType.GENERIC_9X6, handler.player, false)
    var result: BiomeSourceResult
    var registry: Registry<Biome> = handler.player.server.registryManager.get(Registry.BIOME_KEY)

    init {
        handler.player.server.worlds.forEach {
            // Add biome source used in each of the worlds
            val source = it.chunkManager.chunkGenerator.biomeSource
            val identifier = it.registryKey.value
            if (source is MultiNoiseBiomeSource) {
                val noiseResult =
                    MultiNoiseResult(identifier.path, this, identifier.equals(Identifier("the_nether")), it)
                gui.addSlot(noiseResult)
            } else if (source is TheEndBiomeSource) {
                gui.addSlot(EndResult(this, it))
            }
        }

        result = gui.getSlot(0) as BiomeSourceResult

        gui.setSlot(45, ActionComponent(Items.OAK_SIGN, "Single Biome Source") { BiomeIdentifierInputGui(this).open() })
        gui.setSlot(53, ActionComponent(Items.RED_CONCRETE, "Return") { close() })
    }

    override fun getItemStack(): ItemStack {
        return ItemStack(Items.OAK_SAPLING).setCustomName("Biome Source: ${handler.biomeSource}".text())
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

    private fun getBiome(key: RegistryKey<Biome>): Biome {
        return registry.get(key)!!
    }
}
