package com.github.kydzombie.link.packet;

import com.github.kydzombie.link.Link;
import com.github.kydzombie.link.block.HasLinkInfo;
import com.github.kydzombie.link.block.LinkTerminalEntity;
import net.minecraft.network.PacketHandler;
import net.minecraft.packet.AbstractPacket;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.packet.IdentifiablePacket;
import net.modificationstation.stationapi.api.registry.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OpenLinkedStoragePacket extends AbstractPacket implements IdentifiablePacket {
    private int index;
    private boolean editMenu;

    public OpenLinkedStoragePacket() {
        System.out.println("Hi from the funny constructor");
    }

    public OpenLinkedStoragePacket(int index, boolean editMenu) {
        this.index = index;
        this.editMenu = editMenu;
    }

    @Override
    public void read(DataInputStream dataInputStream) {
        try {
            index = dataInputStream.readInt();
            editMenu = dataInputStream.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream dataOutputStream) {
        try {
            dataOutputStream.writeInt(index);
            dataOutputStream.writeBoolean(editMenu);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(PacketHandler packetHandler) {
        var player = PlayerHelper.getPlayerFromPacketHandler(packetHandler);
        var entities = Link.accessing.get(player).getTileEntities();
        if (entities.length > index) {
            entities[index].openLinkMenu(player);
        }

//        LinkIconRegistry.INSTANCE.get();
//        linkInfo.openLinkMenu(player);
    }

    @Override
    public int length() {
        return 4 + 1;
    }

    @Override
    public Identifier getId() {
        return Link.MOD_ID.id("open_linked_storage");
    }
}
