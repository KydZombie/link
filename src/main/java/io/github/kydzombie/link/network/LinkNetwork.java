package io.github.kydzombie.link.network;

import io.github.kydzombie.link.block.LinkCableBlock;
import io.github.kydzombie.link.block.LinkTerminalBlock;
import io.github.kydzombie.link.block.entity.LinkTerminalBlockEntity;
import net.danygames2014.nyalib.network.Network;
import net.danygames2014.nyalib.network.NetworkType;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;

public class LinkNetwork extends Network {
    public LinkNetwork(World world, NetworkType type) {
        super(world, type);
    }

    @Override
    public void update() {
        super.update();
        System.out.println("Updated");
        ArrayList<LinkTerminalBlockEntity> terminals = new ArrayList<>();
        HashSet<BlockPos> machines = new HashSet<>();
        blocks.forEach((pos, block) -> {
            if (block instanceof LinkTerminalBlock) {
                System.out.println("Found a terminal");
                terminals.add((LinkTerminalBlockEntity) world.getBlockEntity(pos.x, pos.y, pos.z));
                machines.add(new BlockPos(pos.x, pos.y, pos.z));
            } else if (!(block instanceof LinkCableBlock)) {
                System.out.println("Found a machine");
                machines.add(new BlockPos(pos.x, pos.y, pos.z));
            }
        });
        for (LinkTerminalBlockEntity terminal : terminals) {
            System.out.println("Sending update");
            terminal.updateMachineConnections(machines);
        }
    }

    public HashSet<BlockPos> getMachines() {
        HashSet<BlockPos> machines = new HashSet<>();
        blocks.forEach((pos, block) -> {
            if (!(block instanceof LinkCableBlock)) {
                System.out.println("Found a machine");
                machines.add(new BlockPos(pos.x, pos.y, pos.z));
            }
        });
        return machines;
    }

    @Override
    public void addBlock(int x, int y, int z, Block block) {
        System.out.println("Added");
        super.addBlock(x, y, z, block);
    }

    @Override
    public boolean removeBlock(int x, int y, int z) {
        System.out.println("Removed");
        return super.removeBlock(x, y, z);
    }
}
