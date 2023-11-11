package com.github.kydzombie.link.gui;

import com.github.kydzombie.link.LinkClient;
import com.github.kydzombie.link.block.HasLinkInfo;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryBase;
import org.lwjgl.opengl.GL11;

public class AlternateChestGui extends ContainerBase {
    private PlayerInventory playerInventory;
    private InventoryBase inventory;
    private int rows = 0;

    public AlternateChestGui(PlayerBase player, InventoryBase inventory) {
        super(new AlternateChestStorage(player.inventory, inventory));
        playerInventory = player.inventory;
        this.inventory = inventory;
        passEvents = false;
        short var3 = 222;
        int var4 = var3 - 108;
        this.rows = inventory.getInventorySize() / 9;
        this.containerHeight = var4 + this.rows * 18;
    }

    @Override
    protected void renderForeground() {
        textManager.drawText(((HasLinkInfo) inventory).getLinkName(), 8, 6, 4210752);
        textManager.drawText(playerInventory.getContainerName(), 8, this.containerHeight - 96 + 2, 4210752);
    }

    @Override
    protected void renderContainerBackground(float f) {
        int var2 = minecraft.textureManager.getTextureId("/gui/container.png");
        var color = LinkClient.currentlySelectedColor;
        GL11.glColor4ub(color.getRedByte(), color.getGreenByte(), color.getBlueByte(), color.getAlphaByte());
        minecraft.textureManager.bindTexture(var2);
        int var3 = (width - this.containerWidth) / 2;
        int var4 = (height - this.containerHeight) / 2;
        this.blit(var3, var4, 0, 0, this.containerWidth, rows * 18 + 17);
        this.blit(var3, var4 + rows * 18 + 17, 0, 126, this.containerWidth, 96);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
