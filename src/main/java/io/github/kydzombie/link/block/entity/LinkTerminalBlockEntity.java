package io.github.kydzombie.link.block.entity;

import io.github.kydzombie.link.block.LinkConnectorBlock;
import io.github.kydzombie.link.util.LinkConnectionInfo;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Objects;

public class LinkTerminalBlockEntity extends BlockEntity implements SimpleInventory {
    ItemStack[] inventory = new ItemStack[6];
    public LinkConnectionInfo[] connections = new LinkConnectionInfo[0];

    public <T extends BlockEntity & Inventory> void updateMachineConnections(HashSet<BlockPos> networkConnections) {
        System.out.println("Updating Terminal");
        connections =
                networkConnections.stream()
                        .map((connectionPos) -> {
                            if (world.getBlockState(connectionPos).getBlock() instanceof LinkConnectorBlock linkConnectorBlock) {
                                return linkConnectorBlock.getConnectedInventory(world, connectionPos.x, connectionPos.y, connectionPos.z);
                            } else {
                                return null;
                            }
                        }).filter(Objects::nonNull)
                        .map(blockEntity -> LinkConnectionInfo.fromBlockEntity((T) blockEntity))
                        .toArray(LinkConnectionInfo[]::new);
    }

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
