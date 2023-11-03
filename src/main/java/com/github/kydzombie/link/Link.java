package com.github.kydzombie.link;

import com.github.kydzombie.link.block.LinkTerminal;
import com.github.kydzombie.link.block.LinkTerminalEntity;
import com.github.kydzombie.link.item.LinkCard;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBase;
import net.minecraft.item.ItemInstance;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.event.tileentity.TileEntityRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.recipe.CraftingRegistry;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.util.Null;
import org.apache.logging.log4j.Logger;

public class Link {
    @Entrypoint.ModID
    public static final ModID MOD_ID = Null.get();

    @Entrypoint.Logger("Link")
    public static final Logger LOGGER = Null.get();

    public static ItemBase LINK_CARD;

    @EventListener
    private void registerItems(ItemRegistryEvent event) {
        LINK_CARD = new LinkCard(MOD_ID.id("link_card"));
    }

    public static BlockBase LINK_TERMINAL;

    @EventListener
    private void registerBlocks(BlockRegistryEvent event) {
        LINK_TERMINAL = new LinkTerminal(MOD_ID.id("link_terminal"), Material.METAL);
    }

    @EventListener
    private void registerTileEntities(TileEntityRegisterEvent event) {
        event.register(LinkTerminalEntity.class, MOD_ID.id("link_terminal").toString());
    }

    @EventListener
    private void registerRecipes(RecipeRegisterEvent event) {
        if (event.recipeId.equals(RecipeRegisterEvent.Vanilla.CRAFTING_SHAPELESS.type())) {
            CraftingRegistry.addShapelessRecipe(new ItemInstance(LINK_CARD), new ItemInstance(LINK_CARD, 1, -1));
        }
    }
}
