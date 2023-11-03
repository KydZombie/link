package com.github.kydzombie.link.mixin;

import com.github.kydzombie.link.block.HasLinkInfo;
import com.github.kydzombie.link.util.Vector2i;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.InventoryBase;
import net.minecraft.tileentity.TileEntityChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TileEntityChest.class)
public class LinkChestMixin implements HasLinkInfo {
    @Unique
    private String linkName = "Chest";

    @Override
    public Vector2i getButtonCoordinates(boolean selected) {
        return selected ? CHEST_SELECTED : CHEST_UNSELECTED;
    }

    @Override
    public void openLinkMenu(PlayerBase player) {
        player.openChestScreen((InventoryBase) this);
    }

    @Override
    public String getName() {
        return linkName;
    }

    @Override
    public void setName(String name) {
        linkName = name;
    }
}
