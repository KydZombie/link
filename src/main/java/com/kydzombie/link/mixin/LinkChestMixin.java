package com.kydzombie.link.mixin;

import com.kydzombie.link.block.LinkCable;
import com.kydzombie.link.item.LinkCard;
import net.minecraft.block.Chest;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.Block;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chest.class)
public class LinkChestMixin {
    @Inject(method = "canUse(Lnet/minecraft/level/Level;IIILnet/minecraft/entity/player/PlayerBase;)Z", at = @At("HEAD"), cancellable = true)
    private void checkLinkCard(Level level, int x, int y, int z, PlayerBase player, CallbackInfoReturnable<Boolean> cir) {
        if (player.method_1373()) {
            var item = player.getHeldItem();
            if (item == null) return;
            if (item.getType() instanceof LinkCard) {
                cir.setReturnValue(false);
                cir.cancel();
            } else if (item.getType() instanceof Block block) {
                if (block.getBlock() instanceof LinkCable) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }
}
