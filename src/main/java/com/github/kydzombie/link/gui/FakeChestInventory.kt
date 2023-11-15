package com.github.kydzombie.link.gui;

import com.github.kydzombie.link.block.HasLinkInfo;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.InventoryBase;
import net.minecraft.item.ItemInstance;
import org.lwjgl.util.Color;

public class FakeChestInventory implements HasLinkInfo, InventoryBase {
    ItemInstance[] inventory = new ItemInstance[27];

    @Override
    public String getLinkName() {
        return "Chest";
    }

    @Override
    public void setLinkName(String name) {

    }

    @Override
    public Color getColor() {
        return (Color) Color.WHITE;
    }

    @Override
    public void setColor(Color color) {

    }

    @Override
    public void openLinkMenu(PlayerBase player) {

    }

    @Override
    public int getInventorySize() {
        return 27;
    }

    @Override
    public ItemInstance getInventoryItem(int i) {
        return inventory[i];
    }

    @Override
    public ItemInstance takeInventoryItem(int i, int j) {
        if (this.inventory[i] != null) {
            ItemInstance var3;
            if (this.inventory[i].count <= j) {
                var3 = this.inventory[i];
                this.inventory[i] = null;
                this.markDirty();
                return var3;
            } else {
                var3 = this.inventory[i].split(j);
                if (this.inventory[i].count == 0) {
                    this.inventory[i] = null;
                }

                this.markDirty();
                return var3;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setInventoryItem(int i, ItemInstance arg) {
        this.inventory[i] = arg;
        if (arg != null && arg.count > this.getMaxItemCount()) {
            arg.count = this.getMaxItemCount();
        }

        this.markDirty();
    }

    @Override
    public String getContainerName() {
        return "Chest";
    }

    @Override
    public int getMaxItemCount() {
        return 64;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerBase arg) {
        return true;
    }
}
