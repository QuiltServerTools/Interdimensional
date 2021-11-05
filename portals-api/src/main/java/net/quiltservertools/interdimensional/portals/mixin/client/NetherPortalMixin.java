package net.quiltservertools.interdimensional.portals.mixin.client;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;
import net.quiltservertools.interdimensional.portals.client.ClientManager;
import net.quiltservertools.interdimensional.portals.client.InterdimensionalPortalsClient;
import net.quiltservertools.interdimensional.portals.interfaces.ClientPlayerInColoredPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Random;


@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalMixin extends Block {

    public NetherPortalMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onEntityCollision", at = @At("HEAD"))
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (world.isClient) {
            if (!ClientManager.getInstance().contains(pos)) {
                ((ClientPlayerInColoredPortal) MinecraftClient.getInstance().player).setLastUsedPortalColor(ClientManager.getInstance().getColorAtPosition(pos));
            }
            InterdimensionalPortals.portalBlock.onEntityCollision(state, world, pos, entity);
        }
    }

    @ModifyArgs(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    public void useCustomParticle(Args args, BlockState state, World world, BlockPos pos, Random random) {
        args.set(0, new BlockStateParticleEffect(InterdimensionalPortalsClient.CUSTOMPORTALPARTICLE, state));
    }
}
