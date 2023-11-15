package com.github.kydzombie.link.packet

import com.github.kydzombie.link.Link
import com.github.kydzombie.link.Link.accessing
import com.github.kydzombie.link.block.HasLinkInfo
import net.minecraft.network.PacketHandler
import net.minecraft.packet.AbstractPacket
import net.modificationstation.stationapi.api.entity.player.PlayerHelper
import net.modificationstation.stationapi.api.packet.IdentifiablePacket
import net.modificationstation.stationapi.api.registry.Identifier
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
                (entities[index] as HasLinkInfo).openLinkMenu(player)
            }
        }
    }

    override fun length(): Int {
        return 4 + 1
    }

    override fun getId(): Identifier {
        return Link.MOD_ID.id("open_linked_storage")
    }
}
