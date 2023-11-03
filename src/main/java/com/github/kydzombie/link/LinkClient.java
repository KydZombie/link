package com.github.kydzombie.link;

import com.github.kydzombie.link.block.LinkTerminalEntity;
import com.github.kydzombie.link.block.LinkTerminalGui;
import com.github.kydzombie.link.item.LinkCard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.InventoryBase;
import net.modificationstation.stationapi.api.client.event.render.model.ItemModelPredicateProviderRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import uk.co.benjiweber.expressions.tuple.BiTuple;

import static com.github.kydzombie.link.Link.MOD_ID;

@Environment(EnvType.CLIENT)
public class LinkClient {
    @EventListener
    private void registerItemModelPredicates(ItemModelPredicateProviderRegistryEvent event) {
        event.registry.register(Link.LINK_CARD, MOD_ID.id("linked"),
                (itemInstance, world, entity, seed) -> itemInstance.getStationNBT().getBoolean("linked") ? 1 : 0);
    }

    @EventListener
    private void registerGuiHandler(GuiHandlerRegistryEvent event) {
        event.registry.registerValueNoMessage(MOD_ID.id("link_terminal"), BiTuple.of(this::openLinkTerminal, LinkTerminalEntity::new));
    }

    private ScreenBase openLinkTerminal(PlayerBase player, InventoryBase entity) {
        return new LinkTerminalGui(player, (LinkTerminalEntity) entity);
    }
}
