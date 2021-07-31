package com.github.quiltservertools.interdimensional.mixin;

import net.kyrptonaught.customportalapi.CustomPortalsMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CustomPortalsMod.class, remap = false)
public class MixinCustomPortalsMod {

    @SuppressWarnings("CannotFind")

    @Shadow
    @Mutable
    @Final
    public static String MOD_ID;

    @Inject(method = "<clinit>()V", at = @At("HEAD"))
    private static void updateModid(CallbackInfo ci) {
        MOD_ID = "interdimensional";
    }
}
