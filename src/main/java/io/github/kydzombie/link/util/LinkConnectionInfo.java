package io.github.kydzombie.link.util;

import io.github.kydzombie.link.item.LinkCardItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public record LinkConnectionInfo(String name, int color, LinkCardItem.LinkStatus status, int x, int y, int z) {
    private static final Random RANDOM = new Random();

    public static <T extends BlockEntity & Inventory> LinkConnectionInfo fromBlockEntity(T blockEntity) {
        return new LinkConnectionInfo(blockEntity.getName(), RANDOM.nextInt(0xFFFFFF), LinkCardItem.LinkStatus.VALID, blockEntity.x, blockEntity.y, blockEntity.z);
    }

    public static @Nullable LinkConnectionInfo fromNbt(NbtCompound connectionInfoNbt) {
        LinkCardItem.LinkStatus status = LinkCardItem.LinkStatus.values()[connectionInfoNbt.getInt("status")];
        if (status == LinkCardItem.LinkStatus.UNLINKED) {
            return null;
        }
        NbtCompound pos = connectionInfoNbt.getCompound("pos");
        return new LinkConnectionInfo(
                connectionInfoNbt.getString("name"),
                connectionInfoNbt.getInt("color"),
                status,
                pos.getInt("x"), pos.getInt("y"), pos.getInt("z")
        );
    }

    public NbtCompound toNbt() {
        NbtCompound connectionInfoNbt = new NbtCompound();
        NbtCompound pos = new NbtCompound();
        pos.putInt("x", x);
        pos.putInt("y", y);
        pos.putInt("z", z);
        connectionInfoNbt.put("pos", pos);
        connectionInfoNbt.putInt("status", status.ordinal());
        connectionInfoNbt.putInt("color", color);
        connectionInfoNbt.putString("name", name);
        return connectionInfoNbt;
    }
}
