package dev.quarris.bettereyes;

import net.minecraft.util.ResourceLocation;

public class ModRef {

    public static final String ID = "bettereyes";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(ID, name);
    }
}
