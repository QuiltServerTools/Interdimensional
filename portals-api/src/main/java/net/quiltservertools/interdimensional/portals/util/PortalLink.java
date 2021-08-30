package net.quiltservertools.interdimensional.portals.util;

import net.quiltservertools.interdimensional.portals.CustomPortalApiRegistry;
import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;
import net.quiltservertools.interdimensional.portals.PortalBlock;
import net.quiltservertools.interdimensional.portals.portal.PortalIgnitionSource;
import net.quiltservertools.interdimensional.portals.portal.frame.PortalFrameTester;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.function.Function;

public class PortalLink {
    public Identifier block;
    public PortalIgnitionSource portalIgnitionSource = PortalIgnitionSource.FIRE;
    private PortalBlock portalBlock = InterdimensionalPortals.portalBlock;
    public Identifier dimID;
    public Identifier returnDimID = new Identifier("overworld");
    public boolean onlyIgnitableInReturnDim = false;
    public int colorID;
    public int forcedWidth, forcedHeight;
    public Identifier portalFrameTester = InterdimensionalPortals.VANILLA_NETHERPORTAL_FRAMETESTER;

    public PortalLink() {

    }

    private Function<Entity, SHOULDTP> beforeTPEvent;
    private Consumer<Entity> postTPEvent;

    public PortalLink(Identifier blockID, Identifier dimID, int colorID) {
        this.block = blockID;
        this.dimID = dimID;
        this.colorID = colorID;
    }

    public Block getPortalBlock() {
        return portalBlock;
    }

    public void setPortalBlock(PortalBlock block) {
        this.portalBlock = block;
    }

    public boolean doesIgnitionMatch(PortalIgnitionSource attemptedSource) {
        return portalIgnitionSource.sourceType == attemptedSource.sourceType && portalIgnitionSource.ignitionSourceID.equals(attemptedSource.ignitionSourceID);
    }

    public boolean canLightInDim(Identifier dim) {
        if (!onlyIgnitableInReturnDim) return true;
        return dim.equals(returnDimID) || dim.equals(dimID);
    }

    public boolean isCorrectForcedSize(int attemptWidth, int attemptHeight) {
        return ((forcedWidth == 0 || forcedWidth == attemptWidth) && (forcedHeight == 0 || forcedHeight == attemptHeight));
    }

    public void beforeTPEvent(Function<Entity, SHOULDTP> execute) {
        beforeTPEvent = execute;
    }

    public SHOULDTP executeBeforeTPEvent(Entity entity) {
        if (beforeTPEvent != null)
            return beforeTPEvent.apply(entity);
        return SHOULDTP.CONTINUE_TP;
    }

    public void setPostTPEvent(Consumer<Entity> execute) {
        postTPEvent = execute;
    }

    public void executePostTPEvent(Entity entity) {
        if (postTPEvent != null)
            postTPEvent.accept(entity);
    }

    public PortalFrameTester.PortalFrameTesterFactory getFrameTester() {
        return CustomPortalApiRegistry.getPortalFrameTester(portalFrameTester);
    }
}