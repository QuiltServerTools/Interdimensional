package net.quiltservertools.interdimensional.portals.portal.linking;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.concurrent.ConcurrentHashMap;

public class PortalLinkingStorage extends PersistentState {

    private final ConcurrentHashMap<Identifier, ConcurrentHashMap<BlockPos, DimensionalBlockPos>> portalLinks = new ConcurrentHashMap<>();

    public PortalLinkingStorage() {
        super();
    }

    public static PersistentState fromNbt(NbtCompound tag) {
        PortalLinkingStorage cman = new PortalLinkingStorage();
        NbtList links = (NbtList) tag.get("portalLinks");

        for (int i = 0; i < links.size(); i++) {
            NbtCompound link = links.getCompound(i);
            DimensionalBlockPos toTag = DimensionalBlockPos.fromTag(link.getCompound("to"));
            cman.addLink(BlockPos.fromLong(link.getLong("fromPos")), new Identifier(link.getString("fromDimID")), toTag.pos, toTag.dimensionType);
        }
        return cman;
    }

    public NbtCompound writeNbt(NbtCompound tag) {
        NbtList links = new NbtList();
        portalLinks.keys().asIterator().forEachRemaining(dimKey -> {
            portalLinks.get(dimKey).forEach((blockPos, dimensionalBlockPos) -> {
                NbtCompound link = new NbtCompound();
                link.putString("fromDimID", dimKey.toString());
                link.putLong("fromPos", blockPos.asLong());
                link.put("to", dimensionalBlockPos.toTag(new NbtCompound()));
                links.add(link);
            });
        });
        tag.put("portalLinks", links);
        return tag;
    }

    public DimensionalBlockPos getDestination(BlockPos portalFramePos, RegistryKey<World> dimID) {
        if (portalLinks.containsKey(dimID.getValue()))
            return portalLinks.get(dimID.getValue()).get(portalFramePos);
        return null;
    }

    public void createLink(BlockPos portalFramePos, RegistryKey<World> dimID, BlockPos destPortalFramePos, RegistryKey<World> destDimID) {
        addLink(portalFramePos, dimID, destPortalFramePos, destDimID);
        addLink(destPortalFramePos, destDimID, portalFramePos, dimID);
    }

    private void addLink(BlockPos portalFramePos, Identifier dimID, BlockPos destPortalFramePos, Identifier destDimID) {
        if (!portalLinks.containsKey(dimID))
            portalLinks.put(dimID, new ConcurrentHashMap<>());
        portalLinks.get(dimID).put(portalFramePos, new DimensionalBlockPos(destDimID, destPortalFramePos));
    }

    private void addLink(BlockPos portalFramePos, RegistryKey<World> dimID, BlockPos destPortalFramePos, RegistryKey<World> destDimID) {
        addLink(portalFramePos, dimID.getValue(), destPortalFramePos, destDimID.getValue());
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}