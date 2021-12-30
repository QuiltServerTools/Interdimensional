package net.quiltservertools.interdimensional.gui.elements

import eu.pb4.sgui.api.ClickType
import eu.pb4.sgui.api.elements.GuiElementInterface
import eu.pb4.sgui.api.gui.SimpleGui
import eu.pb4.sgui.api.gui.SlotGuiInterface
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.gen.chunk.StructureConfig
import net.minecraft.world.gen.chunk.StructuresConfig
import net.minecraft.world.gen.feature.FeatureConfig
import net.minecraft.world.gen.feature.StructureFeature
import net.quiltservertools.interdimensional.gui.CreateGuiHandler
import net.quiltservertools.interdimensional.gui.components.LinkComponent
import net.quiltservertools.interdimensional.text
import java.util.*
import kotlin.collections.HashMap

class StructureSelectorElement(val handler: CreateGuiHandler) : LinkComponent {

    val gui = SimpleGui(ScreenHandlerType.GENERIC_9X3, handler.player, false)
    private var handlerSlotIndex: Int = 0
    var result: ServerWorld? = handler.maplike

    init {
        handler.player.server.worlds.forEach {
            gui.addSlot(WorldSelectorElement.getItem(it), ComponentCallback(it, this))
        }
        gui.addSlot(ItemStack(Items.RED_CONCRETE).setCustomName("Disabled".text()), ComponentCallback(null, this))
        setResult(handler)
        handler.gui.addSlot(this.createElement())
    }

    override fun getItemStack(): ItemStack {
        return ItemStack(Items.CARTOGRAPHY_TABLE).setCustomName("Structures: ${this.result?.registryKey?.value?.path ?: "Disabled"}".text())
    }

    override fun createOptions(index: Int) {
        handler.close()
        this.handlerSlotIndex = index
        gui.open()
    }

    override fun close() {
        gui.close()
        handler.gui.setSlot(this.handlerSlotIndex, this.createElement())
        handler.open()
    }

    override fun setResult(handler: CreateGuiHandler) {
        var config = handler.structuresConfig
        var strongholdOptional = Optional.of(StructuresConfig.DEFAULT_STRONGHOLD)
        if (config.isPresent) {
            val stronghold = config.get().stronghold
            strongholdOptional = if (stronghold != null) {
                Optional.of(stronghold)
            } else {
                Optional.empty()
            }
        }

        config = Optional.of(
            StructuresConfig(
                strongholdOptional,
                this.result?.chunkManager?.chunkGenerator?.structuresConfig?.structures
                    ?: HashMap<StructureFeature<FeatureConfig>, StructureConfig>().toMutableMap()
            )
        )
        handler.structuresConfig = config
    }

    class ComponentCallback(val world: ServerWorld?, private val component: StructureSelectorElement) :
        GuiElementInterface.ClickCallback {
        override fun click(index: Int, type: ClickType?, action: SlotActionType?, gui: SlotGuiInterface) {
            component.result = world
            component.setResult(component.handler)
            component.close()
        }
    }
}