package com.kydzombie.link.gui

import com.kydzombie.link.block.LinkTerminalEntity
import com.kydzombie.link.slot.LinkCardSlot
import net.minecraft.container.ContainerBase
import net.minecraft.container.slot.Slot
import net.minecraft.entity.player.PlayerBase

class LinkTerminalStorage(player: PlayerBase, entity: LinkTerminalEntity) : ContainerBase() {
    init {
        addSlot(LinkCardSlot(entity, 0, 10000, 11))
        addSlot(LinkCardSlot(entity, 1, 10000, 31))
        addSlot(LinkCardSlot(entity, 2, 10000, 51))
        addSlot(LinkCardSlot(entity, 3, 10000, 71))
        addSlot(LinkCardSlot(entity, 4, 10000, 91))
        addSlot(LinkCardSlot(entity, 5, 10000, 111))
        val offset = 2 * 18 + 1
        val playerInventory = player.inventory
        for (row in 0..2) {
            for (column in 0..8) {
                addSlot(Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 103 + row * 18 + offset))
            }
        }
        for (row in 0..8) {
            addSlot(Slot(playerInventory, row, 8 + row * 18, 161 + offset))
        }
    }

    override fun canUse(arg: PlayerBase): Boolean {
        return true
    }

    companion object {
        const val LINK_CARD_X = 149
    }
}
