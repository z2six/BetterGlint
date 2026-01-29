package org.z2six.betterglint.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.PostChain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Accessor("entityEffect")
    PostChain betterglint$getEntityEffect();

    @Accessor("outlineEffectRequested")
    void betterglint$setOutlineEffectRequested(boolean value);
}
