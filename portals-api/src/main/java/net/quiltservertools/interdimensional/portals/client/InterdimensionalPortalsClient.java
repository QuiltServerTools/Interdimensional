package net.quiltservertools.interdimensional.portals.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.impl.client.rendering.ColorProviderRegistryImpl;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;
import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;

@Environment(EnvType.CLIENT)
public class InterdimensionalPortalsClient implements ClientModInitializer {
    public static final ParticleType<BlockStateParticleEffect> CUSTOMPORTALPARTICLE = Registry.register(Registry.PARTICLE_TYPE, InterdimensionalPortals.MOD_ID + ":portal_particle", FabricParticleTypes.complex(BlockStateParticleEffect.PARAMETERS_FACTORY));

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(InterdimensionalPortals.portalBlock, RenderLayer.getTranslucent());
        ColorProviderRegistryImpl.BLOCK.register((state, world, pos, tintIndex) -> ClientManager.getInstance().getColorAtPosition(pos), InterdimensionalPortals.portalBlock);
        ClientManager.getInstance().register();
        ParticleFactoryRegistry.getInstance().register(CUSTOMPORTALPARTICLE, CustomPortalParticle.Factory::new);
    }
}