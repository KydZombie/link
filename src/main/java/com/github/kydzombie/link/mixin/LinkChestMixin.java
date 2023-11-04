package com.github.kydzombie.link.mixin;

import com.github.kydzombie.link.Link;
import com.github.kydzombie.link.gui.AlternateChestStorage;
import com.github.kydzombie.link.block.HasLinkInfo;
import com.github.kydzombie.link.util.Vector2i;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.DoubleChest;
import net.minecraft.inventory.InventoryBase;
import net.minecraft.tileentity.TileEntityBase;
import net.minecraft.tileentity.TileEntityChest;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TileEntityChest.class)
public abstract class LinkChestMixin extends TileEntityBase implements HasLinkInfo {
    @Unique
    private String linkName = "Chest";

    @Override
    public Vector2i getLinkButtonCoordinates(boolean selected) {
        var inventory = findInventory();
        if (inventory instanceof DoubleChest) {
            return selected ? DOUBLE_CHEST_SELECTED : DOUBLE_CHEST_UNSELECTED;
        } else {
            return selected ? CHEST_SELECTED : CHEST_UNSELECTED;
        }
    }

    @Unique
    private InventoryBase findInventory() {;
        var id = level.getTileId(x, y, z);

        InventoryBase chestEntity = (InventoryBase) level.getTileEntity(x, y, z);

        if (level.getTileId(x - 1, y, z) == id) {
            chestEntity = new DoubleChest("Large chest", (TileEntityChest)level.getTileEntity(x - 1, y, z), chestEntity);
        } else if (level.getTileId(x + 1, y, z) == id) {
            chestEntity = new DoubleChest("Large chest", chestEntity, (TileEntityChest)level.getTileEntity(x + 1, y, z));
        } else if (level.getTileId(x, y, z - 1) == id) {
            chestEntity = new DoubleChest("Large chest", (TileEntityChest)level.getTileEntity(x, y, z - 1), chestEntity);
        } else if (level.getTileId(x, y, z + 1) == id) {
            chestEntity = new DoubleChest("Large chest", chestEntity, (TileEntityChest)level.getTileEntity(x, y, z + 1));
        }

        return chestEntity;
    }

    @Override
    public void openLinkMenu(PlayerBase player) {
        if (!level.isServerSide) {
            var inventory = findInventory();
            GuiHelper.openGUI(player, Link.MOD_ID.id("alternate_chest"), inventory, new AlternateChestStorage(player.inventory, inventory));
        }
    }

    @Override
    public String getLinkName() {
        return linkName;
    }

    @Override
    public void setLinkName(String name) {
        linkName = name;
    }
}
