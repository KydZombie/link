package com.github.kydzombie.link.slot;

import com.github.kydzombie.link.Link;
import net.minecraft.container.slot.Slot;
import net.minecraft.inventory.InventoryBase;
import net.minecraft.item.ItemInstance;

public class LinkCardSlot extends Slot {
    public LinkCardSlot(InventoryBase inventory, int invSlot, int x, int y) {
        super(inventory, invSlot, x, y);
    }

    @Override
    public boolean canInsert(ItemInstance itemInstance) {
        return itemInstance != null && itemInstance.getType() == Link.LINK_CARD && itemInstance.getStationNBT().getBoolean("linked");
    }
}
