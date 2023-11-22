package com.kydzombie.link.compat

import com.kydzombie.link.Link
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.item.ItemInstance
import paulevs.bhcreative.api.CreativeTab
import paulevs.bhcreative.api.SimpleTab
import paulevs.bhcreative.registry.TabRegistryEvent

object BHCreativeCompat {
    private lateinit var tab: CreativeTab

    @EventListener
    fun onTabInit(event: TabRegistryEvent) {
        tab = SimpleTab(Link.NAMESPACE.id("all"), ItemInstance(Link.linkTerminal))
        event.register(tab)
        tab.addItem(ItemInstance(Link.linkTerminal))
        tab.addItem(ItemInstance(Link.linkCable))
        tab.addItem(ItemInstance(Link.linkConnector))
        tab.addItem(ItemInstance(Link.linkCard))
    }
}