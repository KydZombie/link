package io.github.kydzombie.link.block;

import io.github.kydzombie.link.Link;
import net.danygames2014.nyalib.network.NetworkComponent;
import net.danygames2014.nyalib.network.NetworkType;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.EnumMap;

// TODO: Collision
public class LinkCableBlock extends TemplateBlock implements NetworkComponent {
    private static final EnumMap<Direction, BooleanProperty> DIRECTION_TO_PROPERTY = new EnumMap<>(Direction.class) {{
        put(Direction.NORTH, Properties.NORTH);
        put(Direction.SOUTH, Properties.SOUTH);
        put(Direction.EAST, Properties.EAST);
        put(Direction.WEST, Properties.WEST);
        put(Direction.UP, Properties.UP);
        put(Direction.DOWN, Properties.DOWN);
    }};

    public LinkCableBlock(Identifier identifier) {
        super(identifier, Material.METAL);
        setTranslationKey(identifier);
        setHardness(.8f);
        setDefaultState(getStateManager().getDefaultState()
                .with(Properties.NORTH, false)
                .with(Properties.SOUTH, false)
                .with(Properties.EAST, false)
                .with(Properties.WEST, false)
                .with(Properties.UP, false)
                .with(Properties.DOWN, false)
        );
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.NORTH, Properties.SOUTH, Properties.EAST, Properties.WEST, Properties.UP, Properties.DOWN);
    }

    private boolean isNetworkComponentInDirection(World world, BlockPos blockPos, Direction direction) {
        if (world.getBlockState(blockPos.add(direction.getVector())).getBlock() instanceof NetworkComponent networkComponent) {
            if (networkComponent.getNetworkType() == getNetworkType()) {
                return true;
            }
        }
        return false;
    }

    private static boolean IS_BEING_REPLACED = false;

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        super.neighborUpdate(world, x, y, z, id);
        BlockPos blockPos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(blockPos);
        for (Direction direction : Direction.values()) {
            state = state.with(DIRECTION_TO_PROPERTY.get(direction), isNetworkComponentInDirection(world, blockPos, direction));
        }

        IS_BEING_REPLACED = true;
        world.setBlockStateWithNotify(blockPos, state);
        IS_BEING_REPLACED = false;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState state = getDefaultState();
        for (Direction direction : Direction.values()) {
            if (isNetworkComponentInDirection(world, blockPos, direction)) {
                state = state.with(DIRECTION_TO_PROPERTY.get(direction), true);
            }
        }
        return state;
    }

    @Override
    public NetworkType getNetworkType() {
        return Link.linkNetwork;
    }

    @Override
    public <T extends Block & NetworkComponent> void addToNet(World world, int x, int y, int z, T component) {
        if (IS_BEING_REPLACED) return;
        NetworkComponent.super.addToNet(world, x, y, z, component);
    }

    @Override
    public <T extends Block & NetworkComponent> void removeFromNet(World world, int x, int y, int z, T component) {
        if (IS_BEING_REPLACED) return;
        NetworkComponent.super.removeFromNet(world, x, y, z, component);
    }
}
