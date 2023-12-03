package com.kydzombie.link

import com.kydzombie.link.block.*
import com.kydzombie.link.gui.AlternateChestGui
import com.kydzombie.link.gui.DummyEditLinkEntity
import com.kydzombie.link.gui.EditLinkGui
import com.kydzombie.link.gui.LinkTerminalGui
import com.kydzombie.link.item.LinkCard
import com.kydzombie.link.packet.LinkConnectionsPacket
import com.kydzombie.link.packet.OpenLinkMenuPacket
import com.kydzombie.link.packet.RequestLinkConnectionsPacket
import com.kydzombie.link.packet.UpdateLinkInfoPacket
import com.kydzombie.link.registry.LinkIcon
import com.kydzombie.link.registry.LinkIconRegistry
import com.kydzombie.link.registry.LinkIconRegistryEvent
import com.kydzombie.link.util.LinkConnectionInfo
import io.michaelrocks.bimap.HashBiMap
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.block.material.Material
import net.minecraft.client.gui.screen.ScreenBase
import net.minecraft.entity.Living
import net.minecraft.entity.player.PlayerBase
import net.minecraft.inventory.DoubleChest
import net.minecraft.inventory.InventoryBase
import net.minecraft.item.ItemInstance
import net.minecraft.level.BlockView
import net.minecraft.tileentity.TileEntityBase
import net.minecraft.tileentity.TileEntityChest
import net.modificationstation.stationapi.api.StationAPI
import net.modificationstation.stationapi.api.client.event.render.model.ItemModelPredicateProviderRegistryEvent
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent
import net.modificationstation.stationapi.api.event.mod.PostInitEvent
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint
import net.modificationstation.stationapi.api.network.packet.IdentifiablePacket
import net.modificationstation.stationapi.api.util.Namespace
import org.apache.logging.log4j.Logger
import uk.co.benjiweber.expressions.tuple.BiTuple
import java.util.function.BiFunction
import java.util.function.Supplier

object Link {
    @Entrypoint.Namespace
    lateinit var NAMESPACE: Namespace

    @Entrypoint.Logger("Link")
    lateinit var LOGGER: Logger

    @JvmStatic
    val accessing = HashBiMap<PlayerBase, TileEntityBase>()

    lateinit var linkCard: LinkCard

    @EventListener
    private fun registerItems(event: ItemRegistryEvent) {
        linkCard = LinkCard(NAMESPACE.id("link_card"))
    }

    lateinit var linkTerminal: LinkTerminal
    lateinit var linkCable: LinkCable
    lateinit var linkConnector: LinkConnector

    @EventListener
    private fun registerBlocks(event: BlockRegistryEvent) {
        linkTerminal = LinkTerminal(NAMESPACE.id("link_terminal"), Material.METAL)
        linkCable = LinkCable(NAMESPACE.id("link_cable"), Material.METAL)
        linkConnector = LinkConnector(NAMESPACE.id("link_connector"), Material.METAL)
    }

    @EventListener
    private fun registerTileEntities(event: BlockEntityRegisterEvent) {
        event.register(LinkTerminalEntity::class.java, NAMESPACE.id("link_terminal").toString())
    }

    @EventListener
    private fun registerPackets(event: PacketRegisterEvent) {
        IdentifiablePacket.register(NAMESPACE.id("link_connections"), true, false, ::LinkConnectionsPacket)
        IdentifiablePacket.register(NAMESPACE.id("open_linked_storage"), false, true, ::OpenLinkMenuPacket)
        IdentifiablePacket.register(
            NAMESPACE.id("request_link_connections"),
            false,
            true,
            ::RequestLinkConnectionsPacket
        )
        IdentifiablePacket.register(NAMESPACE.id("update_link_info"), false, true, ::UpdateLinkInfoPacket)
    }
}


@Environment(EnvType.CLIENT)
object LinkClient {
    @JvmStatic
    var currentEntityData: LinkConnectionInfo? = null

    @EventListener
    private fun registerItemModelPredicates(event: ItemModelPredicateProviderRegistryEvent) {
        event.registry.register(
            Link.linkCard, Link.NAMESPACE.id("linked")
        ) { itemInstance: ItemInstance, _: BlockView?, _: Living?, _: Int ->
            if (itemInstance.stationNbt.getBoolean("linked")) 1f else 0f
        }
    }

    @EventListener
    private fun runEvent(event: PostInitEvent) {
        StationAPI.EVENT_BUS.post(LinkIconRegistryEvent())
    }

    @EventListener
    private fun registerIcons(event: LinkIconRegistryEvent) {
        event.registerLinkIcon(Link.NAMESPACE.id("unknown"), LinkIconRegistry.UNKNOWN_ICON)
        event.registerLinkIcon(Link.NAMESPACE.id("chest"), LinkIcon(176, 66))
        event.registerLinkIcon(Link.NAMESPACE.id("double_chest"), LinkIcon(176, 88))
    }

    @EventListener
    private fun registerGuiHandler(event: GuiHandlerRegistryEvent) {
        event.registry.registerValueNoMessage(
            Link.NAMESPACE.id("link_terminal"),
            BiTuple.of<BiFunction<PlayerBase, InventoryBase, ScreenBase>, Supplier<InventoryBase>>(
                BiFunction<PlayerBase, InventoryBase, ScreenBase> { player: PlayerBase, entity: InventoryBase ->
                    openLinkTerminal(
                        player,
                        entity
                    )
                },
                Supplier<InventoryBase> { LinkTerminalEntity() })
        )
        event.registry.registerValueNoMessage(
            Link.NAMESPACE.id("edit_link"),
            BiTuple.of<BiFunction<PlayerBase, InventoryBase, ScreenBase>, Supplier<InventoryBase>>(
                BiFunction<PlayerBase, InventoryBase, ScreenBase> { player: PlayerBase, entity: InventoryBase ->
                    openLinkEditGui(
                        player,
                        DummyEditLinkEntity(entity as HasLinkInfo)
                    )
                },
                Supplier<InventoryBase> { DummyEditLinkEntity(null) }
            )
        )
        event.registry.registerValueNoMessage(
            Link.NAMESPACE.id("alternate_chest"),
            BiTuple.of<BiFunction<PlayerBase, InventoryBase, ScreenBase>, Supplier<InventoryBase>>(
                BiFunction<PlayerBase, InventoryBase, ScreenBase>(::openAlternateChestGui),
                Supplier<InventoryBase> { TileEntityChest() })
        )
        event.registry.registerValueNoMessage(
            Link.NAMESPACE.id("alternate_double_chest"),
            BiTuple.of<BiFunction<PlayerBase, InventoryBase, ScreenBase>, Supplier<InventoryBase>>(
                BiFunction<PlayerBase, InventoryBase, ScreenBase> { player: PlayerBase, entity: InventoryBase ->
                    openAlternateChestGui(
                        player,
                        entity
                    )
                },
                Supplier<InventoryBase> { DoubleChest(null, null, null) })
        )
    }

    private fun openLinkTerminal(player: PlayerBase, entity: InventoryBase): ScreenBase {
        return LinkTerminalGui(player, entity as LinkTerminalEntity)
    }

    private fun openLinkEditGui(player: PlayerBase, entity: InventoryBase): ScreenBase {
        return EditLinkGui(player, entity as DummyEditLinkEntity)
    }

    private fun openAlternateChestGui(player: PlayerBase, entity: InventoryBase): ScreenBase {
        return AlternateChestGui(player, entity)
    }
}
