package com.kydzombie.link.block;

import com.kydzombie.link.Link;
import com.kydzombie.link.util.LinkConnectionInfo;
import net.minecraft.entity.player.PlayerBase;
import net.modificationstation.stationapi.api.util.Identifier;
import org.lwjgl.util.Color;

public interface HasLinkInfo {
    String link$getLinkName();

    void link$setLinkName(String name);

    Color link$getColor();

    void link$setColor(Color color);

    void link$openLinkMenu(PlayerBase player);

    default Identifier link$getLinkIconId() {
        return Link.NAMESPACE.id("unknown");
    }

    default LinkConnectionInfo getLinkConnectionInfo() {
        return new LinkConnectionInfo(link$getLinkIconId(), link$getLinkName(), link$getColor());
    }
}
