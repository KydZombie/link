package com.github.kydzombie.link.gui;

import net.minecraft.container.Chest;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.InventoryBase;

public class AlternateChestStorage extends Chest {
    public AlternateChestStorage(InventoryBase arg, InventoryBase arg2) {
        super(arg, arg2);
        System.out.println("ENjfowij : " + arg2.getClass().getName());
        System.out.println("INVENTROY IS: " + arg2.getInventorySize());
    }

    @Override
    public boolean canUse(PlayerBase arg) {
        return true;
    }
}
