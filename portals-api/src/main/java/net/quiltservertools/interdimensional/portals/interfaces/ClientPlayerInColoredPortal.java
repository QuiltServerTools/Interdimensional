package net.quiltservertools.interdimensional.portals.interfaces;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ClientPlayerInColoredPortal {

    void setLastUsedPortalColor(int color);

    int getLastUsedPortalColor();
}
