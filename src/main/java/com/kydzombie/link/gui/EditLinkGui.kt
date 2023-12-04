package com.kydzombie.link.gui

import com.kydzombie.link.LinkClient
import com.kydzombie.link.block.HasLinkInfo
import com.kydzombie.link.packet.UpdateLinkInfoPacket
import com.kydzombie.link.util.LinkConnectionInfo
import net.minecraft.client.gui.screen.container.ContainerBase
import net.minecraft.client.gui.widgets.Textbox
import net.minecraft.client.resource.language.I18n
import net.minecraft.entity.player.PlayerBase
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemInstance
import net.minecraft.tileentity.TileEntityBase
import net.modificationstation.stationapi.api.network.packet.PacketHelper
import org.lwjgl.opengl.GL11
import org.lwjgl.util.Color

class EditLinkGui(player: PlayerBase, dummyInventory: DummyEditLinkEntity) : ContainerBase(
    EditLinkStorage(dummyInventory.inventory ?: throw Error("Why did you pass null to EditLinkGui?"), player.inventory)
) {
    private inline val linkInfo: LinkConnectionInfo
        get() = LinkClient.currentEntityData!!

    private lateinit var nameBox: Textbox

    private val renderX: Int
        get() = (width - containerWidth) / 2

    private val renderY: Int
        get() = (height - containerHeight) / 2

    override fun init() {
        containerHeight = 222

        nameBox = Textbox(this, this.textManager, 6, 48, 64, 20, linkInfo.name).apply {
            selected = true
            setMaxLength(16)
        }
    }

    override fun renderContainerBackground(f: Float) {
        val textureId = minecraft.textureManager.getTextureId("/assets/link/gui/link_terminal_gui.png")
        GL11.glColor4f(1f, 1f, 1f, 1f)
        minecraft.textureManager.bindTexture(textureId)

        // Background
        blit(renderX, renderY, 0, 0, containerWidth, 223)
    }

    override fun renderForeground() {
        I18n.translate("gui.link:edit_link").let { text ->
            textManager.drawText(text, (containerWidth / 2) - (textManager.getTextWidth(text) / 2), 6, 4210752)
        }
        textManager.drawText(linkInfo.color.toString(), 8, 24, 4210752)

        nameBox.draw()
    }

    override fun keyPressed(c: Char, i: Int) {
        if (c == '\r') {
            nameBox.selected = false
        } else if (c != '' && nameBox.selected) {
            nameBox.keyPressed(c, i)
        } else {
            super.keyPressed(c, i)
        }
    }

    override fun mouseClicked(i: Int, j: Int, k: Int) {
        super.mouseClicked(i, j, k)
        nameBox.mouseClicked(i - renderX, j - renderY, k)
    }

    override fun onClose() {
        linkInfo.name = nameBox.text
        PacketHelper.send(UpdateLinkInfoPacket(linkInfo))
        super.onClose()
    }
}

class EditLinkStorage(val inventory: HasLinkInfo, val playerInventory: PlayerInventory) :
    net.minecraft.container.ContainerBase() {
    override fun canUse(arg: PlayerBase?): Boolean = true
}

class DummyEditLinkEntity(val inventory: HasLinkInfo?) : TileEntityBase(), HasLinkInfo {
    override fun getInventorySize(): Int = 0

    override fun getInventoryItem(i: Int): ItemInstance? = null

    override fun takeInventoryItem(i: Int, j: Int): ItemInstance? = null

    override fun setInventoryItem(i: Int, arg: ItemInstance?) = Unit

    override fun getContainerName(): String? = null

    override fun getMaxItemCount(): Int = 0

    override fun canPlayerUse(arg: PlayerBase?): Boolean = true
    override fun getLinkName(): String = ""

    override fun setLinkName(name: String?) = Unit

    override fun getLinkColor(): Color = Color(0, 0, 0)

    override fun setLinkColor(color: Color?) = Unit

    override fun openLinkMenu(player: PlayerBase?) = Unit
}