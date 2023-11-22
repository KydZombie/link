package com.kydzombie.link.block

import net.minecraft.block.BlockBase
import net.minecraft.block.material.Material
import net.minecraft.level.Level
import net.minecraft.tileentity.TileEntityBase
import net.minecraft.util.maths.Box
import net.modificationstation.stationapi.api.block.BlockState
import net.modificationstation.stationapi.api.item.ItemPlacementContext
import net.modificationstation.stationapi.api.state.StateManager
import net.modificationstation.stationapi.api.state.property.EnumProperty
import net.modificationstation.stationapi.api.util.Identifier
import net.modificationstation.stationapi.api.util.math.Direction

class LinkConnector(identifier: Identifier, material: Material) : LinkCable(
    identifier, material, .5f
) {
    init {
        // TODO: Fix uneven border
        defaultState =
            defaultState.with(
                FACING_PROPERTY,
                Direction.NORTH
            )
    }

    fun getConnectedTo(level: Level, x: Int, y: Int, z: Int): TileEntityBase? {
        return when (level.getBlockState(x, y, z).get(FACING_PROPERTY)) {
            Direction.UP -> level.getTileEntity(x, y + 1, z)
            Direction.DOWN -> level.getTileEntity(x, y - 1, z)
            Direction.EAST -> level.getTileEntity(x, y, z - 1)
            Direction.WEST -> level.getTileEntity(x, y, z + 1)
            Direction.NORTH -> level.getTileEntity(x - 1, y, z)
            Direction.SOUTH -> level.getTileEntity(x + 1, y, z)
            else -> null
        }
    }

    override fun getOutline(blockState: BlockState, x: Int, y: Int, z: Int): Box {
        val facing = blockState.get(FACING_PROPERTY)
        val maxX: Float = if (facing == Direction.SOUTH || blockState.get(SOUTH)) 1f else maxSize
        val minX: Float = if (facing == Direction.NORTH || blockState.get(NORTH)) 0f else minSize
        val maxY: Float = if (facing == Direction.UP || blockState.get(UP)) 1f else maxSize
        val minY: Float = if (facing == Direction.DOWN || blockState.get(DOWN)) 0f else minSize
        val maxZ: Float = if (facing == Direction.WEST || blockState.get(WEST)) 1f else maxSize
        val minZ: Float = if (facing == Direction.EAST || blockState.get(EAST)) 0f else minSize
        return Box.create(
            (x + minX).toDouble(),
            (y + minY).toDouble(),
            (z + minZ).toDouble(),
            (x + maxX).toDouble(),
            (y + maxY).toDouble(),
            (z + maxZ).toDouble()
        )
    }

    override fun appendProperties(builder: StateManager.Builder<BlockBase, BlockState>) {
        super.appendProperties(builder)
        builder.add(FACING_PROPERTY)
    }

    override fun getPlacementState(context: ItemPlacementContext): BlockState {
        return super.getPlacementState(context).with(FACING_PROPERTY, context.side.opposite)
    }

    companion object {
        val FACING_PROPERTY: EnumProperty<Direction> = EnumProperty.of("facing", Direction::class.java)
    }
}
