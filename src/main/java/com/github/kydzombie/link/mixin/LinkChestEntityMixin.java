package com.github.kydzombie.link.mixin;

import com.github.kydzombie.link.Link;
import com.github.kydzombie.link.gui.AlternateChestStorage;
import com.github.kydzombie.link.block.HasLinkInfo;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.DoubleChest;
import net.minecraft.inventory.InventoryBase;
import net.minecraft.tileentity.TileEntityBase;
import net.minecraft.tileentity.TileEntityChest;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.registry.Identifier;
import org.lwjgl.util.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TileEntityChest.class)
public abstract class LinkChestEntityMixin extends TileEntityBase implements HasLinkInfo {
    @Unique
    private String linkName = "Chest";

    @Override
    public Identifier getLinkIconId() {
        var inventory = findInventory();
        if (inventory instanceof DoubleChest) {
            return Link.MOD_ID.id("double_chest");
        } else {
            return Link.MOD_ID.id("chest");
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

        System.out.println("Found: " + chestEntity.getInventorySize());

        return chestEntity;
    }

    @Override
    public void openLinkMenu(PlayerBase player) {
        System.out.println("HELLO");
        var inventory = findInventory();
        GuiHelper.openGUI(player, Link.MOD_ID.id("alternate_chest"), inventory, new AlternateChestStorage(player.inventory, inventory));
    }

    @Override
    public String getLinkName() {
        return linkName;
    }

    @Override
    public void setLinkName(String name) {
        linkName = name;
    }

    @Unique
    private Color color = (Color) Color.WHITE;

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }
}
