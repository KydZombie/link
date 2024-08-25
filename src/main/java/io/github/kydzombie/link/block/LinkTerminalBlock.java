package io.github.kydzombie.link.block;

import io.github.kydzombie.link.Link;
import io.github.kydzombie.link.block.entity.LinkTerminalBlockEntity;
import io.github.kydzombie.link.gui.screen.LinkTerminalScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class LinkTerminalBlock extends TemplateBlockWithEntity {
    public LinkTerminalBlock(Identifier identifier) {
        super(identifier, Material.METAL);
        setHardness(2.5f);
        setTranslationKey(identifier);
        setDefaultState(getStateManager().getDefaultState().with(Properties.FACING, Direction.NORTH));
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new LinkTerminalBlockEntity();
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState().with(Properties.FACING, context.getPlayerLookDirection().getOpposite());
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (!player.method_1373() && world.getBlockEntity(x, y, z) instanceof LinkTerminalBlockEntity blockEntity) {
            if (!world.isRemote) {
                LinkTerminalScreenHandler screenHandler = new LinkTerminalScreenHandler(player, blockEntity);
                GuiHelper.openGUI(player, Link.NAMESPACE.id("link_terminal"), blockEntity, screenHandler);
            }
            return true;
        } else {
            return false;
        }
    }
}
