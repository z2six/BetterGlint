package org.z2six.betterglint.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.z2six.betterglint.client.BetterGlintOutlineState;
import org.z2six.betterglint.client.BetterGlintItemOutlineRenderer;
import net.minecraft.client.DeltaTracker;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V", at = @At("TAIL"))
    private void betterglint$processItemOutlinesDuringLevelRender(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (BetterGlintOutlineState.consumeProcessingDuringLevelRenderRequest()) {
            BetterGlintItemOutlineRenderer.processAndComposite(deltaTracker.getGameTimeDeltaTicks(), false);
        }
    }

    @Inject(method = "renderItemInHand(Lnet/minecraft/client/Camera;FLorg/joml/Matrix4f;)V", at = @At("TAIL"))
    private void betterglint$processItemOutlinesAfterHand(Camera camera, float partialTick, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (BetterGlintOutlineState.consumeProcessingAfterHandRenderRequest()) {
            BetterGlintItemOutlineRenderer.processAndComposite(partialTick, true);
        }
    }
}
