package com.github.kydzombie.link.gui

import com.github.kydzombie.link.LinkClient.currentlySelectedColor
import com.github.kydzombie.link.block.HasLinkInfo
import net.minecraft.client.gui.screen.container.ContainerBase
import net.minecraft.entity.player.PlayerBase
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.InventoryBase
import org.lwjgl.opengl.GL11

class AlternateChestGui(player: PlayerBase, private val inventory: InventoryBase) :
    ContainerBase(AlternateChestStorage(player.inventory, inventory)) {
    private val playerInventory: PlayerInventory
    private val rows: Int

    init {
        playerInventory = player.inventory
        passEvents = false
        val var3: Short = 222
        val var4 = var3 - 108
        rows = inventory.inventorySize / 9
        containerHeight = var4 + rows * 18
    }

    override fun renderForeground() {
        textManager.drawText((inventory as HasLinkInfo).getLinkName(), 8, 6, 4210752)
        textManager.drawText(playerInventory.containerName, 8, containerHeight - 96 + 2, 4210752)
    }

    override fun renderContainerBackground(f: Float) {
        val var2 = minecraft.textureManager.getTextureId("/gui/container.png")
        val color = currentlySelectedColor
        GL11.glColor4ub(color.redByte, color.greenByte, color.blueByte, color.alphaByte)
        minecraft.textureManager.bindTexture(var2)
        val var3 = (width - containerWidth) / 2
        val var4 = (height - containerHeight) / 2
        blit(var3, var4, 0, 0, containerWidth, rows * 18 + 17)
        blit(var3, var4 + rows * 18 + 17, 0, 126, containerWidth, 96)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }
}
