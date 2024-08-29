package io.github.kydzombie.link.packet;

import io.github.kydzombie.link.Link;
import io.github.kydzombie.link.gui.screen.ingame.LinkTerminalScreen;
import io.github.kydzombie.link.util.LinkConnectionInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.network.packet.IdentifiablePacket;
import net.modificationstation.stationapi.api.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UpdateLinkConnectionsPacket extends Packet implements IdentifiablePacket {
    private LinkConnectionInfo[] connections;

    public UpdateLinkConnectionsPacket() {

    }

    public UpdateLinkConnectionsPacket(LinkConnectionInfo[] connections) {
        this.connections = connections;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            connections = new LinkConnectionInfo[stream.readInt()];
            for (int i = 0; i < connections.length; i++) {
                connections[i] = LinkConnectionInfo.fromInputStream(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.write(connections.length);
            for (LinkConnectionInfo connection : connections) {
                connection.writeToOutputStream(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        Screen screen = ((Minecraft) FabricLoader.getInstance().getGameInstance()).currentScreen;
        if (screen instanceof LinkTerminalScreen linkTerminalScreen) {
            linkTerminalScreen.setConnections(connections);
        }
    }

    @Override
    public int size() {
        int size = 4;
        for (LinkConnectionInfo connection : connections) {
            size += connection.getPacketSize();
        }
        return size;
    }

    @Override
    public Identifier getId() {
        return Link.NAMESPACE.id("update_link_connections");
    }
}
