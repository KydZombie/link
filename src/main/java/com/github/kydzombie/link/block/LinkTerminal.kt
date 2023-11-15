package com.github.kydzombie.link.block

import com.github.kydzombie.link.Link
import com.github.kydzombie.link.Link.accessing
import com.github.kydzombie.link.gui.LinkTerminalStorage
import net.minecraft.block.BlockBase
import net.minecraft.block.material.Material
import net.minecraft.entity.Item
import net.minecraft.entity.player.PlayerBase
import net.minecraft.level.Level
import net.minecraft.tileentity.TileEntityBase
import net.modificationstation.stationapi.api.block.BlockState
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper
import net.modificationstation.stationapi.api.item.ItemPlacementContext
import net.modificationstation.stationapi.api.packet.PacketHelper
import net.modificationstation.stationapi.api.registry.Identifier
import net.modificationstation.stationapi.api.state.StateManager
import net.modificationstation.stationapi.api.state.property.EnumProperty
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity
import net.modificationstation.stationapi.api.util.math.Direction
import java.util.*
import java.util.function.Function

class LinkTerminal(identifier: Identifier, material: Material) : TemplateBlockWithEntity(identifier, material),
    HasLinkConnection {
    private val rand = Random()

    init {
        setTranslationKey(identifier)
        defaultState =
            stateManager.defaultState.with(
                FACING_PROPERTY,
                Direction.NORTH
            )
        setHardness(5f)
    }

    override fun appendProperties(builder: StateManager.Builder<BlockBase, BlockState>) {
        builder.add(FACING_PROPERTY)
    }

    override fun getPlacementState(context: ItemPlacementContext): BlockState {
        val direction = context.playerLookDirection.opposite
        return defaultState.with(FACING_PROPERTY, direction)
    }

    override fun canUse(level: Level, x: Int, y: Int, z: Int, player: PlayerBase): Boolean {
        if (level.getTileEntity(x, y, z) is LinkTerminalEntity) {
            val terminal = level.getTileEntity(x, y, z) as LinkTerminalEntity
            if (!level.isServerSide) {
                val storage = LinkTerminalStorage(player, terminal)
                GuiHelper.openGUI(player, Link.MOD_ID.id("link_terminal"), terminal, storage)
                accessing.forcePut(player, terminal)
                terminal.sendUpdatePacket()
            }
            return true
        }
        return false
    }

    override fun onBlockRemoved(level: Level, x: Int, y: Int, z: Int) {
        val entity = level.getTileEntity(x, y, z) as LinkTerminalEntity
        for (var6 in 0 until entity.inventorySize) {
            val itemInstance = entity.getInventoryItem(var6)
            if (itemInstance != null) {
                val xOffset = rand.nextFloat() * 0.8f + 0.1f
                val yOffset = rand.nextFloat() * 0.8f + 0.1f
                val zOffset = rand.nextFloat() * 0.8f + 0.1f
                val itemEntity = Item(
                    level,
                    (x.toFloat() + xOffset).toDouble(),
                    (y.toFloat() + yOffset).toDouble(),
                    (z.toFloat() + zOffset).toDouble(),
                    itemInstance
                )
                val velocityVariance = 0.05f
                itemEntity.velocityX = (rand.nextGaussian().toFloat() * velocityVariance).toDouble()
                itemEntity.velocityY = (rand.nextGaussian().toFloat() * velocityVariance + 0.2f).toDouble()
                itemEntity.velocityZ = (rand.nextGaussian().toFloat() * velocityVariance).toDouble()
                level.spawnEntity(itemEntity)
            }
        }
        super.onBlockRemoved(level, x, y, z)
    }

    override fun canConnectLinkCable(level: Level, x: Int, y: Int, z: Int, side: Direction): Boolean {
        return level.getBlockState(x, y, z).get(FACING_PROPERTY) != side
    }

    override fun createTileEntity(): TileEntityBase {
        return LinkTerminalEntity()
    }

    companion object {
        val FACING_PROPERTY: EnumProperty<Direction> = EnumProperty.of("facing", Direction::class.java)
    }
}
