package net.quiltservertools.interdimensional.portals;

import net.minecraft.block.BlockState;
import net.quiltservertools.interdimensional.portals.portal.PortalIgnitionSource;
import net.quiltservertools.interdimensional.portals.portal.frame.PortalFrameTester;
import net.quiltservertools.interdimensional.portals.util.ColorUtil;
import net.quiltservertools.interdimensional.portals.util.PortalLink;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;


public class CustomPortalApiRegistry {
    protected static ConcurrentHashMap<Block, PortalLink> portals = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Identifier, PortalFrameTester.PortalFrameTesterFactory> PortalFrameTesters = new ConcurrentHashMap<>();

    public static PortalLink getPortalLinkFromBase(Block baseBlock) {
        if (baseBlock == null) return null;
        if (portals.containsKey(baseBlock)) return portals.get(baseBlock);
        return null;
    }

    public static Collection<PortalLink> getAllPortalLinks() {
        return portals.values();
    }


    public static void registerPortalFrameTester(Identifier frameTesterID, PortalFrameTester.PortalFrameTesterFactory createPortalFrameTester) {
        PortalFrameTesters.put(frameTesterID, createPortalFrameTester);
    }

    public static PortalFrameTester.PortalFrameTesterFactory getPortalFrameTester(Identifier frameTesterID) {
        return PortalFrameTesters.getOrDefault(frameTesterID, null);
    }

    public static void addPortal(Block frameBlock, PortalLink link) {
        if (frameBlock == null) InterdimensionalPortals.logError("Frameblock is null");
        if (link.getPortalBlock() == null) InterdimensionalPortals.logError("Portal block is null");
        if (link.portalIgnitionSource == null) InterdimensionalPortals.logError("Portal ignition source is null");
        if (link.dimID == null) InterdimensionalPortals.logError("Dimension is null");
        if (InterdimensionalPortals.dims.size() > 0 && !InterdimensionalPortals.dims.containsKey(link.dimID))
            InterdimensionalPortals.logError("Dimension not found");
        if (InterdimensionalPortals.getDefaultPortalBlock() == null)
            InterdimensionalPortals.logError("Built in CustomPortalBlock is null");

        if (portals.containsKey(frameBlock) || frameBlock.equals(Blocks.OBSIDIAN)) {
            InterdimensionalPortals.logError("A portal(or the nether portal) is already registered with a frame of: " + frameBlock);
        } else {
            portals.put(frameBlock, link);
        }
    }
    public static boolean isRegisteredFrameBlock(BlockState state) {
        return portals.containsKey(state.getBlock());
    }
}
