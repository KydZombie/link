package com.kydzombie.link.mixin;

import com.kydzombie.link.Link;
import com.kydzombie.link.block.HasLinkInfo;
import com.kydzombie.link.gui.AlternateChestStorage;
import com.kydzombie.link.gui.FakeChestInventory;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.DoubleChest;
import net.minecraft.inventory.InventoryBase;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import org.lwjgl.util.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DoubleChest.class)
public abstract class LinkDoubleChestMixin implements HasLinkInfo, InventoryBase {
    @Shadow
    private InventoryBase left;

    @Shadow
    private InventoryBase right;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void fixDoubleChest(String arg, InventoryBase arg2, InventoryBase par3, CallbackInfo ci) {
        if (left == null) {
            left = new FakeChestInventory();
        }
        if (right == null) {
            right = new FakeChestInventory();
        }
    }

    @Override
    public String link$getLinkName() {
        return ((HasLinkInfo) left).link$getLinkName();
    }

    @Override
    public void link$setLinkName(String name) {
        ((HasLinkInfo) left).link$setLinkName(name);
    }

    @Override
    public Color link$getColor() {
        return ((HasLinkInfo) left).link$getColor();
    }

    @Override
    public void link$setColor(Color color) {
        ((HasLinkInfo) left).link$setColor(color);
    }

    @Override
    public void link$openLinkMenu(PlayerBase player) {
        GuiHelper.openGUI(player, Link.NAMESPACE.id("alternate_double_chest"), this, new AlternateChestStorage(player.inventory, this));
    }
}
