package com.kydzombie.link.block;

import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface HasLinkConnection {
    default boolean canConnectLinkCable(Level level, int x, int y, int z, Direction side) {
        return true;
    }
}
