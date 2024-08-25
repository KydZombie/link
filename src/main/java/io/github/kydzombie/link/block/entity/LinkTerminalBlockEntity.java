package io.github.kydzombie.link.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class LinkTerminalBlockEntity extends BlockEntity implements SimpleInventory {
    ItemStack[] inventory = new ItemStack[6];

    @Override
    public ItemStack[] getStacks() {
        return inventory;
    }

    @Override
    public void setStacks(ItemStack[] stacks) {
        inventory = stacks;
    }

    @Override
    public String getName() {
        return "Link Terminal";
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        readStorageNbt(nbt);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        writeStorageNbt(nbt);
    }
}
