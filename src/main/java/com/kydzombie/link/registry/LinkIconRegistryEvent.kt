package com.kydzombie.link.registry

import net.mine_diver.unsafeevents.Event
import net.modificationstation.stationapi.api.registry.Identifier
import net.modificationstation.stationapi.api.registry.Registry

class LinkIconRegistryEvent : Event() {
    fun registerLinkIcon(identifier: Identifier?, linkIcon: LinkIcon) {
        Registry.register(LinkIconRegistry.INSTANCE, identifier, linkIcon)
    }
}
