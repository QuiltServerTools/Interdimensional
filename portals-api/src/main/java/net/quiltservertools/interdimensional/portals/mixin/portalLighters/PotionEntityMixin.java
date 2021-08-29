package net.quiltservertools.interdimensional.portals.mixin.portalLighters;

import net.quiltservertools.interdimensional.portals.portal.PortalIgnitionSource;
import net.quiltservertools.interdimensional.portals.portal.PortalPlacer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin extends ThrownItemEntity {

    public PotionEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "extinguishFire", at = @At("HEAD"), cancellable = true)
    public void attemptPortalLight(BlockPos pos, CallbackInfo ci) {
        PortalPlacer.attemptPortalLight(this.world, pos, pos.down(), PortalIgnitionSource.WATER);
    }
}
