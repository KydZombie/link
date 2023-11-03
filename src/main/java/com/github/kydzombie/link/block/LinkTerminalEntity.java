package com.github.kydzombie.link.block;

import com.github.kydzombie.link.Link;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.InventoryBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.tileentity.TileEntityBase;
import net.minecraft.util.io.CompoundTag;
import net.minecraft.util.io.ListTag;

import java.util.ArrayList;

public class LinkTerminalEntity extends TileEntityBase implements InventoryBase {
    private ItemInstance[] inventory = new ItemInstance[6];

    public LinkTerminalEntity() {

    }

    public TileEntityBase[] getConnections() {
        var connections = new ArrayList<TileEntityBase>();
        for (ItemInstance itemInstance : inventory) {
            if (itemInstance == null || itemInstance.getType() != Link.LINK_CARD) continue;
            var nbt = itemInstance.getStationNBT();
            if (nbt.getBoolean("linked")) {
                var pos = nbt.getCompoundTag("pos");
                var entity = level.getTileEntity(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
                if (entity == null) {
                    nbt.put("linked", false);
                    continue;
                }
                if (connections.contains(entity)) continue;
                connections.add(entity);
            }
        }
        return connections.toArray(TileEntityBase[]::new);
    }

    @Override
    public int getInventorySize() {
        return inventory.length;
    }

    @Override
    public ItemInstance getInventoryItem(int i) {
        if (i > getInventorySize()) return null;
        return inventory[i];
    }

    @Override
    public ItemInstance takeInventoryItem(int i, int j) {
        var existingItem = getInventoryItem(i);
        if (existingItem != null) {
            inventory[i] = null;
            return existingItem;
        } else {
            return null;
        }
    }

    @Override
    public void setInventoryItem(int i, ItemInstance itemInstance) {
        if (i > inventory.length) return;
        inventory[i] = itemInstance;
    }

    @Override
    public String getContainerName() {
        return "Link Terminal";
    }

    @Override
    public int getMaxItemCount() {
        return 64;
    }

    @Override
    public boolean canPlayerUse(PlayerBase arg) {
        return true;
    }

    @Override
    public void readIdentifyingData(CompoundTag tag) {
        super.readIdentifyingData(tag);

        var listTag = tag.getListTag("inventory");
        inventory = new ItemInstance[6];

        for (int i = 0; i < listTag.size(); ++i) {
            var compoundTag = (CompoundTag) listTag.get(i);
            int slot = compoundTag.getByte("Slot") & 255;
            if (slot < inventory.length) {
                inventory[slot] = new ItemInstance(compoundTag);
            }
        }
    }

    @Override
    public void writeIdentifyingData(CompoundTag tag) {
        super.writeIdentifyingData(tag);
        var listTag = new ListTag();

        for (int i = 0; i < inventory.length; ++i) {
            if (inventory[i] == null) continue;
            var compoundTag = new CompoundTag();
            compoundTag.put("Slot", (byte) i);
            inventory[i].toTag(compoundTag);
            listTag.add(compoundTag);
        }

        tag.put("inventory", listTag);
    }
}
