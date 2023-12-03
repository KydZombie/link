package com.kydzombie.link.packet

import com.kydzombie.link.Link
import com.kydzombie.link.Link.accessing
import com.kydzombie.link.block.LinkTerminalEntity
import net.minecraft.network.PacketHandler
import net.minecraft.packet.AbstractPacket
import net.modificationstation.stationapi.api.entity.player.PlayerHelper
import net.modificationstation.stationapi.api.network.packet.IdentifiablePacket
import net.modificationstation.stationapi.api.util.Identifier
import java.io.DataInputStream
import java.io.DataOutputStream

class RequestLinkConnectionsPacket : AbstractPacket(), IdentifiablePacket {
    override fun read(dataInputStream: DataInputStream) = Unit
    override fun write(dataOutputStream: DataOutputStream) = Unit
    override fun apply(packetHandler: PacketHandler?) {
        val player = PlayerHelper.getPlayerFromPacketHandler(packetHandler)
        (accessing[player] as? LinkTerminalEntity?)?.sendUpdatePacket(player)
    }

    override fun length(): Int = 0

    override fun getId(): Identifier = Link.NAMESPACE.id("request_link_connections")
}
