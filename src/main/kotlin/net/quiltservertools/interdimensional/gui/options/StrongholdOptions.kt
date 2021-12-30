package net.quiltservertools.interdimensional.gui.options

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.quiltservertools.interdimensional.gui.components.Option

enum class StrongholdOptions(val enabled: Boolean, private val icon: Item) : Option {
    ENABLED(true, Items.END_PORTAL_FRAME),
    DISABLED(false, Items.CRACKED_STONE_BRICKS);
    override fun getItemStack() = ItemStack(icon)

    override fun getDisplayName() = "Strongholds: $enabled"
}