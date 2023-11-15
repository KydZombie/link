package com.github.kydzombie.link.gui

import net.minecraft.container.Chest
import net.minecraft.entity.player.PlayerBase
import net.minecraft.inventory.InventoryBase

class AlternateChestStorage(arg: InventoryBase?, arg2: InventoryBase?) : Chest(arg, arg2) {
    override fun canUse(arg: PlayerBase): Boolean = true
}
