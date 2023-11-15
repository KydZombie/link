package com.github.kydzombie.link.registry;

import net.mine_diver.unsafeevents.Event;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.Registry;

public class LinkIconRegistryEvent extends Event {
    public void registerLinkIcon(Identifier identifier, LinkIcon linkIcon) {
        Registry.register(LinkIconRegistry.INSTANCE, identifier, linkIcon);
    }
}
