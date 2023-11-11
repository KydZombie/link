package com.github.kydzombie.link.block;

import com.github.kydzombie.link.Link;
import com.github.kydzombie.link.gui.LinkTerminalStorage;
import com.github.kydzombie.link.packet.LinkConnectionsPacket;
import com.github.kydzombie.link.util.LinkConnectionInfo;
import net.minecraft.block.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Item;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.level.Level;
import net.minecraft.tileentity.TileEntityBase;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.packet.PacketHelper;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Arrays;
import java.util.Random;

public class LinkTerminal extends TemplateBlockWithEntity implements HasLinkConnection {
    private final Random rand = new Random();
    public static final EnumProperty<Direction> FACING_PROPERTY = EnumProperty.of("facing", Direction.class);
    public LinkTerminal(Identifier identifier, Material material) {
        super(identifier, material);
        setTranslationKey(identifier);
        setDefaultState(getStateManager().getDefaultState().with(FACING_PROPERTY, Direction.NORTH));
        setHardness(5f);
    }

    @Override
    public void appendProperties(StateManager.Builder<BlockBase, BlockState> builder) {
        builder.add(FACING_PROPERTY);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        var direction = context.getPlayerLookDirection().getOpposite();
        return getDefaultState().with(FACING_PROPERTY, direction);
    }

    @Override
    public boolean canUse(Level level, int x, int y, int z, PlayerBase player) {
        if (level.getTileEntity(x, y, z) instanceof LinkTerminalEntity terminal) {
            if (!level.isServerSide) {
                var tileEntities = terminal.getTileEntities();
                var storage = new LinkTerminalStorage(player, terminal, tileEntities);
                GuiHelper.openGUI(player, Link.MOD_ID.id("link_terminal"), terminal, storage);

                Link.accessing.put(player, terminal);

                PacketHelper.sendTo(
                        player,
                        new LinkConnectionsPacket(
                                Arrays.stream(tileEntities)
                                        .map(HasLinkInfo::getLinkConnectionInfo)
                                        .toArray(LinkConnectionInfo[]::new)
                        )
                );
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBlockRemoved(Level level, int x, int y, int z) {
        var entity = (LinkTerminalEntity) level.getTileEntity(x, y, z);

        for(int var6 = 0; var6 < entity.getInventorySize(); ++var6) {
            ItemInstance itemInstance = entity.getInventoryItem(var6);
            if (itemInstance != null) {
                float xOffset = rand.nextFloat() * 0.8F + 0.1F;
                float yOffset = rand.nextFloat() * 0.8F + 0.1F;
                float zOffset = rand.nextFloat() * 0.8F + 0.1F;

                Item itemEntity = new Item(level, (float)x + xOffset, (float)y + yOffset, (float)z + zOffset, itemInstance);
                float velocityVariance = 0.05F;
                itemEntity.velocityX = (float)rand.nextGaussian() * velocityVariance;
                itemEntity.velocityY = (float)rand.nextGaussian() * velocityVariance + 0.2F;
                itemEntity.velocityZ = (float)rand.nextGaussian() * velocityVariance;
                level.spawnEntity(itemEntity);
            }
        }
        super.onBlockRemoved(level, x, y, z);
    }

    @Override
    public boolean canConnectLinkCable(Level level, int x, int y, int z, Direction side) {
        return level.getBlockState(x, y, z).get(FACING_PROPERTY) != side;
    }

    @Override
    protected TileEntityBase createTileEntity() {
        return new LinkTerminalEntity();
    }
}
