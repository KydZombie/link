package io.github.kydzombie.link.block.entity;

import net.modificationstation.stationapi.api.util.Identifier;

public interface HasLinkConnectionInfo {
    String getLinkName();

    Identifier getLinkIcon();

    int getLinkColor();
}
