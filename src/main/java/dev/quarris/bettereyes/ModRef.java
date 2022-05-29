package dev.quarris.bettereyes;

import net.minecraft.resources.ResourceLocation;

public class ModRef {

    public static final String ID = "bettereyes";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(ID, name);
    }
}
