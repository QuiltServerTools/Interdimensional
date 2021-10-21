package net.quiltservertools.interdimensional.portals.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.quiltservertools.interdimensional.portals.CustomPortalApiRegistry;
import net.quiltservertools.interdimensional.portals.util.ColorUtil;
import net.quiltservertools.interdimensional.portals.util.PortalLink;
import net.minecraft.block.Block;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;

public class CustomPortalParticle extends PortalParticle {
    protected CustomPortalParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
    }

    public static class Factory implements ParticleFactory<BlockStateParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            CustomPortalParticle portalParticle = new CustomPortalParticle(clientWorld, d, e, f, g, h, i);
            portalParticle.setSprite(this.spriteProvider);

            var pos = new BlockPos(d, e, f);
            var intColor = ClientManager.getInstance().getColorAtPosition(pos);

            if (intColor >= 0) {
                System.out.println(portalParticle.colorAlpha);
                float[] rgb = ColorUtil.getColorForBlock(intColor);
                portalParticle.setColor(rgb[0], rgb[1], rgb[2]);
            }
            return portalParticle;
        }
    }
}
