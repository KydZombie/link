package com.github.kydzombie.link.util;

import com.github.kydzombie.link.registry.LinkIcon;
import com.github.kydzombie.link.registry.LinkIconRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.Color;

public record LinkConnectionInfo(@NotNull Identifier type, @NotNull String name, @NotNull Color color) {
    public LinkIcon getLinkIcon() {
        return LinkIconRegistry.INSTANCE.getOrEmpty(type).orElse(LinkIconRegistry.UNKNOWN_ICON);
    }
}
