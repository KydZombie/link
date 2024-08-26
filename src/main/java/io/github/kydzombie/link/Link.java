package io.github.kydzombie.link;

import io.github.kydzombie.link.block.LinkCableBlock;
import io.github.kydzombie.link.block.LinkConnectorBlock;
import io.github.kydzombie.link.block.LinkTerminalBlock;
import io.github.kydzombie.link.block.entity.LinkTerminalBlockEntity;
import io.github.kydzombie.link.item.LinkCardItem;
import io.github.kydzombie.link.network.LinkNetwork;
import net.danygames2014.nyalib.event.NetworkTypeRegistryEvent;
import net.danygames2014.nyalib.network.NetworkType;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import org.apache.logging.log4j.Logger;

public class Link {
    @Entrypoint.Namespace
    public static final Namespace NAMESPACE = Null.get();

    @Entrypoint.Logger
    public static final Logger LOGGER = Null.get();

    public static LinkCardItem linkCard;

    @EventListener
    private void registerItems(ItemRegistryEvent event) {
        linkCard = new LinkCardItem(NAMESPACE.id("link_card"));
    }

    public static Block linkTerminal;
    public static Block linkCable;
    public static Block linkConnector;

    @EventListener
    private void registerBlocks(BlockRegistryEvent event) {
        LOGGER.info("Registering blocks...");
        linkTerminal = new LinkTerminalBlock(NAMESPACE.id("link_terminal"));
        linkCable = new LinkCableBlock(NAMESPACE.id("link_cable"));
        linkConnector = new LinkConnectorBlock(NAMESPACE.id("link_connector"));
    }

    @EventListener
    private void registerBlockEntities(BlockEntityRegisterEvent event) {
        event.register(LinkTerminalBlockEntity.class, NAMESPACE.id("link_terminal").toString());
    }

    public static NetworkType linkNetwork;

    @EventListener
    private void registerNetworkTypes(NetworkTypeRegistryEvent event) {
        event.register(linkNetwork = new NetworkType(NAMESPACE.id("link"), LinkNetwork.class));
    }
}
