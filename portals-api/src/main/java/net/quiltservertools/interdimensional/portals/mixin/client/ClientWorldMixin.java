package net.quiltservertools.interdimensional.portals.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.quiltservertools.interdimensional.portals.client.ClientManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(World.class)
public abstract class ClientWorldMixin implements WorldAccess {

    @Inject(method = "disconnect", at = @At("HEAD"))
    public void clearPortalCache(CallbackInfo ci) {
        ClientManager.getInstance().clear();
    }
}
