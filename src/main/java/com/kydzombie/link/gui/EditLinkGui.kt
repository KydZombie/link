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
    private lateinit var redSlider: RGBSlider
    private lateinit var greenSlider: RGBSlider
    private lateinit var blueSlider: RGBSlider

    private val renderX: Int
        get() = (width - containerWidth) / 2

    private val renderY: Int
        get() = (height - containerHeight) / 2

    override fun init() {
        containerHeight = 146

        nameBox =
            Textbox(this, this.textManager, 16 + (22 * 2) + 16, 16 + 22 - 10, 64, 20, linkInfo.name).apply {
                setMaxLength(16)
            }

        val buttonY = renderY + 80

        redSlider = RGBSlider(
            0, renderX + 6, buttonY, "Red", linkInfo.color.red,
            SLIDER_WIDTH
        )
        buttons.add(redSlider)
        greenSlider =
            RGBSlider(
                1, renderX + containerWidth - SLIDER_WIDTH - 6, buttonY, "Green", linkInfo.color.green,
                SLIDER_WIDTH
            )
        buttons.add(greenSlider)
        blueSlider =
            RGBSlider(
                2,
                renderX + (containerWidth / 2) - (SLIDER_WIDTH / 2),
                buttonY + 20 + 6,
                "Blue",
                linkInfo.color.blue,
                SLIDER_WIDTH
            )
        buttons.add(blueSlider)
    }

    override fun renderContainerBackground(f: Float) {
        GL11.glColor4f(1f, 1f, 1f, 1f)
        minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("/assets/link/gui/link_edit_gui.png"))

        // Background
        blit(renderX, renderY, 0, 0, containerWidth, 146)

        GL11.glColor3ub(linkInfo.color.redByte, linkInfo.color.greenByte, linkInfo.color.blueByte)
        GL11.glEnable(GL11.GL_BLEND)

        // Color overlay
        blit(renderX, renderY + (146 - 110), 0, 146, containerWidth, 110)

        GL11.glDisable(GL11.GL_BLEND)

        GL11.glColor4f(1f, 1f, 1f, 1f)

        minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("/assets/link/gui/link_terminal_gui.png"))
        linkInfo.linkIcon.render(renderX + 16, renderY + 16, false, linkInfo.color, this, 2f)
    }

    override fun renderForeground() {
        I18n.translate("gui.link:edit_link").let { text ->
            textManager.drawText(text, (containerWidth / 2) - (textManager.getTextWidth(text) / 2), 6, 4210752)
        }
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

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        nameBox.mouseClicked(mouseX - renderX, mouseY - renderY, button)
        super.mouseClicked(mouseX, mouseY, button)
        linkInfo.color.red = redSlider.value
        linkInfo.color.green = greenSlider.value
        linkInfo.color.blue = blueSlider.value
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        if (button != -1) {
            redSlider.mouseReleased(mouseX, mouseY)
            greenSlider.mouseReleased(mouseX, mouseY)
            blueSlider.mouseReleased(mouseX, mouseY)
        }
        linkInfo.color.red = redSlider.value
        linkInfo.color.green = greenSlider.value
        linkInfo.color.blue = blueSlider.value
        super.mouseReleased(mouseX, mouseY, button)
    }

    override fun onClose() {
        linkInfo.name = nameBox.text
        linkInfo.color.red = redSlider.value
        linkInfo.color.green = greenSlider.value
        linkInfo.color.blue = blueSlider.value
        PacketHelper.send(UpdateLinkInfoPacket(linkInfo))
        super.onClose()
    }

    companion object {
        private const val SLIDER_WIDTH = 80
    }
}

class EditLinkStorage(inventory: HasLinkInfo, playerInventory: PlayerInventory) :
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