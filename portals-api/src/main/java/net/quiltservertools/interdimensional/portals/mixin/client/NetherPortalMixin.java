package net.quiltservertools.interdimensional.portals.mixin.client;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;
import net.quiltservertools.interdimensional.portals.client.ClientManager;
import net.quiltservertools.interdimensional.portals.interfaces.ClientPlayerInColoredPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalMixin extends Block {

    public NetherPortalMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (world.isClient) {
            if (!ClientManager.getInstance().contains(pos)) {
                ((ClientPlayerInColoredPortal) MinecraftClient.getInstance().player).setLastUsedPortalColor(-1);
            }
            InterdimensionalPortals.portalBlock.onEntityCollision(state, world, pos, entity);
        }
    }
}
