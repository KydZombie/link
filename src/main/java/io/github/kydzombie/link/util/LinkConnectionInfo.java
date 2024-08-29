package io.github.kydzombie.link.util;

import io.github.kydzombie.link.Link;
import io.github.kydzombie.link.block.entity.HasLinkConnectionInfo;
import io.github.kydzombie.link.item.LinkCardItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record LinkConnectionInfo(String name, Identifier icon, int color, LinkCardItem.LinkStatus status, int x, int y,
                                 int z) {
    public static <T extends BlockEntity & Inventory> LinkConnectionInfo fromBlockEntity(T blockEntity) {
        if (blockEntity instanceof HasLinkConnectionInfo connectionInfo) {
            return new LinkConnectionInfo(connectionInfo.getLinkName(), connectionInfo.getLinkIcon(), connectionInfo.getLinkColor(), LinkCardItem.LinkStatus.VALID, blockEntity.x, blockEntity.y, blockEntity.z);
        } else {
            return new LinkConnectionInfo(blockEntity.getName() + " (!)", Link.NAMESPACE.id("unknown"), 0xFFFFFF, LinkCardItem.LinkStatus.VALID, blockEntity.x, blockEntity.y, blockEntity.z);
        }
    }

    public static LinkConnectionInfo fromInputStream(DataInputStream stream) throws IOException {
        return new LinkConnectionInfo(
                Packet.readString(stream, 64),
                Identifier.of(Packet.readString(stream, 64)),
                stream.readInt(),
                LinkCardItem.LinkStatus.values()[stream.readByte()],
                stream.readInt(),
                stream.readInt(),
                stream.readInt()
        );
    }

    public static @Nullable LinkConnectionInfo fromNbt(NbtCompound connectionInfoNbt) {
        LinkCardItem.LinkStatus status = LinkCardItem.LinkStatus.values()[connectionInfoNbt.getInt("status")];
        if (status == LinkCardItem.LinkStatus.UNLINKED) {
            return null;
        }
        NbtCompound pos = connectionInfoNbt.getCompound("pos");
        return new LinkConnectionInfo(
                connectionInfoNbt.getString("name"),
                Identifier.of(connectionInfoNbt.getString("icon")),
                connectionInfoNbt.getInt("color"),
                status,
                pos.getInt("x"), pos.getInt("y"), pos.getInt("z")
        );
    }

    public int getPacketSize() {
        return name.length() + icon.toString().length() + 17;
    }

    public void writeToOutputStream(DataOutputStream stream) throws IOException {
        Packet.writeString(name, stream);
        Packet.writeString(icon.toString(), stream);
        stream.writeInt(color);
        stream.writeByte(status.ordinal());
        stream.writeInt(x);
        stream.writeInt(y);
        stream.writeInt(z);
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
        connectionInfoNbt.putString("icon", icon.toString());
        return connectionInfoNbt;
    }
}
