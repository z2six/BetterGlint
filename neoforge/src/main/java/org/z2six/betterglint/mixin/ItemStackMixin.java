package org.z2six.betterglint.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.z2six.betterglint.client.BetterGlintClientConfig;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "hasFoil", at = @At("HEAD"), cancellable = true)
    private void betterglint$disableVanillaGlint(CallbackInfoReturnable<Boolean> cir) {
        if (BetterGlintClientConfig.disableVanillaGlint()) {
            cir.setReturnValue(false);
        }
    }
}

