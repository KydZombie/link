package com.github.kydzombie.link.packet

import com.github.kydzombie.link.Link.accessing
import com.github.kydzombie.link.block.HasLinkInfo
import com.github.kydzombie.link.util.LinkConnectionInfo
import net.minecraft.network.PacketHandler
import net.minecraft.packet.AbstractPacket
import net.minecraft.tileentity.TileEntityBase
import net.modificationstation.stationapi.api.entity.player.PlayerHelper
import net.modificationstation.stationapi.api.packet.PacketHelper
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*
import java.util.stream.Stream

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
