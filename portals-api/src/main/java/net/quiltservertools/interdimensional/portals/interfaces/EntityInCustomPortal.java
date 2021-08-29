package net.quiltservertools.interdimensional.portals.interfaces;

public interface EntityInCustomPortal {

    default void setInPortal(boolean inPortal) {
    }

    default int getTimeInPortal() {
        return 0;
    }

    boolean didTeleport();

    void setDidTP(boolean didTP);

    void increaseCooldown();
}
