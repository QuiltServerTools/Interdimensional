package net.quiltservertools.interdimensional.gui.elements

import com.ginsberg.cirkle.CircularList
import com.ginsberg.cirkle.circular
import eu.pb4.sgui.api.elements.GuiElementInterface
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.quiltservertools.interdimensional.text

class ClassSelectElement<T>(
    private val name: String,
    private val getClass: () -> T,
    private val setClass: (T) -> Unit,
    private val classes: List<T>,
    private val getName: (T) -> Text,
    private val icons: Map<T, ItemStack> = mapOf()
) : GuiElementInterface {
    override fun getItemStack(): ItemStack = (icons[getClass()]
        ?: Items.STONE.defaultStack).setCustomName("$name: ".text().append(getName(getClass())).styled {
        it.withItalic(false)
    })

    override fun getGuiCallback() =
        GuiElementInterface.ClickCallback { index, type, action, gui ->
            val circularList: CircularList<T> = classes.circular()
            if (type.isLeft) {
                setClass(circularList[circularList.indexOf(getClass()) + 1])
            } else if (type.isRight) {
                setClass(circularList[circularList.indexOf(getClass()) - 1])
            }
        }
}