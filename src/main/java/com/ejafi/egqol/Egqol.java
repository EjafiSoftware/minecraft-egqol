package com.ejafi.egqol;

import com.ejafi.egqol.toolcrafting.ToolCraftingRegisters;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Egqol.MOD_ID)
public final class Egqol {
    public static final String MOD_ID = "egqol";

    public Egqol(IEventBus modEventBus) {
        ToolCraftingRegisters.register(modEventBus);
    }
}
