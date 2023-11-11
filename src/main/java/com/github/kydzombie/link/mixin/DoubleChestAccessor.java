package com.github.kydzombie.link.mixin;

import net.minecraft.inventory.DoubleChest;
import net.minecraft.inventory.InventoryBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DoubleChest.class)
public interface DoubleChestAccessor {
    @Accessor
    InventoryBase getLeft();
}
