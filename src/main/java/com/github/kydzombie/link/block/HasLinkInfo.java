package com.github.kydzombie.link.block;

import com.github.kydzombie.link.Link;
import com.github.kydzombie.link.util.LinkConnectionInfo;
import net.minecraft.entity.player.PlayerBase;
import net.modificationstation.stationapi.api.registry.Identifier;
import org.lwjgl.util.Color;

public interface HasLinkInfo {
    String getLinkName();

    void setLinkName(String name);

    Color getColor();

    void setColor(Color color);

    void openLinkMenu(PlayerBase player);

    default Identifier getLinkIconId() {
        return Link.MOD_ID.id("unknown");
    }

    default LinkConnectionInfo getLinkConnectionInfo() {
        return new LinkConnectionInfo(getLinkIconId(), getLinkName(), getColor());
    }
}
