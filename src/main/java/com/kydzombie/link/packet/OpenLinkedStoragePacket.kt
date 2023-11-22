package com.kydzombie.link.packet

import com.kydzombie.link.Link
import com.kydzombie.link.Link.accessing
import com.kydzombie.link.block.HasLinkInfo
import net.minecraft.network.PacketHandler
import net.minecraft.packet.AbstractPacket
import net.modificationstation.stationapi.api.entity.player.PlayerHelper
import net.modificationstation.stationapi.api.network.packet.IdentifiablePacket
import net.modificationstation.stationapi.api.util.Identifier
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

class OpenLinkedStoragePacket : AbstractPacket, IdentifiablePacket {
    private var index = 0
    private var editMenu = false

    constructor()
    constructor(index: Int, editMenu: Boolean) {
        this.index = index
        this.editMenu = editMenu
    }

    override fun read(dataInputStream: DataInputStream) {
        try {
            index = dataInputStream.readInt()
            editMenu = dataInputStream.readBoolean()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun write(dataOutputStream: DataOutputStream) {
        try {
            dataOutputStream.writeInt(index)
            dataOutputStream.writeBoolean(editMenu)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun apply(packetHandler: PacketHandler?) {
        val player = PlayerHelper.getPlayerFromPacketHandler(packetHandler)
        accessing[player]?.tileEntities?.let { entities ->
            if (entities.size > index) {
                (entities[index] as HasLinkInfo).`link$openLinkMenu`(player)
            }
        }
    }

    override fun length(): Int {
        return 4 + 1
    }

    override fun getId(): Identifier {
        return Link.NAMESPACE.id("open_linked_storage")
    }
}
