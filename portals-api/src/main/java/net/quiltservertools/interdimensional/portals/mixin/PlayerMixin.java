package net.quiltservertools.interdimensional.portals.mixin;

import net.quiltservertools.interdimensional.portals.InterdimensionalPortals;
import net.quiltservertools.interdimensional.portals.interfaces.EntityInCustomPortal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends Entity implements EntityInCustomPortal {
    boolean inPortal = false;
    int timeInPortal = 0;

    protected PlayerMixin(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    @Override
    public void setInPortal(boolean inPortal) {
        this.inPortal = inPortal;
    }


    @Unique
    @Override
    public int getTimeInPortal() {
        return timeInPortal;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void CPAinCustomPortal(CallbackInfo ci) {
        if (inPortal) {
            if (!InterdimensionalPortals.isInstanceOfCustomPortal(world, this.getBlockPos())) {
                inPortal = false;
                timeInPortal = 0;
                return;
            }
            if (!didTeleport())
                timeInPortal++;
        }
    }
}
