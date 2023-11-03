package com.github.kydzombie.link.block;

import com.github.kydzombie.link.util.Vector2i;
import net.minecraft.entity.player.PlayerBase;

public interface HasLinkInfo {
    String getName();
    void setName(String name);
    void openLinkMenu(PlayerBase player);

    Vector2i UNKNOWN_SELECTED =new Vector2i(198, 22);
    Vector2i UNKNOWN_UNSELECTED =new Vector2i(176, 22);

    Vector2i CHEST_SELECTED =new Vector2i(198, 0);
    Vector2i CHEST_UNSELECTED =new Vector2i(176, 0);

    default Vector2i getButtonCoordinates(boolean selected) {
        return selected ? UNKNOWN_SELECTED : UNKNOWN_UNSELECTED;
    }
}
