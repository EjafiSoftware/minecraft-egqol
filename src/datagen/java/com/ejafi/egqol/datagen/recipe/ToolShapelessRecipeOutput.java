package com.ejafi.egqol.datagen.recipe;

import com.ejafi.egqol.toolcrafting.ToolShapelessRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jspecify.annotations.Nullable;

final class ToolShapelessRecipeOutput implements RecipeOutput {
    private final RecipeOutput output;

    ToolShapelessRecipeOutput(RecipeOutput output) {
        this.output = output;
    }

    @Override
    public Advancement.Builder advancement() {
        return output.advancement();
    }

    @Override
    public void accept(
            ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
        if (!(recipe instanceof ShapelessRecipe shapeless)) {
            throw new IllegalArgumentException("Only a shapeless recipe can keep its tools: " + id);
        }

        output.accept(id, ToolShapelessRecipe.copyOf(shapeless), advancement, conditions);
    }
}
