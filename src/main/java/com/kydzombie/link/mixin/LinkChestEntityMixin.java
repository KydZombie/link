package com.kydzombie.link.mixin;

import com.kydzombie.link.Link;
import com.kydzombie.link.block.CanFindDoubleChest;
import com.kydzombie.link.block.HasLinkInfo;
import com.kydzombie.link.gui.AlternateChestStorage;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.DoubleChest;
import net.minecraft.inventory.InventoryBase;
import net.minecraft.tileentity.TileEntityBase;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.io.CompoundTag;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import org.lwjgl.util.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(TileEntityChest.class)
public abstract class LinkChestEntityMixin extends TileEntityBase implements HasLinkInfo, CanFindDoubleChest {
    @Unique
    private String linkName = "Chest";

    @Override
    public Identifier link$getLinkIconId() {
        var inventory = link$findInventory();
        if (inventory instanceof DoubleChest) {
            return Link.NAMESPACE.id("double_chest");
        } else {
            return Link.NAMESPACE.id("chest");
        }
    }

    @Override
    public InventoryBase link$findInventory() {
        var id = level.getTileId(x, y, z);

        InventoryBase chestEntity = (InventoryBase) level.getTileEntity(x, y, z);

        if (level.getTileId(x - 1, y, z) == id) {
            chestEntity = new DoubleChest("Large chest", (TileEntityChest) level.getTileEntity(x - 1, y, z), chestEntity);
        } else if (level.getTileId(x + 1, y, z) == id) {
            chestEntity = new DoubleChest("Large chest", chestEntity, (TileEntityChest) level.getTileEntity(x + 1, y, z));
        } else if (level.getTileId(x, y, z - 1) == id) {
            chestEntity = new DoubleChest("Large chest", (TileEntityChest) level.getTileEntity(x, y, z - 1), chestEntity);
        } else if (level.getTileId(x, y, z + 1) == id) {
            chestEntity = new DoubleChest("Large chest", chestEntity, (TileEntityChest) level.getTileEntity(x, y, z + 1));
        }

        return chestEntity;
    }

    @Override
    public void link$openLinkMenu(PlayerBase player) {
        var inventory = link$findInventory();
        if (inventory instanceof DoubleChest) {
            ((HasLinkInfo) inventory).link$openLinkMenu(player);
        } else {
            GuiHelper.openGUI(player, Link.NAMESPACE.id("alternate_chest"), inventory, new AlternateChestStorage(player.inventory, inventory));
        }
    }

    @Override
    public String link$getLinkName() {
        return linkName;
    }

    @Override
    public void link$setLinkName(String name) {
        linkName = name;
    }

    @Unique
    private Color color;

    @Override
    public Color link$getColor() {
        if (color == null) {
            var rand = new Random();
            color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        }
        return color;
    }

    @Override
    public void link$setColor(Color color) {
        this.color = color;
    }

    @Inject(method = "readIdentifyingData(Lnet/minecraft/util/io/CompoundTag;)V", at = @At("TAIL"))
    private void injectRead(CompoundTag tag, CallbackInfo ci) {
        var colorTag = tag.getCompoundTag("link:color");
        if (colorTag == null) return;
        this.color = new Color(colorTag.getByte("r"), colorTag.getByte("g"), colorTag.getByte("b"));
    }

    @Inject(method = "writeIdentifyingData(Lnet/minecraft/util/io/CompoundTag;)V", at = @At("TAIL"))
    private void injectWrite(CompoundTag tag, CallbackInfo ci) {
        var colorTag = new CompoundTag();
        var color = link$getColor();
        colorTag.put("r", color.getRedByte());
        colorTag.put("g", color.getGreenByte());
        colorTag.put("b", color.getBlueByte());
        tag.put("link:color", colorTag);
    }
}
