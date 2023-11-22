package com.kydzombie.link.block

import net.minecraft.block.BlockBase
import net.minecraft.block.material.Material
import net.minecraft.level.BlockView
import net.minecraft.level.Level
import net.minecraft.util.maths.Box
import net.modificationstation.stationapi.api.block.BlockState
import net.modificationstation.stationapi.api.item.ItemPlacementContext
import net.modificationstation.stationapi.api.state.StateManager
import net.modificationstation.stationapi.api.state.property.BooleanProperty
import net.modificationstation.stationapi.api.template.block.TemplateBlock
import net.modificationstation.stationapi.api.util.Identifier
import net.modificationstation.stationapi.api.util.math.Direction
import net.modificationstation.stationapi.api.world.BlockStateView

open class LinkCable(
    identifier: Identifier,
    material: Material,
    cableWidth: Float = .4f
) : TemplateBlock(identifier, material) {
    val minSize = .5f - cableWidth / 2
    val maxSize = .5f + cableWidth / 2

    init {
        this.setHardness(0.8f)
        translationKey = identifier.toString()
        defaultState = stateManager.defaultState
            .with(NORTH, false)
            .with(SOUTH, false)
            .with(EAST, false)
            .with(WEST, false)
            .with(UP, false)
            .with(DOWN, false)
    }

    override fun appendProperties(builder: StateManager.Builder<BlockBase, BlockState>) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN)
    }

    override fun onBlockPlaced(level: Level, x: Int, y: Int, z: Int) {
        super.onBlockPlaced(level, x, y, z)
        updateBoundingBox(level, x, y, z)
    }

    override fun onAdjacentBlockUpdate(level: Level, x: Int, y: Int, z: Int, id: Int) {
        level.setBlockState(
            x, y, z, level.getBlockState(x, y, z)
                .with(NORTH, checkConnection(level, x - 1, y, z, 5))
                .with(SOUTH, checkConnection(level, x + 1, y, z, 4))
                .with(EAST, checkConnection(level, x, y, z - 1, 3))
                .with(WEST, checkConnection(level, x, y, z + 1, 2))
                .with(UP, checkConnection(level, x, y + 1, z, 0))
                .with(DOWN, checkConnection(level, x, y - 1, z, 1))
        )
        updateBoundingBox(level, x, y, z)
    }

    override fun getPlacementState(context: ItemPlacementContext): BlockState {
        val level = context.world
        val pos = context.blockPos
        val x = pos.x
        val y = pos.y
        val z = pos.z
        return defaultState
            .with(NORTH, checkConnection(level, x - 1, y, z, 5))
            .with(SOUTH, checkConnection(level, x + 1, y, z, 4))
            .with(EAST, checkConnection(level, x, y, z - 1, 3))
            .with(WEST, checkConnection(level, x, y, z + 1, 2))
            .with(UP, checkConnection(level, x, y + 1, z, 0))
            .with(DOWN, checkConnection(level, x, y - 1, z, 1))
    }

    override fun isFullOpaque(): Boolean {
        return false
    }

    override fun isFullCube(): Boolean {
        return false
    }

    override fun getCollisionShape(level: Level, x: Int, y: Int, z: Int): Box {
        return if (level.getTileId(x, y, z) == id) {
            getOutline(level.getBlockState(x, y, z), x, y, z)
        } else super.getCollisionShape(level, x, y, z)
    }

    protected open fun getOutline(blockState: BlockState, x: Int, y: Int, z: Int): Box {
        val maxX: Float = if (blockState.get(SOUTH)) 1f else maxSize
        val minX: Float = if (blockState.get(NORTH)) 0f else minSize
        val maxY: Float = if (blockState.get(UP)) 1f else maxSize
        val minY: Float = if (blockState.get(DOWN)) 0f else minSize
        val maxZ: Float = if (blockState.get(WEST)) 1f else maxSize
        val minZ: Float = if (blockState.get(EAST)) 0f else minSize
        return Box.create(
            (x + minX).toDouble(),
            (y + minY).toDouble(),
            (z + minZ).toDouble(),
            (x + maxX).toDouble(),
            (y + maxY).toDouble(),
            (z + maxZ).toDouble()
        )
    }

    override fun getOutlineShape(level: Level, x: Int, y: Int, z: Int): Box {
        return getOutline(level.getBlockState(x, y, z), x, y, z)
    }

    override fun updateBoundingBox(arg: BlockView, x: Int, y: Int, z: Int) {
//        var box = getOutline(((BlockStateView) arg).getBlockState(x, y, z), x, y, z);
        val blockState = (arg as BlockStateView).getBlockState(x, y, z)
        val maxX: Float = if (blockState.get(SOUTH)) 1f else maxSize
        val minX: Float = if (blockState.get(NORTH)) 0f else minSize
        val maxY: Float = if (blockState.get(UP)) 1f else maxSize
        val minY: Float = if (blockState.get(DOWN)) 0f else minSize
        val maxZ: Float = if (blockState.get(WEST)) 1f else maxSize
        val minZ: Float = if (blockState.get(EAST)) 0f else minSize
        setBoundingBox(minX, minY, minZ, maxX, maxY, maxZ)
    }

    private fun checkConnection(level: Level, x: Int, y: Int, z: Int, side: Int): Boolean {
        val block = BY_ID[level.getTileId(x, y, z)]
        return if (block is HasLinkConnection) {
            block.canConnectLinkCable(
                level,
                x,
                y,
                z,
                Direction.byId(side)
            )
        } else block is LinkCable
    }

    companion object {
        val NORTH: BooleanProperty = BooleanProperty.of("north")
        val SOUTH: BooleanProperty = BooleanProperty.of("south")
        val EAST: BooleanProperty = BooleanProperty.of("east")
        val WEST: BooleanProperty = BooleanProperty.of("west")
        val UP: BooleanProperty = BooleanProperty.of("up")
        val DOWN: BooleanProperty = BooleanProperty.of("down")
    }
}
