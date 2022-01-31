package net.quiltservertools.interdimensional.portals.api;

import net.minecraft.server.command.ServerCommandSource;
import net.quiltservertools.interdimensional.portals.CustomPortalApiRegistry;
import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;
import net.quiltservertools.interdimensional.portals.PortalBlock;
import net.quiltservertools.interdimensional.portals.portal.PortalIgnitionSource;
import net.quiltservertools.interdimensional.portals.util.ColorUtil;
import net.quiltservertools.interdimensional.portals.util.PortalLink;
import net.quiltservertools.interdimensional.portals.util.SHOULDTP;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class CustomPortalBuilder {
    PortalLink portalLink;

    private CustomPortalBuilder() {
        portalLink = new PortalLink();
    }

    /**
     * Begin the creation of a new Portal
     *
     * @return an instance of CustomPortalBuilder to begin configuring the portal
     */
    public static CustomPortalBuilder beginPortal() {
        return new CustomPortalBuilder();
    }

    /**
     * Register the portal when completed.
     * This should be called last, only when you are finished configuring the portal
     */
    public void registerPortal() {
        CustomPortalApiRegistry.addPortal(Registry.BLOCK.get(portalLink.block), portalLink);
    }

    /**
     * Specify the Block Identifier to be used as the Frame
     *
     * @param blockID Block identifier of the portal's frame block
     */
    public CustomPortalBuilder frameBlock(Identifier blockID) {
        portalLink.block = blockID;
        return this;
    }

    /**
     * Specify the Block to be used as the Frame
     *
     * @param block The Block to be used as the portal's frame block
     */
    public CustomPortalBuilder frameBlock(Block block) {
        portalLink.block = Registry.BLOCK.getId(block);
        return this;
    }

    /**
     * Specify the destination for the portal
     *
     * @param dimID Identifier of the Dimension the portal will travel to
     */
    public CustomPortalBuilder destDimID(Identifier dimID) {
        portalLink.dimID = dimID;
        return this;
    }

    /**
     * Specify the color to be used to tint the portal block.
     *
     * @param color Single Color int value used for tinting. See {@link net.minecraft.util.DyeColor}
     */
    public CustomPortalBuilder tintColor(int color) {
        portalLink.colorID = color;
        return this;
    }

    /**
     * Specify the color in RGB to be used to tint the portal block.
     */
    public CustomPortalBuilder tintColor(int r, int g, int b) {
        portalLink.colorID = ColorUtil.getColorFromRGB(r, g, b);
        return this;
    }

    public CustomPortalBuilder ignitionSource(PortalIgnitionSource ignitionSource) {
        portalLink.portalIgnitionSource = ignitionSource;
        return this;
    }

    /**
     * This portal will be ignited by water
     */
    public CustomPortalBuilder lightWithWater() {
        portalLink.portalIgnitionSource = PortalIgnitionSource.WATER;
        return this;
    }

    /**
     * This portal will be ignited by an item
     *
     * @param item Item to be used to ignite the portal
     */
    public CustomPortalBuilder lightWithItem(Item item) {
        portalLink.portalIgnitionSource = PortalIgnitionSource.ItemUseSource(item);
        return this;
    }

    /**
     * This portal will be ignited by a fluid
     *
     * @param fluid Fluid to be used to ignite the portal
     */
    public CustomPortalBuilder lightWithFluid(Fluid fluid) {
        portalLink.portalIgnitionSource = PortalIgnitionSource.FluidSource(fluid);
        return this;
    }

    public CustomPortalBuilder customIgnitionSource(Identifier customSourceID) {
        portalLink.portalIgnitionSource = PortalIgnitionSource.CustomSource(customSourceID);
        return this;
    }

    /**
     * Specify the forced size of the portal
     * Portal will only be ignitable for these exact dimensions
     *
     * @param width  Forced width of portal
     * @param height Forced height of portal
     * @return
     */
    public CustomPortalBuilder forcedSize(int width, int height) {
        portalLink.forcedWidth = width;
        portalLink.forcedHeight = height;
        return this;
    }

    public CustomPortalBuilder customPortalBlock(PortalBlock portalBlock) {
        portalLink.setPortalBlock(portalBlock);
        return this;
    }

    /**
     * Specify the dimension this portal will return you to
     *
     * @param returnDimID              Identifer of the dimmension the portal will return you to when leaving destination
     * @param onlyIgnitableInReturnDim Should this portal only be ignitable in returnDimID
     */
    public CustomPortalBuilder returnDim(Identifier returnDimID, boolean onlyIgnitableInReturnDim) {
        portalLink.returnDimID = returnDimID;
        portalLink.onlyIgnitableInReturnDim = onlyIgnitableInReturnDim;
        return this;
    }

    /**
     * Specify that this portal can only be ignited in the Overworld
     * Attempting to light it in other dimensions will fail
     */
    public CustomPortalBuilder onlyLightInOverworld() {
        portalLink.onlyIgnitableInReturnDim = true;
        return this;
    }

    /**
     * Specify that this is a flat portal (end portal style)
     *
     * @return
     */
    @Deprecated
    public CustomPortalBuilder flatPortal() {
        portalLink.portalFrameTester = InterdimensionalPortals.FLATPORTAL_FRAMETESTER;
        return this;
    }

    public CustomPortalBuilder registerBeforeTPEvent(Function<Entity, SHOULDTP> event) {
        portalLink.beforeTPEvent(event);
        return this;
    }

    public CustomPortalBuilder registerPostTPEvent(Consumer<Entity> event) {
        portalLink.setPostTPEvent(event);
        return this;
    }

    public CustomPortalBuilder setPermission(Predicate<ServerCommandSource> permission) {
        portalLink.permission = permission;
        return this;
    }
}
