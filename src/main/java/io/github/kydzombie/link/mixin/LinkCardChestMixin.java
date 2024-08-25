package io.github.kydzombie.link.mixin;

import io.github.kydzombie.link.item.LinkCardItem;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class LinkCardChestMixin {
    @Inject(method = "onUse(Lnet/minecraft/world/World;IIILnet/minecraft/entity/player/PlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void allowUsingLinkCard(World world, int x, int y, int z, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (player.method_1373()) {
            ItemStack stack = player.getHand();
            if (stack == null) return;
            if (stack.getItem() instanceof LinkCardItem) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
