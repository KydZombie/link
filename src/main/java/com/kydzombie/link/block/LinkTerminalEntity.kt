package com.kydzombie.link.block

import com.kydzombie.link.Link
import com.kydzombie.link.Link.accessing
import com.kydzombie.link.mixin.DoubleChestAccessor
import com.kydzombie.link.packet.LinkConnectionsPacket
import com.kydzombie.link.util.LinkConnectionInfo
import net.minecraft.block.BlockBase
import net.minecraft.entity.player.PlayerBase
import net.minecraft.inventory.DoubleChest
import net.minecraft.inventory.InventoryBase
import net.minecraft.item.ItemInstance
import net.minecraft.tileentity.TileEntityBase
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.util.io.CompoundTag
import net.minecraft.util.io.ListTag
import net.modificationstation.stationapi.api.packet.PacketHelper
import net.modificationstation.stationapi.api.util.math.Vec3i
import java.util.*

class LinkTerminalEntity : TileEntityBase(), InventoryBase {
    private var inventory = arrayOfNulls<ItemInstance>(6)
    private val connections: Array<LinkConnectionInfo>
        get() = tileEntities.map { tileEntity -> (tileEntity as HasLinkInfo).linkConnectionInfo }.filterIsInstance<LinkConnectionInfo>().toTypedArray()

    val tileEntities: Array<TileEntityBase>
        get() {
            val connections = ArrayList<TileEntityBase>()
            if (level == null) {
                Link.LOGGER.error("LinkTerminalEntity has a null level")
                return emptyArray()
            }
            for (itemInstance in inventory) {
                if (itemInstance == null || itemInstance.type !== Link.LINK_CARD) continue
                val nbt = itemInstance.stationNBT
                if (nbt.getBoolean("linked")) {
                    val pos = nbt.getCompoundTag("pos")
                    val entity = level.getTileEntity(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"))
                    if (entity == null) {
                        nbt.put("linked", false)
                        continue
                    }
                    if (connections.contains(entity)) continue
                    if (entity is TileEntityChest && entity is CanFindDoubleChest) {
                        val found = entity.findInventory()
                        if (found is DoubleChest) {
                            val left = (found as DoubleChestAccessor).getLeft() as TileEntityBase
                            if (!connections.contains(left)) {
                                connections.add(left)
                            }
                        } else {
                            connections.add(entity)
                        }
                    } else {
                        connections.add(entity)
                    }
                }
            }
            val cablesToCheck = ArrayList<Vec3i>()
            val cablesChecked = ArrayList<Vec3i>()
            val connectors = ArrayList<Vec3i>()
            var relativeBlocks = arrayOf(
                Vec3i(x + 1, y, z), Vec3i(x - 1, y, z),
                Vec3i(x, y + 1, z), Vec3i(x, y - 1, z),
                Vec3i(x, y, z + 1), Vec3i(x, y, z - 1)
            )
            for (relativePos in relativeBlocks) {
                val block = BlockBase.BY_ID[level.getTileId(relativePos.x, relativePos.y, relativePos.z)] ?: continue
                if (block is LinkCable) {
                    if (!cablesToCheck.contains(relativePos)) {
                        cablesToCheck.add(Vec3i(relativePos.x, relativePos.y, relativePos.z))
                    }
                    if (block is LinkConnector) {
                        connectors.add(Vec3i(relativePos.x, relativePos.y, relativePos.z))
                    }
                }
            }
            while (cablesToCheck.isNotEmpty()) {
                val cablePos = cablesToCheck[0]
                relativeBlocks = arrayOf(
                    Vec3i(cablePos.x + 1, cablePos.y, cablePos.z), Vec3i(cablePos.x - 1, cablePos.y, cablePos.z),
                    Vec3i(cablePos.x, cablePos.y + 1, cablePos.z), Vec3i(cablePos.x, cablePos.y - 1, cablePos.z),
                    Vec3i(cablePos.x, cablePos.y, cablePos.z + 1), Vec3i(cablePos.x, cablePos.y, cablePos.z - 1)
                )
                for (relativePos in relativeBlocks) {
                    val block = BlockBase.BY_ID[level.getTileId(relativePos.x, relativePos.y, relativePos.z)] ?: continue
                    if (block is LinkCable) {
                        if (!cablesToCheck.contains(relativePos) && !cablesChecked.contains(relativePos)) {
                            cablesToCheck.add(Vec3i(relativePos.x, relativePos.y, relativePos.z))
                        }
                        if (block is LinkConnector) {
                            connectors.add(Vec3i(relativePos.x, relativePos.y, relativePos.z))
                        }
                    }
                }
                cablesChecked.add(cablePos)
                cablesToCheck.removeAt(0)
            }
            for (connectorPos in connectors) {
                val entity = Link.LINK_CONNECTOR.getConnectedTo(level, connectorPos.x, connectorPos.y, connectorPos.z)
                if (entity == null || connections.contains(entity)) continue
                if (entity is TileEntityChest) {
                    val found = (entity as CanFindDoubleChest).findInventory()
                    if (found is DoubleChest) {
                        val left = (found as DoubleChestAccessor).getLeft() as TileEntityBase
                        if (!connections.contains(left)) {
                            connections.add(left)
                        }
                    } else {
                        connections.add(entity)
                    }
                } else {
                    connections.add(entity)
                }
            }
            return connections.toTypedArray()
        }

    override fun getInventorySize(): Int {
        return inventory.size
    }

    override fun getInventoryItem(i: Int): ItemInstance? {
        return if (i > inventorySize) null else inventory[i]
    }

    fun sendUpdatePacket(player: PlayerBase?) {
        if (player != null) {
            PacketHelper.sendTo(player, LinkConnectionsPacket(connections))
        }
    }

    fun sendUpdatePacket() {
        sendUpdatePacket(accessing.inverse[this])
    }

    override fun takeInventoryItem(i: Int, j: Int): ItemInstance? {
        val existingItem = getInventoryItem(i)
        return if (existingItem != null) {
            inventory[i] = null
            sendUpdatePacket()
            existingItem
        } else {
            sendUpdatePacket()
            null
        }
    }

    override fun setInventoryItem(i: Int, itemInstance: ItemInstance?) {
        if (i > inventory.size) return
        inventory[i] = itemInstance
        sendUpdatePacket()
    }

    override fun getContainerName(): String {
        return "Link Terminal"
    }

    override fun getMaxItemCount(): Int {
        return 64
    }

    override fun canPlayerUse(arg: PlayerBase): Boolean {
        return true
    }

    override fun readIdentifyingData(tag: CompoundTag) {
        super.readIdentifyingData(tag)
        val listTag = tag.getListTag("inventory")
        inventory = arrayOfNulls(6)
        for (i in 0 until listTag.size()) {
            val compoundTag = listTag[i] as CompoundTag
            val slot = compoundTag.getByte("Slot").toInt() and 255
            if (slot < inventory.size) {
                inventory[slot] = ItemInstance(compoundTag)
            }
        }
    }

    override fun writeIdentifyingData(tag: CompoundTag) {
        super.writeIdentifyingData(tag)
        val listTag = ListTag()
        for (i in inventory.indices) {
            if (inventory[i] == null) continue
            val compoundTag = CompoundTag()
            compoundTag.put("Slot", i.toByte())
            inventory[i]!!.toTag(compoundTag)
            listTag.add(compoundTag)
        }
        tag.put("inventory", listTag)
    }
}
