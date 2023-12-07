package me.basiqueevangelist.commonbridge;

import me.basiqueevangelist.commonbridge.deconomy.DiamondEconomyProvider;
import me.basiqueevangelist.commonbridge.numismatic.NumismaticEconomyProvider;
import me.basiqueevangelist.commonbridge.opac.OpacProtectionProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class CommonBridge implements ModInitializer {
    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isModLoaded("numismatic-overhaul"))
            NumismaticEconomyProvider.init();

        if (FabricLoader.getInstance().isModLoaded("openpartiesandclaims"))
            OpacProtectionProvider.init();

        if (FabricLoader.getInstance().isModLoaded("diamondeconomy"))
            DiamondEconomyProvider.init();
    }

    public static Identifier id(String path) {
        return new Identifier("common-bridge", path);
    }
}
