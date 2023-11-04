package com.github.kydzombie.link.block;

import net.minecraft.block.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.level.Level;
import net.minecraft.tileentity.TileEntityBase;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.util.math.Direction;

public class LinkConnector extends LinkCable {
    public static final EnumProperty<Direction> FACING_PROPERTY = EnumProperty.of("facing", Direction.class);
    public LinkConnector(Identifier identifier, Material material) {
        super(identifier, material);
        setDefaultState(getStateManager().getDefaultState()
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
                .with(FACING_PROPERTY, Direction.NORTH));
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
    public void appendProperties(StateManager.Builder<BlockBase, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, FACING_PROPERTY);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context).with(FACING_PROPERTY, context.getSide().getOpposite());
    }
}
