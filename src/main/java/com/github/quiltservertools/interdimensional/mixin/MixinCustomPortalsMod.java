package com.github.quiltservertools.interdimensional.mixin;

/* fixme actually no dont fix this wth is this :concern:
@Mixin(value = CustomPortalsMod.class, remap = false)
public class MixinCustomPortalsMod {

    @Shadow
    @Mutable
    @Final
    public static String MOD_ID;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<clinit>()V", at = @At("HEAD"))
    private static void updateModid(CallbackInfo ci) {
        MOD_ID = "interdimensional";
    }
}

 */
