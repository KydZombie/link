package com.github.kydzombie.link.mixin;

import com.github.kydzombie.link.Link;
import com.github.kydzombie.link.gui.AlternateChestStorage;
import com.github.kydzombie.link.block.HasLinkInfo;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.DoubleChest;
import net.minecraft.inventory.InventoryBase;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DoubleChest.class)
public class LinkDoubleChestMixin implements HasLinkInfo {
    @Shadow private InventoryBase left;

    @Override
    public String getLinkName() {
        return ((HasLinkInfo)left).getLinkName();
    }

    @Override
    public void setLinkName(String name) {
        ((HasLinkInfo)left).setLinkName(name);
    }

    @Override
    public void openLinkMenu(PlayerBase player) {
        GuiHelper.openGUI(player, Link.MOD_ID.id("alternate_chest"), (InventoryBase) this, new AlternateChestStorage(player.inventory, (InventoryBase) this));
    }
}
