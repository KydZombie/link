package com.github.kydzombie.link.registry;

import com.github.kydzombie.link.Link;
import com.mojang.serialization.Lifecycle;
import net.modificationstation.stationapi.api.registry.Registries;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.registry.RegistryKey;
import net.modificationstation.stationapi.api.registry.SimpleRegistry;

public class LinkIconRegistry extends SimpleRegistry<LinkIcon> {
    public static final LinkIcon UNKNOWN_ICON = new LinkIcon(176, 44);
    public static final RegistryKey<Registry<LinkIcon>> KEY = RegistryKey.ofRegistry(Link.MOD_ID.id("icons"));
    public static final LinkIconRegistry INSTANCE = Registries.create(KEY, new LinkIconRegistry(), registry -> UNKNOWN_ICON, Lifecycle.experimental());

    public LinkIconRegistry() {
        super(KEY, Lifecycle.experimental());
    }
}
