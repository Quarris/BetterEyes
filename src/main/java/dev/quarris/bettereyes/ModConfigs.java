package dev.quarris.bettereyes;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfigs {

    public static ForgeConfigSpec.BooleanValue printToActionBar;
    public static ForgeConfigSpec.DoubleValue shatterChance;

    public static ForgeConfigSpec register(ForgeConfigSpec.Builder builder) {
        printToActionBar = builder.comment(
                "true: Prints message to action bar above the hotbar;",
                "false: Prints message to chat."
        ).define("printToActionBar", true);

        shatterChance = builder.comment(
                "The probability between 0 and 1 that the eye will shatter."
        ).defineInRange("shatterChance", 0.2, 0, 1);

        return builder.build();
    }
}
