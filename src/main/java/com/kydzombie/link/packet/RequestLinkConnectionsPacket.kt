package com.kydzombie.link.packet

import com.kydzombie.link.Link.accessing
import net.minecraft.network.PacketHandler
import net.minecraft.packet.AbstractPacket
import net.modificationstation.stationapi.api.entity.player.PlayerHelper
import java.io.DataInputStream
import java.io.DataOutputStream

class RequestLinkConnectionsPacket : AbstractPacket() {
    override fun read(dataInputStream: DataInputStream) {}
    override fun write(dataOutputStream: DataOutputStream) {}
    override fun apply(packetHandler: PacketHandler?) {
        val player = PlayerHelper.getPlayerFromPacketHandler(packetHandler)
        with(accessing[player]) {
            this?.sendUpdatePacket(player)
        }
    }

    override fun length(): Int {
        return 0
    }
}
