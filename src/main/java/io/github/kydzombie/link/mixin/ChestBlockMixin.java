package io.github.kydzombie.link.mixin;

import io.github.kydzombie.link.block.LinkCableBlock;
import io.github.kydzombie.link.block.LinkConnectorBlock;
import io.github.kydzombie.link.item.LinkCardItem;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class ChestBlockMixin {
    @Inject(method = "onUse(Lnet/minecraft/world/World;IIILnet/minecraft/entity/player/PlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void allowUsingLinkCard(World world, int x, int y, int z, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (player.method_1373()) {
            ItemStack stack = player.getHand();
            if (stack == null) return;
            Item item = stack.getItem();
            if (item instanceof LinkCardItem) {
                cir.setReturnValue(false);
                cir.cancel();
            } else if (item instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                if (block instanceof LinkCableBlock || block instanceof LinkConnectorBlock) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }
}
