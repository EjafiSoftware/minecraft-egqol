package com.ejafi.egqol.toolcrafting;

import com.ejafi.egqol.Egqol;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ToolCraftingRegisters {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Egqol.MOD_ID);

    static final DeferredHolder<RecipeSerializer<?>, ToolShapelessRecipe.Serializer> TOOL_SHAPELESS =
            RECIPE_SERIALIZERS.register("tool_shapeless", ToolShapelessRecipe.Serializer::new);

    private ToolCraftingRegisters() {}

    public static void register(IEventBus modEventBus) {
        RECIPE_SERIALIZERS.register(modEventBus);
    }
}
