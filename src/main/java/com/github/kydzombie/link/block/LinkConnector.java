package com.github.kydzombie.link.block;

import net.minecraft.block.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.tileentity.TileEntityBase;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.world.BlockStateView;

public class LinkConnector extends LinkCable {
    public static final EnumProperty<Direction> FACING_PROPERTY = EnumProperty.of("facing", Direction.class);
    public LinkConnector(Identifier identifier, Material material) {
        // TODO: Fix uneven border
        super(identifier, material, .5f);
        setDefaultState(getDefaultState().with(FACING_PROPERTY, Direction.NORTH));
//        setDefaultState(getStateManager().getDefaultState()
//                .with(NORTH, false)
//                .with(SOUTH, false)
//                .with(EAST, false)
//                .with(WEST, false)
//                .with(UP, false)
//                .with(DOWN, false)
//                .with(FACING_PROPERTY, Direction.NORTH));
    }

    public TileEntityBase getConnectedTo(Level level, int x, int y, int z) {
        return switch(level.getBlockState(x, y, z).get(FACING_PROPERTY)) {
            case UP -> level.getTileEntity(x, y + 1, z);
            case DOWN -> level.getTileEntity(x, y - 1, z);
            case EAST -> level.getTileEntity(x, y, z - 1);
            case WEST -> level.getTileEntity(x, y, z + 1);
            case NORTH -> level.getTileEntity(x - 1, y, z);
            case SOUTH -> level.getTileEntity(x + 1, y, z);
        };
    }

    @Override
    protected Box getOutline(BlockState blockState, int x, int y, int z) {
        var facing = blockState.get(FACING_PROPERTY);

        float maxX = facing == Direction.SOUTH || blockState.get(SOUTH) ? 1 : MAX_SIZE;
        float minX = facing == Direction.NORTH || blockState.get(NORTH) ? 0 : MIN_SIZE;
        float maxY = facing == Direction.UP || blockState.get(UP) ? 1 : MAX_SIZE;
        float minY = facing == Direction.DOWN || blockState.get(DOWN) ? 0 : MIN_SIZE;
        float maxZ = facing == Direction.WEST || blockState.get(WEST) ? 1 : MAX_SIZE;
        float minZ = facing == Direction.EAST || blockState.get(EAST) ? 0 : MIN_SIZE;

        return Box.create(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
    }

    public void appendProperties(StateManager.Builder<BlockBase, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING_PROPERTY);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context).with(FACING_PROPERTY, context.getSide().getOpposite());
    }
}
