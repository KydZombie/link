package com.github.kydzombie.link.gui;

import com.github.kydzombie.link.block.HasLinkInfo;
import com.github.kydzombie.link.block.LinkTerminalEntity;
import com.github.kydzombie.link.slot.LinkCardSlot;
import com.github.kydzombie.link.util.Vector2i;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.tileentity.TileEntityBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class LinkTerminalGui extends ContainerBase {
    private float mouseX;
    private float mouseY;

    private final PlayerBase player;
    private final LinkTerminalEntity entity;

    private static final Vector2i CORNER_OFFSET = new Vector2i(7, 7 + 20);

    boolean linkCardsMenuOpen = false;

    private static final float ANIMATION_TIME = 1.5f;
    float animationTimer = 0f;
    private static final Rectangle LINK_CARDS_MENU = new Rectangle(228, 0, 28, 128);

    private static final Rectangle OPEN_CARDS_ARROW = new Rectangle(236, 128, 10, 35);
    private static final Rectangle CLOSE_CARDS_ARROW = new Rectangle(246, 128, 10, 35);
    private static final int SELECTED_ARROW_OFFSET = 35;

    public static final int BUTTON_SIZE = 22;
    private static final int BUTTON_MARGIN = 10;

    private long lastFrameTime = System.currentTimeMillis();
    private float deltaTime;

    public LinkTerminalGui(PlayerBase player, LinkTerminalEntity entity) {
        super(new LinkTerminalStorage(player, entity));
        this.player = player;
        this.entity = entity;

        containerHeight = 222;
    }

    private void updateDeltaTime() {
        var now = System.currentTimeMillis();
        deltaTime = 1f / (now - lastFrameTime);
        lastFrameTime = now;
    }

    public void render(int mouseX, int mouseY, float f) {
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;

        updateDeltaTime();

        if (linkCardsMenuOpen) {
            if (animationTimer < ANIMATION_TIME) {
                animationTimer += deltaTime;
                if (animationTimer > ANIMATION_TIME) {
                    for (Object object : (container).slots) {
                        if (object instanceof LinkCardSlot slot) {
                            slot.x = LinkTerminalStorage.LINK_CARD_X;
                        }
                    }
                    animationTimer = ANIMATION_TIME;
                }
            }
        } else {
            if (animationTimer > 0) {
                for (Object object : (container).slots) {
                    if (object instanceof LinkCardSlot slot) {
                        slot.x = 10000;
                    }
                }
                animationTimer -= deltaTime;
                if (animationTimer < 0) {
                    animationTimer = 0;
                }
            }
        }

        super.render(mouseX, mouseY, f);
    }

    @Override
    protected void renderForeground() {
        textManager.drawText(entity.getContainerName(), 8, 6, 4210752);
        textManager.drawText(player.inventory.getContainerName(), 8, this.containerHeight - 96 + 2, 4210752);

        int renderX = (width - containerWidth) / 2;
        int renderY = (height - containerHeight) / 2;

        int linkCardsMenuOffset = Math.round(LINK_CARDS_MENU.width * (animationTimer / ANIMATION_TIME));

        // Link Buttons
        int maxPerRow = (containerWidth - CORNER_OFFSET.x() - linkCardsMenuOffset) / (BUTTON_SIZE + BUTTON_MARGIN);
        TileEntityBase[] connections = entity.getConnections();
        for (int i = 0; i < connections.length; i++) {
            TileEntityBase connection = connections[i];
            if (connection instanceof HasLinkInfo linkInfo) {
                int buttonX = CORNER_OFFSET.x() + ((i % maxPerRow) * (BUTTON_SIZE + BUTTON_MARGIN));
                int buttonY = CORNER_OFFSET.y() + ((i / maxPerRow) * (BUTTON_SIZE + BUTTON_MARGIN));
                drawTextWithShadowCentred(textManager, linkInfo.getLinkName(), buttonX + (BUTTON_SIZE / 2), buttonY - 9, Integer.MAX_VALUE);
            }
        }
    }

    @Override
    protected void renderContainerBackground(float f) {
        int textureId = minecraft.textureManager.getTextureId("/assets/link/gui/link_terminal_gui.png");
        GL11.glColor4f(1f, 1f, 1f, 1f);
        minecraft.textureManager.bindTexture(textureId);
        int renderX = (width - containerWidth) / 2;
        int renderY = (height - containerHeight) / 2;

        // Background

        blit(renderX, renderY, 0, 0, this.containerWidth, 223);

        // Link Cards

        int linkCardsMenuOffset = Math.round(LINK_CARDS_MENU.width * (animationTimer / ANIMATION_TIME));
        blit(renderX + 143 + LINK_CARDS_MENU.width - linkCardsMenuOffset, renderY + 5, LINK_CARDS_MENU.x, LINK_CARDS_MENU.y, linkCardsMenuOffset, LINK_CARDS_MENU.height);


        // Render link card open/close arrow
        int arrowX = (linkCardsMenuOpen ? renderX + 131 + LINK_CARDS_MENU.width : renderX + 161) - linkCardsMenuOffset;
        var arrowY = renderY + 53;
        boolean hoveringArrow =
                mouseX > arrowX && mouseX < arrowX + OPEN_CARDS_ARROW.width &&
                        mouseY > arrowY && mouseY < arrowY + OPEN_CARDS_ARROW.height;
        if (linkCardsMenuOpen) {
            blit(arrowX, arrowY, CLOSE_CARDS_ARROW.x, hoveringArrow ? CLOSE_CARDS_ARROW.y + SELECTED_ARROW_OFFSET : CLOSE_CARDS_ARROW.y, CLOSE_CARDS_ARROW.width, CLOSE_CARDS_ARROW.height);
        } else {
            blit(arrowX, arrowY, OPEN_CARDS_ARROW.x, hoveringArrow ? OPEN_CARDS_ARROW.y + SELECTED_ARROW_OFFSET : OPEN_CARDS_ARROW.y, OPEN_CARDS_ARROW.width, OPEN_CARDS_ARROW.height);
        }

        // Link Buttons
        int maxPerRow = (containerWidth - CORNER_OFFSET.x() - linkCardsMenuOffset) / (BUTTON_SIZE + BUTTON_MARGIN);
        TileEntityBase[] connections = entity.getConnections();
        for (int i = 0; i < connections.length; i++) {
            TileEntityBase connection = connections[i];
            if (connection instanceof HasLinkInfo linkInfo) {
                int buttonX = renderX + CORNER_OFFSET.x() + ((i % maxPerRow) * (BUTTON_SIZE + BUTTON_MARGIN));
                int buttonY = renderY + CORNER_OFFSET.y() + ((i / maxPerRow) * (BUTTON_SIZE + BUTTON_MARGIN));
                var selected = mouseX > buttonX && mouseX < buttonX + BUTTON_SIZE &&
                        mouseY > buttonY && mouseY < buttonY + BUTTON_SIZE;
                linkInfo.renderLinkButton(buttonX, buttonY, selected, this);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        System.out.println("mouseButton = " + mouseButton);
        int renderX = (width - containerWidth) / 2;
        int renderY = (height - containerHeight) / 2;
        int linkCardsMenuOffset = Math.round(LINK_CARDS_MENU.width * (animationTimer / ANIMATION_TIME));
        int arrowX = (linkCardsMenuOpen ? renderX + 131 + LINK_CARDS_MENU.width : renderX + 161) - linkCardsMenuOffset;
        var arrowY = renderY + 53;
        if (mouseX > arrowX && mouseX < arrowX + OPEN_CARDS_ARROW.width &&
                mouseY > arrowY && mouseY < arrowY + OPEN_CARDS_ARROW.height) {
            linkCardsMenuOpen = !linkCardsMenuOpen;
        }

        int maxPerRow = (containerWidth - CORNER_OFFSET.x() - linkCardsMenuOffset) / (BUTTON_SIZE + BUTTON_MARGIN);
        TileEntityBase[] connections = entity.getConnections();
        for (int i = 0; i < connections.length; i++) {
            TileEntityBase connection = connections[i];
            if (connection instanceof HasLinkInfo linkInfo) {
                int buttonX = renderX + CORNER_OFFSET.x() + ((i % maxPerRow) * (BUTTON_SIZE + BUTTON_MARGIN));
                int buttonY = renderY + CORNER_OFFSET.y() + ((i / maxPerRow) * (BUTTON_SIZE + BUTTON_MARGIN));
                if (mouseX > buttonX && mouseX < buttonX + BUTTON_SIZE && mouseY > buttonY && mouseY < buttonY + BUTTON_SIZE) {
                    if (mouseButton == 0) {
                        linkInfo.openLinkMenu(player);
                    } else if (mouseButton == 1) {
                        // TODO Add name edit menu
                    }
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
