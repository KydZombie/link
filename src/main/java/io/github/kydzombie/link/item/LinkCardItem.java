package io.github.kydzombie.link.item;

import io.github.kydzombie.link.util.LinkConnectionInfo;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Formatting;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class LinkCardItem extends TemplateItem implements CustomTooltipProvider {
    public LinkCardItem(Identifier identifier) {
        super(identifier);
        setTranslationKey(identifier);
        setMaxCount(1);
    }

    public static @Nullable LinkConnectionInfo getConnectionInfo(ItemStack stack) {
        return LinkConnectionInfo.fromNbt(stack.getStationNbt().getCompound("link:connection_info"));
    }

    public static LinkStatus getLinkStatus(ItemStack stack) {
        LinkConnectionInfo connectionInfo = getConnectionInfo(stack);
        if (connectionInfo != null) {
            return connectionInfo.status();
        } else {
            return LinkStatus.UNLINKED;
        }
    }

    public enum LinkStatus {
        UNLINKED(0f),
        INVALID(0.5f),
        VALID(1f);

        public final float predicateValue;

        LinkStatus(float predicateValue) {
            this.predicateValue = predicateValue;
        }
    }

    @Override
    public boolean useOnBlock(ItemStack stack, PlayerEntity user, World world, int x, int y, int z, int side) {
        if (user.method_1373()) {
            BlockEntity blockEntity = user.world.getBlockEntity(x, y, z);
            if (blockEntity instanceof Inventory) {
                if (!world.isRemote) {
                    NbtCompound stationNbt = stack.getStationNbt();
                    LinkConnectionInfo linkConnectionInfo = LinkConnectionInfo.fromBlockEntity((BlockEntity & Inventory) blockEntity);
                    stationNbt.put("link:connection_info", linkConnectionInfo.toNbt());
                }
                return true;
            }
        }
        return super.useOnBlock(stack, user, world, x, y, z, side);
    }

    @Override
    public String[] getTooltip(ItemStack stack, String originalTooltip) {
        LinkConnectionInfo connectionInfo = getConnectionInfo(stack);
        if (connectionInfo != null) {
            switch (connectionInfo.status()) {
                case VALID -> {
                    return new String[]{
                            originalTooltip,
                            connectionInfo.name(),
                            "Pos: " + connectionInfo.x() + ", " + connectionInfo.y() + ", " + connectionInfo.z()
                    };
                }
                case INVALID -> {
                    return new String[]{
                            originalTooltip,
                            Formatting.RED + "Invalid link!",
                            Formatting.RED + "Pos: " + connectionInfo.x() + ", " + connectionInfo.y() + ", " + connectionInfo.z()
                    };
                }
            }
        }
        return new String[]{originalTooltip};
    }
}
