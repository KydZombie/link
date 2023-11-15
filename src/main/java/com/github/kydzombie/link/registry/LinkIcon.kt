package com.github.kydzombie.link.registry;

import com.github.kydzombie.link.gui.LinkTerminalGui;
import com.github.kydzombie.link.util.Vector2i;
import net.minecraft.client.gui.DrawableHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public class LinkIcon {
    private final int buttonSize;

    private static final Vector2i BACKGROUND_UNSELECTED = new Vector2i(176, 0);
    private static final Vector2i BACKGROUND_SELECTED = new Vector2i(176, 22);

    private Vector2i texturePos;

    public LinkIcon(Vector2i texturePos) {
        this.texturePos = texturePos;
        this.buttonSize = LinkTerminalGui.BUTTON_SIZE;
    }

    public LinkIcon(int textureX, int textureY) {
        this(new Vector2i(textureX, textureY));
    }

    Vector2i getLinkBackgroundCoordinates(boolean selected) {
        return selected ? BACKGROUND_SELECTED : BACKGROUND_UNSELECTED;
    }

    Vector2i getLinkIconCoordinates(boolean selected) {
        return texturePos;
    }

    public void render(int x, int y, boolean selected, Color color, DrawableHelper helper) {
        GL11.glColor3ub(color.getRedByte(), color.getGreenByte(), color.getBlueByte());
        var backgroundCoords = getLinkBackgroundCoordinates(selected);
        helper.blit(x, y, backgroundCoords.x(), backgroundCoords.y(), buttonSize, buttonSize);
        GL11.glColor3f(1f, 1f, 1f);
        var iconCoords = getLinkIconCoordinates(selected);
        helper.blit(x, y, iconCoords.x(), iconCoords.y(), buttonSize, buttonSize);
    }
}
