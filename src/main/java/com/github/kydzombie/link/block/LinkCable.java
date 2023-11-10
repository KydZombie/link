package com.github.kydzombie.link.block;

import net.minecraft.block.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.template.block.TemplateBlockBase;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.world.BlockStateView;

public class LinkCable extends TemplateBlockBase {
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");

    final float MIN_SIZE;
    final float MAX_SIZE;

    public LinkCable(Identifier identifier, Material material, float cableWidth) {
        super(identifier, material);
        MIN_SIZE = .5f - (cableWidth / 2);
        MAX_SIZE = .5f + (cableWidth / 2);

        this.setHardness(0.8f);

        setTranslationKey(identifier.toString());
        setDefaultState(getStateManager().getDefaultState()
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false));
    }

    public LinkCable(Identifier identifier, Material material) {
        this(identifier, material, .4f);
    }

    @Override
    public void appendProperties(StateManager.Builder<BlockBase, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public void onBlockPlaced(Level level, int x, int y, int z) {
        super.onBlockPlaced(level, x, y, z);
        updateBoundingBox(level, x, y, z);
    }

    @Override
    public void onAdjacentBlockUpdate(Level level, int x, int y, int z, int id) {
        level.setBlockState(x, y, z,  level.getBlockState(x, y, z)
                .with(NORTH, checkConnection(level, x - 1, y, z, 5))
                .with(SOUTH, checkConnection(level, x + 1, y, z, 4))
                .with(EAST, checkConnection(level, x, y, z - 1, 3))
                .with(WEST, checkConnection(level, x, y, z + 1, 2))
                .with(UP, checkConnection(level, x, y + 1, z, 0))
                .with(DOWN, checkConnection(level, x, y - 1, z, 1)));
        updateBoundingBox(level, x, y, z);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        var level = context.getWorld();
        var pos = context.getBlockPos();
        var x = pos.x;
        var y = pos.y;
        var z = pos.z;
        return getDefaultState()
                .with(NORTH, checkConnection(level, x - 1, y, z, 5))
                .with(SOUTH, checkConnection(level, x + 1, y, z, 4))
                .with(EAST, checkConnection(level, x, y, z - 1, 3))
                .with(WEST, checkConnection(level, x, y, z + 1, 2))
                .with(UP, checkConnection(level, x, y + 1, z, 0))
                .with(DOWN, checkConnection(level, x, y - 1, z, 1));
    }

    @Override
    public boolean isFullOpaque() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public Box getCollisionShape(Level level, int x, int y, int z) {
        return Box.create(x, y, z, x + 1, y + 1, z + 1);
    }

    protected Box getOutline(BlockState blockState, int x, int y, int z) {
        float maxX = blockState.get(SOUTH) ? 1 : MAX_SIZE;
        float minX = blockState.get(NORTH) ? 0 : MIN_SIZE;
        float maxY = blockState.get(UP) ? 1 : MAX_SIZE;
        float minY = blockState.get(DOWN) ? 0 : MIN_SIZE;
        float maxZ = blockState.get(WEST) ? 1 : MAX_SIZE;
        float minZ = blockState.get(EAST) ? 0 : MIN_SIZE;

        return Box.create(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
    }

    @Override
    public Box getOutlineShape(Level level, int x, int y, int z) {
        return getOutline(level.getBlockState(x, y, z), x, y, z);
    }

    boolean checkConnection(Level level, int x, int y, int z, int side) {
        var block = BlockBase.BY_ID[level.getTileId(x, y, z)];
        if (block instanceof HasLinkConnection connection) {
            return connection.canConnectLinkCable(level, x, y, z, Direction.byId(side));
        }
        return block instanceof LinkCable;
    }
}
