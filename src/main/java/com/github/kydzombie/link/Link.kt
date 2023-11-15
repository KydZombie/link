package com.github.kydzombie.link

import com.github.kydzombie.link.block.LinkCable
import com.github.kydzombie.link.block.LinkConnector
import com.github.kydzombie.link.block.LinkTerminal
import com.github.kydzombie.link.block.LinkTerminalEntity
import com.github.kydzombie.link.gui.AlternateChestGui
import com.github.kydzombie.link.gui.LinkTerminalGui
import com.github.kydzombie.link.item.LinkCard
import com.github.kydzombie.link.packet.LinkConnectionsPacket
import com.github.kydzombie.link.packet.OpenLinkedStoragePacket
import com.github.kydzombie.link.registry.LinkIcon
import com.github.kydzombie.link.registry.LinkIconRegistry
import com.github.kydzombie.link.registry.LinkIconRegistryEvent
import io.michaelrocks.bimap.AbstractBiMap
import io.michaelrocks.bimap.HashBiMap
import io.michaelrocks.bimap.MutableBiMap
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.block.BlockBase
import net.minecraft.block.material.Material
import net.minecraft.client.gui.screen.ScreenBase
import net.minecraft.entity.Living
import net.minecraft.entity.player.PlayerBase
import net.minecraft.inventory.DoubleChest
import net.minecraft.inventory.InventoryBase
import net.minecraft.item.ItemBase
import net.minecraft.item.ItemInstance
import net.minecraft.level.BlockView
import net.minecraft.tileentity.TileEntityChest
import net.modificationstation.stationapi.api.StationAPI
import net.modificationstation.stationapi.api.client.event.render.model.ItemModelPredicateProviderRegistryEvent
import net.modificationstation.stationapi.api.event.mod.PostInitEvent
import net.modificationstation.stationapi.api.event.packet.PacketRegisterEvent
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent
import net.modificationstation.stationapi.api.event.tileentity.TileEntityRegisterEvent
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint
import net.modificationstation.stationapi.api.packet.IdentifiablePacket
import net.modificationstation.stationapi.api.registry.ModID
import org.apache.logging.log4j.Logger
import org.lwjgl.util.Color
import uk.co.benjiweber.expressions.tuple.BiTuple
import java.util.function.BiFunction
import java.util.function.Supplier

object Link {
    @Entrypoint.ModID
    lateinit var MOD_ID: ModID

    @Entrypoint.Logger("Link")
    lateinit var LOGGER: Logger

    @JvmStatic
    val accessing = HashBiMap<PlayerBase, LinkTerminalEntity>()

    lateinit var LINK_CARD: ItemBase

    @EventListener
    private fun registerItems(event: ItemRegistryEvent) {
        LINK_CARD = LinkCard(MOD_ID.id("link_card"))
    }

    private lateinit var LINK_TERMINAL: BlockBase
    private lateinit var LINK_CABLE: BlockBase
    lateinit var LINK_CONNECTOR: LinkConnector

    @EventListener
    private fun registerBlocks(event: BlockRegistryEvent) {
        LINK_TERMINAL = LinkTerminal(MOD_ID.id("link_terminal"), Material.METAL)
        LINK_CABLE = LinkCable(MOD_ID.id("link_cable"), Material.METAL)
        LINK_CONNECTOR = LinkConnector(MOD_ID.id("link_connector"), Material.METAL)
    }

    @EventListener
    private fun registerTileEntities(event: TileEntityRegisterEvent) {
        event.register(LinkTerminalEntity::class.java, MOD_ID.id("link_terminal").toString())
    }

    @EventListener
    private fun registerPackets(event: PacketRegisterEvent) {
        IdentifiablePacket.create(
            MOD_ID.id("link_connections"), true, false
        ) { LinkConnectionsPacket() }
        IdentifiablePacket.create(
            MOD_ID.id("open_linked_storage"), false, true
        ) { OpenLinkedStoragePacket() }
    }
}


@Environment(EnvType.CLIENT)
object LinkClient {
    @JvmStatic
    var currentlySelectedColor = Color.WHITE as Color

    @EventListener
    private fun registerItemModelPredicates(event: ItemModelPredicateProviderRegistryEvent) {
        event.registry.register(
            Link.LINK_CARD, Link.MOD_ID.id("linked")
        ) { itemInstance: ItemInstance, _: BlockView?, _: Living?, _: Int ->
            if (itemInstance.stationNBT.getBoolean("linked")) 1f else 0f
        }
    }

    @EventListener
    private fun runEvent(event: PostInitEvent) {
        StationAPI.EVENT_BUS.post(LinkIconRegistryEvent())
    }

    @EventListener
    private fun registerIcons(event: LinkIconRegistryEvent) {
        event.registerLinkIcon(Link.MOD_ID.id("unknown"), LinkIconRegistry.UNKNOWN_ICON)
        event.registerLinkIcon(Link.MOD_ID.id("chest"), LinkIcon(176, 66))
        event.registerLinkIcon(Link.MOD_ID.id("double_chest"), LinkIcon(176, 88))
    }

    @EventListener
    private fun registerGuiHandler(event: GuiHandlerRegistryEvent) {
        event.registry.registerValueNoMessage(
            Link.MOD_ID.id("link_terminal"),
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
            Link.MOD_ID.id("alternate_chest"),
            BiTuple.of<BiFunction<PlayerBase, InventoryBase, ScreenBase>, Supplier<InventoryBase>>(
                BiFunction<PlayerBase, InventoryBase, ScreenBase> { player: PlayerBase, entity: InventoryBase ->
                    openAlternateChestGui(
                        player,
                        entity
                    )
                },
                Supplier<InventoryBase> { TileEntityChest() })
        )
        event.registry.registerValueNoMessage(
            Link.MOD_ID.id("alternate_double_chest"),
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

    private fun openAlternateChestGui(player: PlayerBase, entity: InventoryBase): ScreenBase {
        return AlternateChestGui(player, entity)
    }
}
