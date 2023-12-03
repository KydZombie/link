package com.kydzombie.link.packet

import com.kydzombie.link.Link
import com.kydzombie.link.block.HasLinkInfo
import com.kydzombie.link.util.LinkConnectionInfo
import net.minecraft.network.PacketHandler
import net.minecraft.packet.AbstractPacket
import net.modificationstation.stationapi.api.entity.player.PlayerHelper
import net.modificationstation.stationapi.api.network.packet.IdentifiablePacket
import net.modificationstation.stationapi.api.util.Identifier
import java.io.DataInputStream
import java.io.DataOutputStream

class UpdateLinkInfoPacket : AbstractPacket, IdentifiablePacket {
    private lateinit var info: LinkConnectionInfo

    constructor()
    constructor(newInfo: LinkConnectionInfo) {
        info = newInfo
    }

    override fun read(dataInputStream: DataInputStream) {
        info = LinkConnectionInfo.createFrom(dataInputStream)
    }

    override fun write(dataOutputStream: DataOutputStream) {
        info.writeTo(dataOutputStream)
    }

    override fun apply(packetHandler: PacketHandler?) {
        val player = PlayerHelper.getPlayerFromPacketHandler(packetHandler)
        (Link.accessing[player] as? HasLinkInfo)?.setLinkInfo(info)
    }

    override fun length(): Int = info.size()

    override fun getId(): Identifier {
        return Link.NAMESPACE.id("update_link_info")
    }
}