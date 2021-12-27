package net.quiltservertools.interdimensional.gui.options

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.world.Difficulty
import net.quiltservertools.interdimensional.gui.components.Option

enum class DifficultyOption(val type: String, private val icon: Item, val difficulty: Difficulty) : Option {
    NORMAL("Normal", Items.ORANGE_CONCRETE, Difficulty.NORMAL),
    HARD("Hard", Items.RED_CONCRETE, Difficulty.HARD),
    PEACEFUL("Peaceful", Items.LIME_CONCRETE, Difficulty.PEACEFUL),
    EASY("Easy", Items.YELLOW_CONCRETE, Difficulty.EASY);

    override fun getItemStack() = ItemStack(icon)

    override fun getDisplayName() = type
}