package org.z2six.betterglint;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import org.z2six.betterglint.config.BetterGlintNeoForgeClientConfig;

@Mod(Constants.MOD_ID)
public class BetterGlint {

    public BetterGlint(IEventBus eventBus, ModContainer modContainer) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        modContainer.registerConfig(ModConfig.Type.CLIENT, BetterGlintNeoForgeClientConfig.SPEC);
        eventBus.addListener(BetterGlintNeoForgeClientConfig::onConfigLoading);
        eventBus.addListener(BetterGlintNeoForgeClientConfig::onConfigReloading);

        // Use NeoForge to bootstrap the Common mod.
        Constants.LOG.info("Hello NeoForge world!");
        CommonClass.init();

    }
}
