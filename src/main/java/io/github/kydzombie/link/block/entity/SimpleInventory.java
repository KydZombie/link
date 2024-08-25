package io.github.kydzombie.link.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public interface SimpleInventory extends Inventory {
    ItemStack[] getStacks();

    void setStacks(ItemStack[] stacks);

    @Override
    default int size() {
        return getStacks().length;
    }

    @Override
    default ItemStack getStack(int slot) {
        return getStacks()[slot];
    }

    @Override
    default ItemStack removeStack(int slot, int amount) {
        if (getStack(slot) != null) {
            ItemStack stack;
            if (getStack(slot).count <= amount) {
                stack = getStack(slot);
                setStack(slot, null);
            } else {
                stack = getStack(slot).split(amount);
                if (getStack(slot).count == 0) {
                    setStack(slot, null);
                }
            }
            markDirty();
            return stack;
        } else {
            return null;
        }
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        getStacks()[slot] = stack;
        if (stack != null && stack.count > this.getMaxCountPerStack()) {
            stack.count = this.getMaxCountPerStack();
        }
        this.markDirty();
    }

    @Override
    default int getMaxCountPerStack() {
        return 64;
    }

    default boolean canPlayerUse(PlayerEntity player) {
        if (this instanceof BlockEntity blockEntity) {
            if (blockEntity.world.getBlockEntity(blockEntity.x, blockEntity.y, blockEntity.z) != this) {
                return false;
            } else {
                return !(player.method_1347((double) blockEntity.x + 0.5, (double) blockEntity.y + 0.5, (double) blockEntity.z + 0.5) > 64.0);
            }
        } else {
            return false;
        }
    }

    default void readStorageNbt(NbtCompound nbt) {
        NbtList invNbt = nbt.getList("Items");
        setStacks(new ItemStack[this.size()]);

        for (int var3 = 0; var3 < invNbt.size(); ++var3) {
            NbtCompound stackNbt = (NbtCompound) invNbt.get(var3);
            int slot = stackNbt.getByte("Slot") & 255;
            if (slot >= 0 && slot < size()) {
                setStack(slot, new ItemStack(stackNbt));
            }
        }

    }

    default void writeStorageNbt(NbtCompound nbt) {
        NbtList invNbt = new NbtList();

        for (int x = 0; x < size(); ++x) {
            if (getStack(x) != null) {
                NbtCompound stackNbt = new NbtCompound();
                stackNbt.putByte("Slot", (byte) x);
                getStack(x).writeNbt(stackNbt);
                invNbt.add(stackNbt);
            }
        }

        nbt.put("Items", invNbt);
    }
}
