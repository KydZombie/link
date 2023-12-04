package com.kydzombie.link.registry

import com.kydzombie.link.gui.LinkTerminalGui
import com.kydzombie.link.util.Vector2i
import net.minecraft.client.gui.DrawableHelper
import org.lwjgl.opengl.GL11
import org.lwjgl.util.Color

open class LinkIcon(private val texturePos: Vector2i) {
    private val buttonSize: Int = LinkTerminalGui.BUTTON_SIZE

    constructor(textureX: Int, textureY: Int) : this(Vector2i(textureX, textureY))

    protected open fun getLinkBackgroundCoordinates(selected: Boolean): Vector2i {
        return if (selected) BACKGROUND_SELECTED else BACKGROUND_UNSELECTED
    }

    protected open fun getLinkIconCoordinates(selected: Boolean): Vector2i {
        return texturePos
    }

    open fun render(x: Int, y: Int, selected: Boolean, color: Color, helper: DrawableHelper, scale: Float = 1f) {
        GL11.glColor3ub(color.redByte, color.greenByte, color.blueByte)
        GL11.glTranslatef(x.toFloat(), y.toFloat(), 0F)
        GL11.glScalef(scale, scale, 1f)
        val backgroundCoords = getLinkBackgroundCoordinates(selected)
        helper.blit(0, 0, backgroundCoords.x, backgroundCoords.y, buttonSize, buttonSize)
        GL11.glColor3f(1f, 1f, 1f)
        val iconCoords = getLinkIconCoordinates(selected)
        helper.blit(0, 0, iconCoords.x, iconCoords.y, buttonSize, buttonSize)
        GL11.glScalef(1 / scale, 1 / scale, 1f)
        GL11.glTranslatef(-x.toFloat(), -y.toFloat(), 0F)
    }

    companion object {
        private val BACKGROUND_UNSELECTED = Vector2i(176, 0)
        private val BACKGROUND_SELECTED = Vector2i(176, 22)
    }
}
