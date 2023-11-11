package com.github.kydzombie.link.packet;

import com.github.kydzombie.link.Link;
import com.github.kydzombie.link.block.HasLinkInfo;
import com.github.kydzombie.link.util.LinkConnectionInfo;
import net.minecraft.network.PacketHandler;
import net.minecraft.packet.AbstractPacket;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.packet.PacketHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.stream.Stream;

public class RequestLinkConnectionsPacket extends AbstractPacket {
    @Override
    public void read(DataInputStream dataInputStream) {

    }

    @Override
    public void write(DataOutputStream dataOutputStream) {

    }

    @Override
    public void apply(PacketHandler packetHandler) {
        var player = PlayerHelper.getPlayerFromPacketHandler(packetHandler);
        var terminal = Link.accessing.get(player);
        PacketHelper.sendTo(
                player, new LinkConnectionsPacket(
                        ((Stream<HasLinkInfo>) (Object) Arrays.stream(terminal.getTileEntities()))
                                .map(HasLinkInfo::getLinkConnectionInfo)
                                .toArray(LinkConnectionInfo[]::new)
                )
        );
    }

    @Override
    public int length() {
        return 0;
    }
}
