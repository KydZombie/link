package com.github.kydzombie.link.item;

import com.github.kydzombie.link.block.HasLinkInfo;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.level.Level;
import net.minecraft.util.io.CompoundTag;
import net.modificationstation.stationapi.api.client.gui.CustomTooltipProvider;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.template.item.TemplateItemBase;
import net.modificationstation.stationapi.api.util.Colours;

public class LinkCard extends TemplateItemBase implements CustomTooltipProvider {
    public LinkCard(Identifier identifier) {
        super(identifier);
        setTranslationKey(identifier);
        setMaxStackSize(1);
    }

    @Override
    public boolean useOnTile(ItemInstance itemInstance, PlayerBase player, Level level, int x, int y, int z, int meta) {
        if (player.method_1373()) {
            var entity = player.level.getTileEntity(x, y, z);
            if (entity instanceof HasLinkInfo info) {
                if (!level.isServerSide) {
                    var pos = new CompoundTag();
                    pos.put("x", entity.x);
                    pos.put("y", entity.y);
                    pos.put("z", entity.z);
                    itemInstance.getStationNBT().put("pos", pos);
                    itemInstance.getStationNBT().put("linked", true);
                    itemInstance.getStationNBT().put("entity_name", info.getLinkName());
                }
                return true;
            }
        }
        return super.useOnTile(itemInstance, player, level, x, y, z, meta);
    }

    @Override
    public String[] getTooltip(ItemInstance itemInstance, String originalTooltip) {
        if (itemInstance.getStationNBT().containsKey("pos")) {
            var nbt = itemInstance.getStationNBT();
            var pos = nbt.getCompoundTag("pos");
            if (nbt.getBoolean("linked")) {
                return new String[]{
                        originalTooltip,
                        nbt.getString("entity_name"),
                        "%d, %d, %d".formatted(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"))
                };
            } else {
                return new String[]{
                        originalTooltip,
                        Colours.RED + "Invalid Tile Entity!",
                        Colours.RED + "%d, %d, %d".formatted(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"))
                };
            }
        } else {
            return new String[]{originalTooltip};
        }
    }
}
