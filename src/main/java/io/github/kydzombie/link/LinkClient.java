package io.github.kydzombie.link;

import io.github.kydzombie.link.block.entity.LinkTerminalBlockEntity;
import io.github.kydzombie.link.gui.screen.ingame.LinkTerminalScreen;
import io.github.kydzombie.link.item.LinkCardItem;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.client.event.render.model.ItemModelPredicateProviderRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import uk.co.benjiweber.expressions.tuple.BiTuple;

public class LinkClient {
    @EventListener
    private void registerItemModelPredicated(ItemModelPredicateProviderRegistryEvent event) {
        event.registry.register(
                Link.linkCard,
                Link.NAMESPACE.id("link:status"),
                (itemStack, blockView, livingEntity, seed) ->
                        LinkCardItem.getLinkStatus(itemStack).predicateValue
        );
    }

    @EventListener
    private void registerGuiHandlers(GuiHandlerRegistryEvent event) {
        event.registry.registerValueNoMessage(Link.NAMESPACE.id("link_terminal"), BiTuple.of(this::openLinkTerminal, LinkTerminalBlockEntity::new));
    }

    private Screen openLinkTerminal(PlayerEntity player, Inventory inventory) {
        return new LinkTerminalScreen(player, (LinkTerminalBlockEntity) inventory);
    }
}
