package com.github.kydzombie.link.block;

import com.github.kydzombie.link.gui.LinkTerminalGui;
import com.github.kydzombie.link.util.Vector2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.RenderHelper;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.tileentity.TileEntityBase;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface HasLinkInfo {
    String getLinkName();
    void setLinkName(String name);
    void openLinkMenu(PlayerBase player);

    Vector2i UNKNOWN_SELECTED = new Vector2i(198, 0);
    Vector2i UNKNOWN_UNSELECTED = new Vector2i(176, 0);

    Vector2i CHEST_SELECTED = new Vector2i(198, 22);
    Vector2i CHEST_UNSELECTED = new Vector2i(176, 22);

    Vector2i DOUBLE_CHEST_SELECTED = new Vector2i(198, 44);
    Vector2i DOUBLE_CHEST_UNSELECTED = new Vector2i(176, 44);

    default Vector2i getLinkButtonCoordinates(boolean selected) {
        return selected ? UNKNOWN_SELECTED : UNKNOWN_UNSELECTED;
    }

    default void renderLinkButton(int x, int y, boolean selected, DrawableHelper helper) {
        var coordinates = getLinkButtonCoordinates(selected);
        helper.blit(x, y, coordinates.x(), coordinates.y(), LinkTerminalGui.BUTTON_SIZE, LinkTerminalGui.BUTTON_SIZE);
    }
}
