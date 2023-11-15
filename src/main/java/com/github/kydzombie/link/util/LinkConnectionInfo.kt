package com.github.kydzombie.link.util

import com.github.kydzombie.link.registry.LinkIcon
import com.github.kydzombie.link.registry.LinkIconRegistry
import net.modificationstation.stationapi.api.registry.Identifier
import org.lwjgl.util.Color

@JvmRecord
data class LinkConnectionInfo(val type: Identifier, val name: String, val color: Color) {
    val linkIcon: LinkIcon
        get() = LinkIconRegistry.INSTANCE.getOrEmpty(type).orElse(LinkIconRegistry.UNKNOWN_ICON)
}
