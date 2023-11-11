package com.github.kydzombie.link;

import com.github.kydzombie.link.gui.AlternateChestGui;
import com.github.kydzombie.link.block.LinkTerminalEntity;
import com.github.kydzombie.link.gui.LinkTerminalGui;
import com.github.kydzombie.link.registry.LinkIcon;
import com.github.kydzombie.link.registry.LinkIconRegistry;
import com.github.kydzombie.link.registry.LinkIconRegistryEvent;
import com.github.kydzombie.link.util.Vector2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.DoubleChest;
import net.minecraft.inventory.InventoryBase;
import net.minecraft.tileentity.TileEntityChest;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.client.event.render.model.ItemModelPredicateProviderRegistryEvent;
import net.modificationstation.stationapi.api.event.mod.PostInitEvent;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import org.lwjgl.util.Color;
import uk.co.benjiweber.expressions.tuple.BiTuple;

import java.util.function.Supplier;

import static com.github.kydzombie.link.Link.MOD_ID;

@Environment(EnvType.CLIENT)
public class LinkClient {
    public static Color currentlySelectedColor = (Color) Color.WHITE;
    @EventListener
    private void registerItemModelPredicates(ItemModelPredicateProviderRegistryEvent event) {
        event.registry.register(Link.LINK_CARD, MOD_ID.id("linked"),
                (itemInstance, world, entity, seed) -> itemInstance.getStationNBT().getBoolean("linked") ? 1 : 0);
    }

    @EventListener
    private void runEvent(PostInitEvent event) {
        StationAPI.EVENT_BUS.post(new LinkIconRegistryEvent());
    }

    @EventListener
    private void registerIcons(LinkIconRegistryEvent event) {
        event.registerLinkIcon(MOD_ID.id("unknown"), LinkIconRegistry.UNKNOWN_ICON);
        event.registerLinkIcon(MOD_ID.id("chest"), new LinkIcon(176, 66));
        event.registerLinkIcon(MOD_ID.id("double_chest"), new LinkIcon(176, 88));
    }

    @EventListener
    private void registerGuiHandler(GuiHandlerRegistryEvent event) {
        event.registry.registerValueNoMessage(MOD_ID.id("link_terminal"), BiTuple.of(this::openLinkTerminal, LinkTerminalEntity::new));
        event.registry.registerValueNoMessage(MOD_ID.id("alternate_chest"), BiTuple.of(this::openAlternateChestGui, TileEntityChest::new));
        event.registry.registerValueNoMessage(MOD_ID.id("alternate_double_chest"), BiTuple.of(this::openAlternateChestGui, () -> new DoubleChest(null, null, null)));
    }

    private ScreenBase openLinkTerminal(PlayerBase player, InventoryBase entity) {
        return new LinkTerminalGui(player, (LinkTerminalEntity) entity);
    }

    private ScreenBase openAlternateChestGui(PlayerBase player, InventoryBase entity) {
        return new AlternateChestGui(player, entity);
    }
}
