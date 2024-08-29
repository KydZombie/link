package io.github.kydzombie.link.block.entity;

import io.github.kydzombie.link.Link;
import io.github.kydzombie.link.block.LinkConnectorBlock;
import io.github.kydzombie.link.gui.screen.LinkTerminalScreenHandler;
import io.github.kydzombie.link.item.LinkCardItem;
import io.github.kydzombie.link.network.LinkNetwork;
import io.github.kydzombie.link.packet.UpdateLinkConnectionsPacket;
import io.github.kydzombie.link.util.LinkConnectionInfo;
import net.danygames2014.nyalib.network.NetworkManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class LinkTerminalBlockEntity extends BlockEntity implements SimpleInventory {
    ItemStack[] inventory = new ItemStack[6];
    public LinkConnectionInfo[] connections = new LinkConnectionInfo[0];

    public void updateMachineConnections() {
        if (world == null) return;
        updateMachineConnections(((LinkNetwork) NetworkManager.getAt(world.dimension, x, y, z, Link.linkNetwork.getIdentifier())).getMachines());
    }

    public <T extends BlockEntity & Inventory> void updateMachineConnections(HashSet<BlockPos> networkConnections) {
        System.out.println("Updating Terminal");
        LinkConnectionInfo[] physicalConnections = networkConnections.stream()
                .map((connectionPos) -> {
                    if (world.getBlockState(connectionPos).getBlock() instanceof LinkConnectorBlock linkConnectorBlock) {
                        return linkConnectorBlock.getConnectedInventory(world, connectionPos.x, connectionPos.y, connectionPos.z);
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull)
                .map(blockEntity -> LinkConnectionInfo.fromBlockEntity((T) blockEntity))
                .toArray(LinkConnectionInfo[]::new);
        LinkConnectionInfo[] linkCardConnections = Arrays.stream(inventory)
                .map((stack) -> {
                    if (stack != null && stack.getItem() instanceof LinkCardItem) {
                        return LinkCardItem.getConnectionInfo(stack);
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull)
                .toArray(LinkConnectionInfo[]::new);
        connections = Stream.concat(Arrays.stream(physicalConnections), Arrays.stream(linkCardConnections)).toArray(LinkConnectionInfo[]::new);

        for (PlayerEntity player : (List<PlayerEntity>) world.field_200) {
            if (player.container instanceof LinkTerminalScreenHandler handler) {
                if (handler.blockEntity == this) {
                    sendMachineConnections(player);
                }
            }
        }
    }

    public void sendMachineConnections(PlayerEntity player) {
        PacketHelper.sendTo(player, new UpdateLinkConnectionsPacket(connections));
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

    @Override
    public void markDirty() {
        super.markDirty();
        updateMachineConnections();
    }
}
