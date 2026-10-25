package com.ejafi.egqol.datagen.recipe;

import com.ejafi.egqol.Egqol;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public final class EgqolRecipeProvider extends RecipeProvider {
    private static final int STRING_PER_WOOL = 4;
    private static final ResourceLocation STRING_FROM_WOOL =
            ResourceLocation.fromNamespaceAndPath(Egqol.MOD_ID, "crafting/string_from_wool");

    public EgqolRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.STRING, STRING_PER_WOOL)
                .requires(ItemTags.WOOL)
                .requires(Tags.Items.TOOLS_SHEAR)
                .unlockedBy("has_shears", has(Tags.Items.TOOLS_SHEAR))
                .save(new ToolShapelessRecipeOutput(output), STRING_FROM_WOOL);
    }
}
