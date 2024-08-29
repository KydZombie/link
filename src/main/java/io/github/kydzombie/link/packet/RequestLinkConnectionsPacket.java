package io.github.kydzombie.link.packet;

import io.github.kydzombie.link.Link;
import io.github.kydzombie.link.gui.screen.LinkTerminalScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.IdentifiablePacket;
import net.modificationstation.stationapi.api.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RequestLinkConnectionsPacket extends Packet implements IdentifiablePacket {
    @Override
    public void read(DataInputStream stream) {

    }

    @Override
    public void write(DataOutputStream stream) {

    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        System.out.println("Received Request");
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if (player.container instanceof LinkTerminalScreenHandler handler) {
            System.out.println("Sending");
            handler.blockEntity.updateMachineConnections();
        } else {
            System.out.println("player.container = " + player.playerContainer);
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Identifier getId() {
        return Link.NAMESPACE.id("request_link_connections");
    }
}
