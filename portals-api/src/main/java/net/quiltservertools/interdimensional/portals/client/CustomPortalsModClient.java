package net.quiltservertools.interdimensional.portals.client;

import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.launcher.SessionEventListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;
import net.quiltservertools.interdimensional.portals.CustomPortalApiRegistry;
import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;
import net.quiltservertools.interdimensional.portals.PerWorldPortals;
import net.quiltservertools.interdimensional.portals.networking.NetworkManager;
import net.quiltservertools.interdimensional.portals.networking.PortalRegistrySync;
import net.quiltservertools.interdimensional.portals.util.PortalLink;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class CustomPortalsModClient implements ClientModInitializer {
    public static final ParticleType<BlockStateParticleEffect> CUSTOMPORTALPARTICLE = Registry.register(Registry.PARTICLE_TYPE, InterdimensionalPortals.MOD_ID + ":customportalparticle", FabricParticleTypes.complex(BlockStateParticleEffect.PARAMETERS_FACTORY));

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(InterdimensionalPortals.portalBlock, RenderLayer.getTranslucent());
        ColorProviderRegistryImpl.BLOCK.register((state, world, pos, tintIndex) -> {
            if (world != null && pos != null) {
                Block block = InterdimensionalPortals.getPortalBase(world, pos);
                PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(block);
                if (link != null) return link.colorID;
            }
            return 1908001;
        }, InterdimensionalPortals.portalBlock);
        ParticleFactoryRegistry.getInstance().register(CUSTOMPORTALPARTICLE, CustomPortalParticle.Factory::new);

        PortalRegistrySync.registerReceivePortalData();

        MinecraftClient.getInstance().getGame().setSessionEventListener(new SessionEventListener() {
            @Override
            public void onStartGameSession(GameSession session) {
            }

            @Override
            public void onLeaveGameSession(GameSession session) {
                PerWorldPortals.removeOldPortalsFromRegistry();
            }
        });
    }
}