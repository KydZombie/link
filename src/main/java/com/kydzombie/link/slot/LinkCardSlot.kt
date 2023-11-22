package com.kydzombie.link.slot

import com.kydzombie.link.Link
import net.minecraft.container.slot.Slot
import net.minecraft.inventory.InventoryBase
import net.minecraft.item.ItemInstance

class LinkCardSlot(inventory: InventoryBase, invSlot: Int, x: Int, y: Int) : Slot(inventory, invSlot, x, y) {
    override fun canInsert(itemInstance: ItemInstance): Boolean {
        return itemInstance.type === Link.linkCard && itemInstance.stationNbt.getBoolean("linked")
    }
}
