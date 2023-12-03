package com.kydzombie.link.util

import com.kydzombie.link.LinkClient
import com.kydzombie.link.registry.LinkIcon
import com.kydzombie.link.registry.LinkIconRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.packet.AbstractPacket
import net.modificationstation.stationapi.api.util.Identifier
import org.lwjgl.util.Color
import java.io.DataInputStream
import java.io.DataOutputStream

data class LinkConnectionInfo(val type: Identifier, var name: String, val color: Color) {
    val linkIcon: LinkIcon
        get() = LinkIconRegistry.INSTANCE.getOrEmpty(type).orElse(LinkIconRegistry.UNKNOWN_ICON)

    @Environment(EnvType.CLIENT)
    fun setCurrentEntity() {
        LinkClient.currentEntityData = copy()
    }

    fun size(): Int {
        var size = 0
        size += type.toString().length
        size += name.length
        size += 3
        return size
    }

    fun writeTo(dataOutputStream: DataOutputStream) {
        AbstractPacket.writeString(type.toString(), dataOutputStream)
        AbstractPacket.writeString(name, dataOutputStream)
        dataOutputStream.write(color.redByte.toInt())
        dataOutputStream.write(color.greenByte.toInt())
        dataOutputStream.write(color.blueByte.toInt())
    }

    companion object {
        fun createFrom(dataInputStream: DataInputStream): LinkConnectionInfo {
            return LinkConnectionInfo(
                Identifier.of(AbstractPacket.readString(dataInputStream, 100)),
                AbstractPacket.readString(dataInputStream, 100),
                Color(
                    dataInputStream.readByte(),
                    dataInputStream.readByte(),
                    dataInputStream.readByte()
                )
            )
        }
    }
}
