package io.github.kydzombie.link.gui.screen.slot;

import io.github.kydzombie.link.item.LinkCardItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class LinkCardSlot extends Slot {
    public LinkCardSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack != null && stack.getItem() instanceof LinkCardItem;
    }
}
