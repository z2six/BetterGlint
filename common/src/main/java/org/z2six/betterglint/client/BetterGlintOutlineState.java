package org.z2six.betterglint.client;

import java.util.concurrent.atomic.AtomicBoolean;

public final class BetterGlintOutlineState {
    private static final AtomicBoolean PROCESS_DURING_LEVEL_RENDER_REQUESTED = new AtomicBoolean(false);
    private static final AtomicBoolean PROCESS_AFTER_HAND_RENDER_REQUESTED = new AtomicBoolean(false);

    private BetterGlintOutlineState() {
    }

    public static void requestProcessingDuringLevelRender() {
        PROCESS_DURING_LEVEL_RENDER_REQUESTED.set(true);
    }

    public static void requestProcessingAfterHandRender() {
        PROCESS_AFTER_HAND_RENDER_REQUESTED.set(true);
    }

    public static boolean consumeProcessingDuringLevelRenderRequest() {
        return PROCESS_DURING_LEVEL_RENDER_REQUESTED.getAndSet(false);
    }

    public static boolean consumeProcessingAfterHandRenderRequest() {
        return PROCESS_AFTER_HAND_RENDER_REQUESTED.getAndSet(false);
    }
}
