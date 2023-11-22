package com.kydzombie.link.gui

import com.kydzombie.link.block.HasLinkInfo
import net.minecraft.entity.player.PlayerBase
import net.minecraft.inventory.InventoryBase
import net.minecraft.item.ItemInstance
import org.lwjgl.util.Color

class FakeChestInventory : HasLinkInfo, InventoryBase {
    var inventory = arrayOfNulls<ItemInstance>(27)
    override fun getLinkName(): String = "Chest"

    override fun setLinkName(name: String) = Unit
    override fun getLinkColor(): Color = Color.WHITE as Color

    override fun setLinkColor(color: Color) = Unit
    override fun openLinkMenu(player: PlayerBase) = Unit
    override fun getInventorySize(): Int = 27

    override fun getInventoryItem(i: Int): ItemInstance? = inventory[i]

    override fun takeInventoryItem(i: Int, j: Int): ItemInstance? {
        return if (inventory[i] != null) {
            val var3: ItemInstance
            if (inventory[i]!!.count <= j) {
                var3 = inventory[i]!!
                inventory[i] = null
                markDirty()
                var3
            } else {
                var3 = inventory[i]!!.split(j)
                if (inventory[i]!!.count == 0) {
                    inventory[i] = null
                }
                markDirty()
                var3
            }
        } else {
            null
        }
    }

    override fun setInventoryItem(i: Int, itemInstance: ItemInstance?) {
        inventory[i] = itemInstance
        if (itemInstance != null && itemInstance.count > this.maxItemCount) {
            itemInstance.count = this.maxItemCount
        }
        markDirty()
    }

    override fun getContainerName(): String = "Chest"

    override fun getMaxItemCount(): Int = 64

    override fun markDirty() = Unit

    override fun canPlayerUse(arg: PlayerBase): Boolean = true
}
