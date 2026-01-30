package org.z2six.betterglint.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.z2six.betterglint.Constants;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class BetterGlintItemOutlineRenderer {
    private static final ResourceLocation POST_CHAIN = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "shaders/post/item_outline.json");
    private static final int BUFFER_SIZE = 786432;

    private static volatile PostChain postChain;
    private static volatile RenderTarget maskTarget;
    private static volatile int lastWidth = -1;
    private static volatile int lastHeight = -1;
    private static volatile boolean maskDirty = false;

    private static final ThreadLocal<Boolean> IN_MASK_PASS = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private BetterGlintItemOutlineRenderer() {
    }

    public static boolean isInMaskPass() {
        return Boolean.TRUE.equals(IN_MASK_PASS.get());
    }

    public static void renderItemToMask(
        ItemRenderer itemRenderer,
        ItemStack itemStack,
        ItemDisplayContext displayContext,
        boolean leftHand,
        com.mojang.blaze3d.vertex.PoseStack poseStack,
        int combinedLight,
        int combinedOverlay,
        BakedModel model
    ) {
        ensureInitialized();
        if (maskTarget == null) {
            return;
        }

        if (!maskDirty) {
            maskTarget.clear(Minecraft.ON_OSX); // clears depth to 1.0
            maskDirty = true;
        }
        maskTarget.bindWrite(true);

        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(BUFFER_SIZE));
        IN_MASK_PASS.set(Boolean.TRUE);
        boolean depthTestEnabled = true;
        boolean blendEnabled = false;
        int depthFunc = GL11.GL_LEQUAL;
        boolean depthMask = true;
        try {
            depthTestEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
            blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
            depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
            ByteBuffer depthMaskBuf = BufferUtils.createByteBuffer(1);
            GL11.glGetBooleanv(GL11.GL_DEPTH_WRITEMASK, depthMaskBuf);
            depthMask = depthMaskBuf.get(0) != 0;
        } catch (Throwable ignored) {
        }

        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS); // always write weapon depth into the mask depth buffer
        RenderSystem.depthMask(true);
        try {
            itemRenderer.render(itemStack, displayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, model);
        } finally {
            RenderSystem.depthMask(depthMask);
            RenderSystem.depthFunc(depthFunc);
            if (!depthTestEnabled) {
                RenderSystem.disableDepthTest();
            }
            if (blendEnabled) {
                RenderSystem.enableBlend();
            }
            IN_MASK_PASS.set(Boolean.FALSE);
        }
        bufferSource.endBatch();

        maskTarget.unbindWrite();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }

    public static void processAndComposite(float partialTicks) {
        processAndComposite(partialTicks, false);
    }

    public static void processAndComposite(float partialTicks, boolean firstPerson) {
        ensureInitialized();
        if (postChain == null || maskTarget == null || !maskDirty) {
            return;
        }

        postChain.setUniform("OutlineR", (float) BetterGlintClientConfig.outlineRed() / 255.0F);
        postChain.setUniform("OutlineG", (float) BetterGlintClientConfig.outlineGreen() / 255.0F);
        postChain.setUniform("OutlineB", (float) BetterGlintClientConfig.outlineBlue() / 255.0F);
        postChain.setUniform("OutlineA", (float) BetterGlintClientConfig.outlineAlpha() / 255.0F);
        postChain.setUniform("BlurScale", firstPerson ? 1.0F : 0.35F);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        postChain.process(partialTicks);

        maskTarget.clear(Minecraft.ON_OSX);
        maskDirty = false;
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }

    private static void ensureInitialized() {
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getMainRenderTarget().width;
        int height = minecraft.getMainRenderTarget().height;

        if (postChain == null) {
            try {
                PostChain chain = new PostChain(minecraft.getTextureManager(), minecraft.getResourceManager(), minecraft.getMainRenderTarget(), POST_CHAIN);
                chain.resize(width, height);
                postChain = chain;
                maskTarget = chain.getTempTarget("mask");
                lastWidth = width;
                lastHeight = height;
            } catch (IOException e) {
                Constants.LOG.warn("Failed to load BetterGlint item outline shader: {}", POST_CHAIN, e);
                postChain = null;
                maskTarget = null;
            }
        }

        if (postChain != null && (width != lastWidth || height != lastHeight)) {
            postChain.resize(width, height);
            lastWidth = width;
            lastHeight = height;
        }
    }
}
