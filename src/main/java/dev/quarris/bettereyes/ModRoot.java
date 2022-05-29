package dev.quarris.bettereyes;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(ModRef.ID)
public class ModRoot {

    public ModRoot() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigs.register(new ForgeConfigSpec.Builder()));
    }
}
