package net.quiltservertools.interdimensional.portals.mixin.client;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.quiltservertools.interdimensional.portals.client.ClientManager;
import net.quiltservertools.interdimensional.portals.interfaces.ClientPlayerInColoredPortal;
import net.quiltservertools.interdimensional.portals.interfaces.EntityInCustomPortal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerMixin extends LivingEntity implements ClientPlayerInColoredPortal {

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    public float nextNauseaStrength;

    @Shadow public abstract void closeHandledScreen();

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
            this.updateCustomNausea();
        }
    }

    @Unique
    public void updateCustomNausea() {
        if (this.client.currentScreen != null && !this.client.currentScreen.isPauseScreen() && !(this.client.currentScreen instanceof DeathScreen)) {
            if (this.client.currentScreen instanceof HandledScreen) {
                this.closeHandledScreen();
            }

            this.client.setScreen(null);
        }
        if (this.nextNauseaStrength == 0.0F) {
            this.client.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.BLOCK_PORTAL_TRIGGER, this.random.nextFloat() * 0.4F + 0.8F, 0.25F));
        }

        this.nextNauseaStrength += 0.0125F;
        if (this.nextNauseaStrength >= 1.0F) {
            this.nextNauseaStrength = 1.0F;
        }

        this.inNetherPortal = true;
    }
}
