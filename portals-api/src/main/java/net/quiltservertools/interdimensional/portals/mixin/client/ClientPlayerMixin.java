package net.quiltservertools.interdimensional.portals.mixin.client;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.quiltservertools.interdimensional.portals.client.ClientManager;
import net.quiltservertools.interdimensional.portals.interfaces.ClientPlayerInColoredPortal;
import net.quiltservertools.interdimensional.portals.interfaces.EntityInCustomPortal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerMixin extends LivingEntity implements ClientPlayerInColoredPortal {

    @Shadow
    @Final
    protected MinecraftClient client;

    protected ClientPlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    int portalColor;

    @Override
    public void setLastUsedPortalColor(int color) {
        this.portalColor = color;

    }

    @Override
    public int getLastUsedPortalColor() {
        return portalColor;
    }


    @Inject(method = "updateNausea", at = @At(value = "HEAD"))
    public void injectCustomNausea(CallbackInfo ci) {
        if (((EntityInCustomPortal) this).getTimeInPortal() > 0) {
            // If in custom portal, update the color
            setLastUsedPortalColor(ClientManager.getInstance().getColorAtPosition(this.getBlockPos()));
        }
    }
}
