package com.kydzombie.link.gui

import com.kydzombie.link.block.LinkTerminalEntity
import com.kydzombie.link.packet.OpenLinkMenuPacket
import com.kydzombie.link.slot.LinkCardSlot
import com.kydzombie.link.util.LinkConnectionInfo
import com.kydzombie.link.util.Vector2i
import net.minecraft.client.gui.screen.container.ContainerBase
import net.minecraft.entity.player.PlayerBase
import net.modificationstation.stationapi.api.network.packet.PacketHelper
import org.lwjgl.opengl.GL11
import java.awt.Rectangle

class LinkTerminalGui(private val player: PlayerBase, private val entity: LinkTerminalEntity) : ContainerBase(
    LinkTerminalStorage(
        player, entity
    )
) {
    private var mouseX = 0f
    private var mouseY = 0f
    private var linkCardsMenuOpen = false
    private var animationTimer = 0f
    private var lastFrameTime = System.currentTimeMillis()
    private var deltaTime = 0f
    private var connections: Array<LinkConnectionInfo>? = null

    private val renderX: Int
        get() = (width - containerWidth) / 2

    private val renderY: Int
        get() = (height - containerHeight) / 2

    private val linkCardsMenuOffset: Int
        get() = Math.round(LINK_CARDS_MENU.width * (animationTimer / ANIMATION_TIME))

    private val arrowX: Int
        get() = (if (linkCardsMenuOpen) renderX + 131 + LINK_CARDS_MENU.width else renderX + 161) - linkCardsMenuOffset

    private val arrowY: Int
        get() = renderY + 53

    init {
        containerHeight = 222
    }

    fun updateConnections(newConnections: Array<LinkConnectionInfo>?) {
        connections = newConnections
    }

    private fun updateDeltaTime() {
        val now = System.currentTimeMillis()
        deltaTime = 1f / (now - lastFrameTime)
        lastFrameTime = now
    }

    private fun isHoveringArrow(): Boolean {
        return mouseX > arrowX && mouseX < arrowX + OPEN_CARDS_ARROW.width && mouseY > arrowY && mouseY < arrowY + OPEN_CARDS_ARROW.height
    }

    private fun isHoveringButton(
        buttonX: Int,
        buttonY: Int,
        xSize: Int = BUTTON_SIZE,
        ySize: Int = BUTTON_SIZE
    ): Boolean {
        return mouseX > buttonX && mouseX < buttonX + xSize && mouseY > buttonY && mouseY < buttonY + ySize
    }

    override fun render(mouseX: Int, mouseY: Int, f: Float) {
        this.mouseX = mouseX.toFloat()
        this.mouseY = mouseY.toFloat()
        updateDeltaTime()
        if (linkCardsMenuOpen) {
            if (animationTimer < ANIMATION_TIME) {
                animationTimer += deltaTime
                if (animationTimer > ANIMATION_TIME) {
                    for (slot in container.slots) {
                        if (slot is LinkCardSlot) {
                            slot.x = LinkTerminalStorage.LINK_CARD_X
                        }
                    }
                    animationTimer = ANIMATION_TIME
                }
            }
        } else {
            if (animationTimer > 0) {
                for (slot in container.slots) {
                    if (slot is LinkCardSlot) {
                        slot.x = 10000
                    }
                }
                animationTimer -= deltaTime
                if (animationTimer < 0) {
                    animationTimer = 0f
                }
            }
        }
        super.render(mouseX, mouseY, f)
    }

    override fun renderForeground() {
        textManager.drawText(entity.containerName, 8, 6, 4210752)
        textManager.drawText(player.inventory.containerName, 8, containerHeight - 96 + 2, 4210752)
        val linkCardsMenuOffset = Math.round(LINK_CARDS_MENU.width * (animationTimer / ANIMATION_TIME))

        // Link Buttons
        if (connections != null) {
            val maxPerRow = (containerWidth - CORNER_OFFSET.x - linkCardsMenuOffset) / (BUTTON_SIZE + BUTTON_MARGIN)
            for (i in connections!!.indices) {
                val connection = connections!![i]
                val buttonX = CORNER_OFFSET.x + i % maxPerRow * (BUTTON_SIZE + BUTTON_MARGIN)
                val buttonY = CORNER_OFFSET.y + i / maxPerRow * (BUTTON_SIZE + BUTTON_MARGIN)
                if (isHoveringButton(renderX + buttonX, renderY + buttonY)) {
                    drawTextWithShadowCentred(
                        textManager,
                        connection.name,
                        buttonX + BUTTON_SIZE / 2,
                        buttonY - 9,
                        Int.MAX_VALUE
                    )
                }
            }
        }
    }

    override fun renderContainerBackground(f: Float) {
        val textureId = minecraft.textureManager.getTextureId("/assets/link/gui/link_terminal_gui.png")
        GL11.glColor4f(1f, 1f, 1f, 1f)
        minecraft.textureManager.bindTexture(textureId)

        // Background
        blit(renderX, renderY, 0, 0, containerWidth, 223)

        // Link Cards
        val linkCardsMenuOffset = Math.round(LINK_CARDS_MENU.width * (animationTimer / ANIMATION_TIME))
        blit(
            renderX + 143 + LINK_CARDS_MENU.width - linkCardsMenuOffset,
            renderY + 5,
            LINK_CARDS_MENU.x,
            LINK_CARDS_MENU.y,
            linkCardsMenuOffset,
            LINK_CARDS_MENU.height
        )


        // Render link card open/close arrow
        if (linkCardsMenuOpen) {
            blit(
                arrowX,
                arrowY,
                CLOSE_CARDS_ARROW.x,
                if (isHoveringArrow()) CLOSE_CARDS_ARROW.y + SELECTED_ARROW_OFFSET else CLOSE_CARDS_ARROW.y,
                CLOSE_CARDS_ARROW.width,
                CLOSE_CARDS_ARROW.height
            )
        } else {
            blit(
                arrowX,
                arrowY,
                OPEN_CARDS_ARROW.x,
                if (isHoveringArrow()) OPEN_CARDS_ARROW.y + SELECTED_ARROW_OFFSET else OPEN_CARDS_ARROW.y,
                OPEN_CARDS_ARROW.width,
                OPEN_CARDS_ARROW.height
            )
        }

        // Link Buttons
        if (connections != null) {
            val maxPerRow = (containerWidth - CORNER_OFFSET.x - linkCardsMenuOffset) / (BUTTON_SIZE + BUTTON_MARGIN)
            for (i in connections!!.indices) {
                val connection = connections!![i]
                val buttonX = renderX + CORNER_OFFSET.x + i % maxPerRow * (BUTTON_SIZE + BUTTON_MARGIN)
                val buttonY = renderY + CORNER_OFFSET.y + i / maxPerRow * (BUTTON_SIZE + BUTTON_MARGIN)
                val selected =
                    mouseX > buttonX && mouseX < buttonX + BUTTON_SIZE && mouseY > buttonY && mouseY < buttonY + BUTTON_SIZE
                // TODO: Make icons show that they are from link cards
                connection.linkIcon.render(buttonX, buttonY, selected, connection.color, this)
            }
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (isHoveringArrow()) {
            linkCardsMenuOpen = !linkCardsMenuOpen
        }
        if (connections != null) {
            val maxPerRow = (containerWidth - CORNER_OFFSET.x - linkCardsMenuOffset) / (BUTTON_SIZE + BUTTON_MARGIN)
            for (i in connections!!.indices) {
                val connection = connections!![i]
                val buttonX = renderX + CORNER_OFFSET.x + i % maxPerRow * (BUTTON_SIZE + BUTTON_MARGIN)
                val buttonY = renderY + CORNER_OFFSET.y + i / maxPerRow * (BUTTON_SIZE + BUTTON_MARGIN)
                if (isHoveringButton(buttonX, buttonY)) {
                    if (mouseButton == 0) {
                        connection.setCurrentEntity()
                        PacketHelper.send(OpenLinkMenuPacket(i, false))
                    } else if (mouseButton == 1) {
                        connection.setCurrentEntity()
                        PacketHelper.send(OpenLinkMenuPacket(i, true))
                    }
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    companion object {
        private val CORNER_OFFSET = Vector2i(7, 7 + 20)
        private const val ANIMATION_TIME = 1.5f
        private val LINK_CARDS_MENU = Rectangle(228, 0, 28, 128)
        private val OPEN_CARDS_ARROW = Rectangle(236, 128, 10, 35)
        private val CLOSE_CARDS_ARROW = Rectangle(246, 128, 10, 35)
        private const val SELECTED_ARROW_OFFSET = 35
        const val BUTTON_SIZE = 22
        private const val BUTTON_MARGIN = 10
    }
}
