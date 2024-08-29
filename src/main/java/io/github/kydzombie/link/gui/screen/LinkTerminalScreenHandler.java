package io.github.kydzombie.link.gui.screen;

import io.github.kydzombie.link.block.entity.LinkTerminalBlockEntity;
import io.github.kydzombie.link.gui.screen.slot.LinkCardSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class LinkTerminalScreenHandler extends ScreenHandler {
    public LinkTerminalBlockEntity blockEntity;

    public LinkTerminalScreenHandler(PlayerEntity player, LinkTerminalBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        for (int cardSlot = 0; cardSlot < blockEntity.size(); cardSlot++) {
            addSlot(new LinkCardSlot(blockEntity, cardSlot, 10000, 11 + (cardSlot * 20)));
        }

        final int UI_OFFSET = 2 * 18 + 1;
        PlayerInventory playerInventory = player.inventory;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 103 + row * 18 + UI_OFFSET));
            }

        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 8 + column * 18, 161 + UI_OFFSET));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return blockEntity.canPlayerUse(player);
    }
}
