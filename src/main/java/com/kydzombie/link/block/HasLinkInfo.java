package com.kydzombie.link.block;

import com.kydzombie.link.Link;
import com.kydzombie.link.gui.DummyEditLinkEntity;
import com.kydzombie.link.gui.EditLinkStorage;
import com.kydzombie.link.util.LinkConnectionInfo;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.InventoryBase;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import org.lwjgl.util.Color;

public interface HasLinkInfo extends InventoryBase {
    String getLinkName();

    void setLinkName(String name);

    Color getLinkColor();

    void setLinkColor(Color color);

    void openLinkMenu(PlayerBase player);

    default void openEditMenu(PlayerBase player) {
        GuiHelper.openGUI(player, Link.NAMESPACE.id("edit_link"), this, new EditLinkStorage(new DummyEditLinkEntity(this), player.inventory));
    }

    default Identifier getLinkIconId() {
        return Link.NAMESPACE.id("unknown");
    }

    default void setLinkIconId(Identifier identifier) {
    }

    default LinkConnectionInfo getLinkConnectionInfo() {
        return new LinkConnectionInfo(getLinkIconId(), getLinkName(), getLinkColor());
    }

    default void setLinkInfo(LinkConnectionInfo info) {
        this.setLinkIconId(info.getType());
        this.setLinkName(info.getName());
        this.setLinkColor(info.getColor());
    }
}
