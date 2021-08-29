package net.quiltservertools.interdimensional.portals.mixin;


import net.quiltservertools.interdimensional.portals.CustomPortalApiRegistry;
import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;
import net.quiltservertools.interdimensional.portals.networking.NetworkManager;
import net.quiltservertools.interdimensional.portals.util.PortalLink;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalMixin extends Block {

    public NetherPortalMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"), cancellable = true)
    public void getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom, CallbackInfoReturnable<BlockState> cir) {
        /*if (NetworkManager.isServerSideOnlyMode()) {
            PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(InterdimensionalPortals.defaultPortalBaseFinder(world, pos));
            if (link != null) {
                cir.setReturnValue(link.getPortalBlock().getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom));
            }
        }*/
    }

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        /*if (NetworkManager.isServerSideOnlyMode()) {
            PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(InterdimensionalPortals.defaultPortalBaseFinder(world, pos));
            if (link != null) {
                link.getPortalBlock().onEntityCollision(state, world, pos, entity);
                ci.cancel();
            }
        }*/
    }
}
