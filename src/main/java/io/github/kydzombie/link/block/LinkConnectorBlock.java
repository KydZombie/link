package io.github.kydzombie.link.block;

import io.github.kydzombie.link.Link;
import io.github.kydzombie.link.util.LinkConnectionInfo;
import net.danygames2014.nyalib.network.NetworkComponent;
import net.danygames2014.nyalib.network.NetworkType;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.StringIdentifiable;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

// TODO: Collision
public class LinkConnectorBlock extends TemplateBlock implements NetworkComponent {
    private static final EnumProperty<Connection> NORTH = EnumProperty.of("north", Connection.class);
    private static final EnumProperty<Connection> SOUTH = EnumProperty.of("south", Connection.class);
    private static final EnumProperty<Connection> EAST = EnumProperty.of("east", Connection.class);
    private static final EnumProperty<Connection> WEST = EnumProperty.of("west", Connection.class);
    private static final EnumProperty<Connection> UP = EnumProperty.of("up", Connection.class);
    private static final EnumProperty<Connection> DOWN = EnumProperty.of("down", Connection.class);
    private static final EnumMap<Direction, EnumProperty<Connection>> DIRECTION_TO_PROPERTY = new EnumMap<>(Direction.class) {{
        put(Direction.NORTH, NORTH);
        put(Direction.SOUTH, SOUTH);
        put(Direction.EAST, EAST);
        put(Direction.WEST, WEST);
        put(Direction.UP, UP);
        put(Direction.DOWN, DOWN);
    }};

    public LinkConnectorBlock(Identifier identifier) {
        super(identifier, Material.METAL);
        setTranslationKey(identifier);
        setHardness(.8f);
        setDefaultState(getStateManager().getDefaultState()
                .with(NORTH, Connection.NONE)
                .with(SOUTH, Connection.NONE)
                .with(EAST, Connection.NONE)
                .with(WEST, Connection.NONE)
                .with(UP, Connection.NONE)
                .with(DOWN, Connection.NONE)
        );
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    private boolean isNetworkComponentInDirection(World world, BlockPos blockPos, Direction direction) {
        if (world.getBlockState(blockPos.add(direction.getVector())).getBlock() instanceof NetworkComponent networkComponent) {
            if (networkComponent.getNetworkTypes().contains(getNetworkType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    private static boolean IS_BEING_REPLACED = false;

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        super.neighborUpdate(world, x, y, z, id);
        BlockPos blockPos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(blockPos);
        for (Direction direction : Direction.values()) {
            if (state.get(DIRECTION_TO_PROPERTY.get(direction)) == Connection.INVENTORY) {
                continue;
            }
            state = state.with(DIRECTION_TO_PROPERTY.get(direction), isNetworkComponentInDirection(world, blockPos, direction) ? Connection.CABLE : Connection.NONE);
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
        Direction facing = context.getSide().getOpposite();
        for (Direction direction : Direction.values()) {
            EnumProperty<Connection> property = DIRECTION_TO_PROPERTY.get(direction);
            if (direction == facing) {
                state = state.with(property, Connection.INVENTORY);
            } else if (isNetworkComponentInDirection(world, blockPos, direction)) {
                state = state.with(property, Connection.CABLE);
            }
        }
        return state;
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

    public @Nullable <T extends BlockEntity & Inventory> LinkConnectionInfo getConnectionInfo(World world, int x, int y, int z) {
        Inventory inventory = getConnectedInventory(world, x, y, z);
        if (inventory != null) {
            return LinkConnectionInfo.fromBlockEntity((T) inventory);
        } else {
            return null;
        }
    }

    public @Nullable <T extends BlockEntity & Inventory> T getConnectedInventory(World world, int x, int y, int z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        for (Direction direction : DIRECTION_TO_PROPERTY.keySet()) {
            BlockPos checkPos = blockPos.add(direction.getVector());
            if (world.getBlockEntity(checkPos.x, checkPos.y, checkPos.z) instanceof Inventory inventory) {
                return (T) inventory;
            }
        }
        return null;
    }

    @Override
    public NetworkType getNetworkType() {
        return Link.linkNetwork;
    }

    enum Connection implements StringIdentifiable {
        NONE,
        CABLE,
        INVENTORY;

        @Override
        public String asString() {
            return switch (this) {
                case NONE -> "none";
                case CABLE -> "cable";
                case INVENTORY -> "inventory";
            };
        }
    }
}
