package net.quiltservertools.interdimensional.portals.mixin.client;

import com.google.common.collect.Sets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.quiltservertools.interdimensional.portals.CustomPortalApiRegistry;
import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;
import net.quiltservertools.interdimensional.portals.networking.NetworkManager;
import net.quiltservertools.interdimensional.portals.util.PortalLink;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Environment(EnvType.CLIENT)
@Mixin(World.class)
public abstract class ClientWorldMixin implements WorldAccess {
    private static final Set<BlockPos> checkedPos = Sets.newConcurrentHashSet();

    @Shadow
    public abstract boolean setBlockState(BlockPos pos, BlockState state);

    @Inject(method = "getBlockState", at = @At("RETURN"), cancellable = true)
    public void swapPortalBlocks(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
            /*BlockState state = cir.getReturnValue();
            if (!checkedPos.contains(pos) && state.getBlock() instanceof NetherPortalBlock) {
                checkedPos.add(pos);
                Block baseBlock = InterdimensionalPortals.getPortalBase(this, pos);
                if (baseBlock != null && !(baseBlock instanceof AirBlock)) {
                    PortalLink link = CustomPortalApiRegistry.getPortalLinkFromBase(baseBlock);
                    if (link != null) {
                        BlockState newState = InterdimensionalPortals.blockWithAxis(link.getPortalBlock().getDefaultState(), InterdimensionalPortals.getAxisFrom(state));
                        checkedPos.remove(pos);
                        this.setBlockState(pos, newState);
                        cir.setReturnValue(newState);
                    }
                }
            }*/
    }
}
