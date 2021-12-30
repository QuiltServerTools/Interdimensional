package net.quiltservertools.interdimensional.portals;

import net.minecraft.block.BlockState;
import net.quiltservertools.interdimensional.portals.portal.frame.PortalFrameTester;
import net.quiltservertools.interdimensional.portals.util.PortalLink;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;


public class CustomPortalApiRegistry {
    protected static ConcurrentHashMap<Block, PortalLink> portals = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Identifier, PortalFrameTester.PortalFrameTesterFactory> PORTAL_FRAME_TESTERS = new ConcurrentHashMap<>();

    public static PortalLink getPortalLinkFromBase(Block baseBlock) {
        if (baseBlock == null) return null;
        if (portals.containsKey(baseBlock)) return portals.get(baseBlock);
        return null;
    }

    public static Collection<PortalLink> getAllPortalLinks() {
        return portals.values();
    }


    public static void registerPortalFrameTester(Identifier frameTesterID, PortalFrameTester.PortalFrameTesterFactory createPortalFrameTester) {
        PORTAL_FRAME_TESTERS.put(frameTesterID, createPortalFrameTester);
    }

    public static PortalFrameTester.PortalFrameTesterFactory getPortalFrameTester(Identifier frameTesterID) {
        return PORTAL_FRAME_TESTERS.getOrDefault(frameTesterID, null);
    }

    public static void addPortal(Block frameBlock, PortalLink link) {
        if (InterdimensionalPortals.dims.size() > 0 && !InterdimensionalPortals.dims.containsKey(link.dimID))
            InterdimensionalPortals.logError("Dimension not found");
        if (InterdimensionalPortals.getDefaultPortalBlock() == null)
            InterdimensionalPortals.logError("Built in PortalBlock is null");

        if (portals.containsKey(frameBlock) || frameBlock.equals(Blocks.OBSIDIAN)) {
            InterdimensionalPortals.logError("A portal(or the nether portal) is already registered with a frame of: " + frameBlock);
        } else {
            portals.put(frameBlock, link);
        }
    }

    public static boolean removePortal(Block frameBlock) {
        return portals.remove(frameBlock) == null;
    }

    public static boolean isRegisteredFrameBlock(BlockState state) {
        return portals.containsKey(state.getBlock());
    }
}
