package me.basiqueevangelist.commonbridge;

import me.basiqueevangelist.commonbridge.deconomy.DiamondEconomyProvider;
import me.basiqueevangelist.commonbridge.lightmans.LightmansEconomyProvider;
import me.basiqueevangelist.commonbridge.numismatic.NumismaticEconomyProvider;
import me.basiqueevangelist.commonbridge.opac.OpacProtectionProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonBridge implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("CommonBridge");

    @Override
    public void onInitialize() {
        if (shouldEnable("numismatic-overhaul"))
            NumismaticEconomyProvider.init();

        if (shouldEnable("openpartiesandclaims"))
            OpacProtectionProvider.init();

        if (shouldEnable("diamondeconomy"))
            DiamondEconomyProvider.init();

        if (shouldEnable("lightmanscurrency"))
            LightmansEconomyProvider.init();

        LOGGER.info("Common Bridge initialized.");
    }

    public static Identifier id(String path) {
        return new Identifier("common-bridge", path);
    }

    public static boolean shouldEnable(String modId) {
        var mod = FabricLoader.getInstance().getModContainer(modId).orElse(null);

        if (mod == null) {
            LOGGER.info("Skipping provider for {}, as it is not installed", modId);
            return false;
        }

        if (mod.getMetadata().containsCustomValue("common-bridge:opt_out")) {
            LOGGER.info("Skipping provider for {}, as it has manually opted out", modId);
            return false;
        }

        String propKey = "common-bridge." + modId;
        if (System.getProperty(propKey) != null && !Boolean.getBoolean(propKey)) {
            LOGGER.info("Skipping provider for {}, as the user has manually opted out", modId);
            return false;
        }

        LOGGER.info("Enabling provider for {}", modId);
        return true;
    }
}
