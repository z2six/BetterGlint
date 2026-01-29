package org.z2six.betterglint.mixin;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.z2six.betterglint.client.BetterGlintClientConfig;
import org.z2six.betterglint.client.BetterGlintOutlineState;
import org.z2six.betterglint.client.BetterGlintItemOutlineRenderer;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(
        method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
        at = @At("HEAD")
    )
    private void betterglint$captureMaskAndScheduleOutline(
        ItemStack itemStack,
        ItemDisplayContext displayContext,
        boolean leftHand,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int combinedLight,
        int combinedOverlay,
        BakedModel model,
        CallbackInfo ci
    ) {
        if (BetterGlintItemOutlineRenderer.isInMaskPass()) {
            return;
        }

        if (!BetterGlintClientConfig.enableOutline()) {
            return;
        }

        if (displayContext == ItemDisplayContext.GUI) {
            return;
        }

        if (itemStack.isEmpty() || !itemStack.getItem().isFoil(itemStack)) {
            return;
        }

        BetterGlintItemOutlineRenderer.renderItemToMask(
            (ItemRenderer) (Object) this,
            itemStack,
            displayContext,
            leftHand,
            poseStack,
            combinedLight,
            combinedOverlay,
            model
        );

        if (displayContext.firstPerson()) {
            BetterGlintOutlineState.requestProcessingAfterHandRender();
        } else {
            BetterGlintOutlineState.requestProcessingDuringLevelRender();
        }
    }
}
