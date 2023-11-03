package com.github.kydzombie.link.block;

import com.github.kydzombie.link.slot.LinkCardSlot;
import net.minecraft.container.ContainerBase;
import net.minecraft.container.slot.Slot;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.entity.player.PlayerInventory;

public class LinkTerminalStorage extends ContainerBase {
    private final PlayerBase player;
    private final LinkTerminalEntity entity;

    public static final int LINK_CARD_X = 149;

    public LinkTerminalStorage(PlayerBase player, LinkTerminalEntity entity) {
        this.player = player;
        this.entity = entity;

        addSlot(new LinkCardSlot(entity, 0, 10000, 11));
        addSlot(new LinkCardSlot(entity, 1, 10000, 31));
        addSlot(new LinkCardSlot(entity, 2, 10000, 51));
        addSlot(new LinkCardSlot(entity, 3, 10000, 71));
        addSlot(new LinkCardSlot(entity, 4, 10000, 91));
        addSlot(new LinkCardSlot(entity, 5, 10000, 111));

        int offset = (2 * 18) + 1;

        PlayerInventory playerInventory = player.inventory;

        for(int row = 0; row < 3; ++row) {
            for(int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 103 + row * 18 + offset));
            }
        }

        for(int row = 0; row < 9; ++row) {
            this.addSlot(new Slot(playerInventory, row, 8 + row * 18, 161 + offset));
        }
    }
    @Override
    public boolean canUse(PlayerBase arg) {
        return true;
    }
}
