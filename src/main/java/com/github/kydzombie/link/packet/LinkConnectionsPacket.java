package com.github.kydzombie.link.packet;

import com.github.kydzombie.link.Link;
import com.github.kydzombie.link.gui.LinkTerminalGui;
import com.github.kydzombie.link.util.LinkConnectionInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketHandler;
import net.minecraft.packet.AbstractPacket;
import net.modificationstation.stationapi.api.packet.IdentifiablePacket;
import net.modificationstation.stationapi.api.registry.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LinkConnectionsPacket extends AbstractPacket implements IdentifiablePacket {
    @NotNull private LinkConnectionInfo[] connections;

    public LinkConnectionsPacket() {
        super();
    }

    public LinkConnectionsPacket(@NotNull LinkConnectionInfo[] connections) {
        super();
        this.connections = connections;
    }

    @Override
    public void read(DataInputStream dataInputStream) {
        try {
            connections = new LinkConnectionInfo[dataInputStream.readInt()];
            for (int i = 0; i < connections.length; i++) {
                connections[i] = new LinkConnectionInfo(
                        Identifier.of(readString(dataInputStream, 100)),
                        readString(dataInputStream, 100),
                        new Color(
                                dataInputStream.readByte(),
                                dataInputStream.readByte(),
                                dataInputStream.readByte()
                        )
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream dataOutputStream) {
        try {
            dataOutputStream.writeInt(connections.length);
            for (LinkConnectionInfo connection : connections) {
                writeString(connection.type().toString(), dataOutputStream);
                writeString(connection.name(), dataOutputStream);
                dataOutputStream.write(connection.color().getRedByte());
                dataOutputStream.write(connection.color().getGreenByte());
                dataOutputStream.write(connection.color().getBlueByte());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(PacketHandler arg) {
        var screen = ((Minecraft)FabricLoader.getInstance().getGameInstance()).currentScreen;
        if (screen instanceof LinkTerminalGui gui) {
            gui.updateConnections(connections);
        }
    }

    @Override
    public int length() {
        int size = 4;
        for (var connection :
                connections) {
            size += connection.type().toString().length();
            size += connection.name().length();
            size += 3;
        }
        return size;
    }

    @Override
    public Identifier getId() {
        return Link.MOD_ID.id("link_connections");
    }
}
