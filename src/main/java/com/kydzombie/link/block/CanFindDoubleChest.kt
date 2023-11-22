package com.kydzombie.link.block

import net.minecraft.inventory.InventoryBase

interface CanFindDoubleChest {
    fun `link$findInventory`(): InventoryBase?
}
