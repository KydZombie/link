package com.kydzombie.link.item

import com.kydzombie.link.block.HasLinkInfo
import net.minecraft.entity.player.PlayerBase
import net.minecraft.item.ItemInstance
import net.minecraft.level.Level
import net.minecraft.util.io.CompoundTag
import net.modificationstation.stationapi.api.client.gui.CustomTooltipProvider
import net.modificationstation.stationapi.api.registry.Identifier
import net.modificationstation.stationapi.api.template.item.TemplateItemBase
import net.modificationstation.stationapi.api.util.Colours

class LinkCard(identifier: Identifier) : TemplateItemBase(identifier), CustomTooltipProvider {
    init {
        setTranslationKey(identifier)
        setMaxStackSize(1)
    }

    override fun useOnTile(
        itemInstance: ItemInstance,
        player: PlayerBase,
        level: Level,
        x: Int,
        y: Int,
        z: Int,
        meta: Int
    ): Boolean {
        if (player.method_1373()) {
            val entity = player.level.getTileEntity(x, y, z)
            if (entity is HasLinkInfo) {
                if (!level.isServerSide) {
                    val pos = CompoundTag()
                    pos.put("x", entity.x)
                    pos.put("y", entity.y)
                    pos.put("z", entity.z)
                    itemInstance.stationNBT.put("pos", pos)
                    itemInstance.stationNBT.put("linked", true)
                    itemInstance.stationNBT.put("entity_name", entity.getLinkName())
                }
                return true
            }
        }
        return super.useOnTile(itemInstance, player, level, x, y, z, meta)
    }

    // TODO: Localize this
    override fun getTooltip(itemInstance: ItemInstance, originalTooltip: String): Array<String> {
        return if (itemInstance.stationNBT.containsKey("pos")) {
            val nbt = itemInstance.stationNBT
            val pos = nbt.getCompoundTag("pos")
            if (nbt.getBoolean("linked")) {
                arrayOf(
                    originalTooltip,
                    nbt.getString("entity_name"),
                    "%d, %d, %d".formatted(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"))
                )
            } else {
                arrayOf(
                    originalTooltip,
                    Colours.RED.toString() + "Invalid Tile Entity!",
                    Colours.RED.toString() + "%d, %d, %d".formatted(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"))
                )
            }
        } else {
            arrayOf(originalTooltip)
        }
    }
}
