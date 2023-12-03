package com.kydzombie.link.packet

import com.kydzombie.link.Link
import com.kydzombie.link.gui.LinkTerminalGui
import com.kydzombie.link.util.LinkConnectionInfo
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.network.PacketHandler
import net.minecraft.packet.AbstractPacket
import net.modificationstation.stationapi.api.network.packet.IdentifiablePacket
import net.modificationstation.stationapi.api.util.Identifier
import java.io.DataInputStream
import java.io.DataOutputStream

class LinkConnectionsPacket() : AbstractPacket(), IdentifiablePacket {

    private lateinit var connections: Array<LinkConnectionInfo>

    constructor(connections: Array<LinkConnectionInfo>) : this() {
        this.connections = connections
    }

    override fun read(dataInputStream: DataInputStream) {
        connections = Array(dataInputStream.readInt()) {
            LinkConnectionInfo.createFrom(dataInputStream)
        }
    }

    override fun write(dataOutputStream: DataOutputStream) {
        dataOutputStream.writeInt(connections.size)
        for (connection in connections) connection.writeTo(dataOutputStream)
    }

    override fun apply(arg: PacketHandler?) {
        val screen = (FabricLoader.getInstance().gameInstance as Minecraft).currentScreen
        (screen as? LinkTerminalGui)?.updateConnections(connections)
    }

    override fun length(): Int {
        var size = 4
        for (connection in connections) size += connection.size()
        return size
    }

    override fun getId(): Identifier {
        return Link.NAMESPACE.id("link_connections")
    }
}
