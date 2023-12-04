package com.kydzombie.link.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.widgets.Button
import org.lwjgl.opengl.GL11

class RGBSlider(index: Int, x: Int, y: Int, text: String, var value: Int, width: Int, height: Int = 20) :
    Button(index, x, y, width, height, text) {
    private var dragged = false

    override fun getYImage(bl: Boolean): Int = 0

    override fun postRender(minecraft: Minecraft?, mouseX: Int, mouseY: Int) {
        if (visible) {
            if (dragged) {
                value = (((mouseX - (x + 4)).toFloat() / (width - 8)) * 255).toInt().coerceIn(0..255)
                text = value.toString()
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            blit(x + ((value / 255f) * (width - 8).toFloat()).toInt(), y, 0, 66, 4, height.coerceIn(0, 20))
            blit(x + ((value / 255f) * (width - 8).toFloat()).toInt() + 4, y, 196, 66, 4, height.coerceIn(0, 20))
        }
    }

    override fun isMouseOver(minecraft: Minecraft, mouseX: Int, mouseY: Int): Boolean {
        if (super.isMouseOver(minecraft, mouseX, mouseY)) {
            value = (((mouseX - (x + 4)).toFloat() / (width - 8)) * 255).toInt().coerceIn(0..255)
            text = value.toString()
            dragged = true
            return true
        }
        return false
    }

    override fun mouseReleased(i: Int, j: Int) {
        dragged = false
    }
}