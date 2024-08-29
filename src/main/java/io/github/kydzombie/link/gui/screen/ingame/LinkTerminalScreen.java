package io.github.kydzombie.link.gui.screen.ingame;

import io.github.kydzombie.link.block.entity.LinkTerminalBlockEntity;
import io.github.kydzombie.link.gui.screen.LinkTerminalScreenHandler;
import io.github.kydzombie.link.gui.screen.slot.LinkCardSlot;
import io.github.kydzombie.link.packet.RequestLinkConnectionsPacket;
import io.github.kydzombie.link.util.LinkConnectionInfo;
import io.github.kydzombie.link.util.Rectangle;
import io.github.kydzombie.link.util.Timer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class LinkTerminalScreen extends HandledScreen {
    private final PlayerEntity player;
    private final LinkTerminalBlockEntity blockEntity;

    private LinkConnectionInfo[] connections;
    boolean awaitingUpdate = false;

    int mouseX = 0;
    int mouseY = 0;

    private static final Rectangle LINK_CARDS_MENU = new Rectangle(228, 0, 28, 128);
    private static final Rectangle OPEN_ARROW = new Rectangle(236, 128, 10, 35);
    private static final Rectangle CLOSED_ARROW = new Rectangle(246, 128, 10, 35);
    private static final int SELECTED_ARROW_OFFSET = 35;

    private final Timer animationTimer = new Timer(0.1f);
    private boolean linkCardsMenuOpen = false;

    private TextFieldWidget searchBox;

    public LinkTerminalScreen(PlayerEntity player, LinkTerminalBlockEntity blockEntity) {
        super(new LinkTerminalScreenHandler(player, blockEntity));
        this.player = player;
        this.blockEntity = blockEntity;
        backgroundHeight = 223;
//        ArrayList<LinkConnectionInfo> tempConnections = new ArrayList<>();
//        for (ItemStack stack : blockEntity.getStacks()) {
//            if (stack == null) continue;
//            if (stack.getItem() instanceof LinkCardItem) {
//                LinkConnectionInfo connectionInfo = LinkCardItem.getConnectionInfo(stack);
//                if (connectionInfo != null && connectionInfo.status() == LinkCardItem.LinkStatus.VALID) {
//                    tempConnections.add(connectionInfo);
//                }
//            }
//        }
//        tempConnections.addAll(List.of(blockEntity.connections));
//        connections = tempConnections.toArray(LinkConnectionInfo[]::new);
    }

    public void requestConnections() {
        PacketHelper.send(new RequestLinkConnectionsPacket());
    }

    public void setConnections(LinkConnectionInfo[] connections) {
        this.connections = connections;
        awaitingUpdate = false;
        System.out.println("Screen: This is what I got:");
        for (LinkConnectionInfo connection : connections) {
            System.out.println("connection = " + connection);
        }
    }

    @Override
    public void init() {
        super.init();
        searchBox = new TextFieldWidget(this, textRenderer, 75, 5, 64, 10, "");
    }

    private int getRenderX() {
        return (width - backgroundWidth) / 2;
    }

    private int getRenderY() {
        return (height - backgroundHeight) / 2;
    }

    private int getLinkCardsMenuOffset() {
        if (linkCardsMenuOpen) {
            return Math.round(LINK_CARDS_MENU.width() * animationTimer.getPercent());
        } else {
            return Math.round(LINK_CARDS_MENU.width() * (1 - animationTimer.getPercent()));
        }
    }

    private int getArrowX() {
        return (linkCardsMenuOpen ? getRenderX() + 131 + LINK_CARDS_MENU.width() : getRenderX() + 161) - getLinkCardsMenuOffset();
    }

    private int getArrowY() {
        return getRenderY() + 53;
    }

    private boolean isHoveringArrow() {
        return mouseX > getArrowX() &&
                mouseX < getArrowX() + OPEN_ARROW.width() &&
                mouseY > getArrowY() &&
                mouseY < getArrowY() + OPEN_ARROW.height();
    }

    private void bindGuiTexture() {
        int textureId = minecraft.textureManager.getTextureId("/assets/link/gui/link_terminal_gui.png");
        minecraft.textureManager.bindTexture(textureId);
    }

    private void drawLinkName() {
        if (connections == null) return;

        int button = 0;
        for (LinkConnectionInfo connection : connections) {
            final int maxPerRow = (backgroundWidth - CORNER_OFFSET_X - getLinkCardsMenuOffset()) / (BUTTON_SIZE + BUTTON_MARGIN);
            if (!searchBox.getText().isBlank()) {
                if (!connection.name().toLowerCase().contains(searchBox.getText().toLowerCase())) {
                    continue;
                }
            }

            final int buttonX = getRenderX() + CORNER_OFFSET_X + button % maxPerRow * (BUTTON_SIZE + BUTTON_MARGIN);
            final int buttonY = getRenderY() + CORNER_OFFSET_Y + button / maxPerRow * (BUTTON_SIZE + BUTTON_MARGIN);
            final boolean selected = mouseX > buttonX && mouseX < buttonX + BUTTON_SIZE && mouseY > buttonY && mouseY < buttonY + BUTTON_SIZE;

            if (selected) {
                drawCenteredTextWithShadow(textRenderer, connection.name(), mouseX - getRenderX(), mouseY - getRenderY() - 12, 0xFFFFFF);
                break;
            }

            button++;
        }
    }

    @Override
    protected void drawForeground() {
        super.drawForeground();
        textRenderer.draw(blockEntity.getName(), 8, 6, 0x404040);
        textRenderer.draw(player.inventory.getName(), 8, backgroundHeight - 96 + 2, 0x404040);

        searchBox.render();

        bindGuiTexture();

        drawLinkName();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        searchBox.mouseClicked(mouseX - getRenderX(), mouseY - getRenderY(), button);
        super.mouseClicked(mouseX, mouseY, button);
        if (isHoveringArrow()) {
            linkCardsMenuOpen = !linkCardsMenuOpen;
//            searchBox.enabled = !linkCardsMenuOpen;
            animationTimer.start();
        }
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        if (character == '\r') {
            searchBox.setFocused(false);
        } else if (character != '' && searchBox.focused) {
            searchBox.keyPressed(character, keyCode);
        } else {
            super.keyPressed(character, keyCode);
        }
    }

    private void drawLinkCardsMenu() {
        GL11.glColor4f(1, 1, 1, 1);
        int linkCardsMenuOffset = getLinkCardsMenuOffset();
        drawTexture(
                getRenderX() + 143 + LINK_CARDS_MENU.width() - linkCardsMenuOffset,
                getRenderY() + 5,
                LINK_CARDS_MENU.x(),
                LINK_CARDS_MENU.y(),
                linkCardsMenuOffset,
                LINK_CARDS_MENU.height()
        );
    }

    private void drawArrows() {
        GL11.glColor4f(1, 1, 1, 1);
        Rectangle arrow = linkCardsMenuOpen ? CLOSED_ARROW : OPEN_ARROW;

        drawTexture(
                getArrowX(),
                getArrowY(),
                arrow.x(),
                arrow.y() + (isHoveringArrow() ? SELECTED_ARROW_OFFSET : 0),
                arrow.width(),
                arrow.height()
        );
    }

    final static int BUTTON_SIZE = 22;
    final static int BUTTON_MARGIN = 5;
    final static int CORNER_OFFSET_X = 7;
    final static int CORNER_OFFSET_Y = 20;

    private void drawLinkButtons() {
        if (connections == null) return;

        final int maxPerRow = (backgroundWidth - CORNER_OFFSET_X - getLinkCardsMenuOffset()) / (BUTTON_SIZE + BUTTON_MARGIN);
        int button = 0;
        for (LinkConnectionInfo connection : connections) {
            if (!searchBox.getText().isBlank()) {
                if (!connection.name().toLowerCase().contains(searchBox.getText().toLowerCase())) {
                    continue;
                }
            }

            final int buttonX = getRenderX() + CORNER_OFFSET_X + button % maxPerRow * (BUTTON_SIZE + BUTTON_MARGIN);
            final int buttonY = getRenderY() + CORNER_OFFSET_Y + button / maxPerRow * (BUTTON_SIZE + BUTTON_MARGIN);
            final boolean selected = mouseX > buttonX && mouseX < buttonX + BUTTON_SIZE && mouseY > buttonY && mouseY < buttonY + BUTTON_SIZE;

            Color color = new Color(connection.color());
            GL11.glColor3b((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
            GL11.glTranslatef(buttonX, buttonY, 0);
            final int background_texture_x = 176;
            final int background_texture_y = selected ? 22 : 0;
            drawTexture(0, 0, background_texture_x, background_texture_y, BUTTON_SIZE, BUTTON_SIZE);

            GL11.glColor3f(1, 1, 1);
            final int icon_texture_x = 176;
            final int icon_texture_y = 66;
            drawTexture(0, 0, icon_texture_x, icon_texture_y, BUTTON_SIZE, BUTTON_SIZE);

            GL11.glTranslatef(-buttonX, -buttonY, 0);
            button++;
        }
    }

    @Override
    protected void drawBackground(float tickDelta) {
        bindGuiTexture();
        GL11.glColor4f(1, 1, 1, 1);

        drawTexture(getRenderX(), getRenderY(), 0, 0, backgroundWidth, backgroundHeight);

        drawLinkCardsMenu();
        drawArrows();
        drawLinkButtons();
    }

    private void updateLinkCardSlots() {
        if (linkCardsMenuOpen) {
            if (animationTimer.getPercent() == 1f) {
                //noinspection unchecked
                for (Slot slot : (List<Slot>) container.slots) {
                    if (slot instanceof LinkCardSlot) {
                        slot.x = 149;
                    }
                }
            }
        } else {
            //noinspection unchecked
            for (Slot slot : (List<Slot>) container.slots) {
                if (slot instanceof LinkCardSlot) {
                    slot.x = 10000;
                }
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        updateLinkCardSlots();

        if (connections == null && !awaitingUpdate) {
            requestConnections();
        }

        super.render(mouseX, mouseY, delta);
    }
}