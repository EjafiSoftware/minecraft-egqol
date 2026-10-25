package com.ejafi.egqol.toolcrafting;

import java.util.List;
import net.datafaker.Faker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

final class ToolShapelessRecipeFaker {
    private static final Faker FAKER = new Faker();

    private final String group = FAKER.lorem().word();
    private final CraftingBookCategory category = FAKER.options().option(CraftingBookCategory.class);
    private final ItemStack result = new IngredientStackFaker().generate();
    private List<ItemStack> ingredientStacks =
            List.of(new IngredientStackFaker().generate(), new ToolStackFaker().generate());

    ToolShapelessRecipeFaker withIngredientsMatching(List<ItemStack> ingredientStacks) {
        this.ingredientStacks = List.copyOf(ingredientStacks);
        return this;
    }

    ToolShapelessRecipe generate() {
        return new ToolShapelessRecipe(
                group,
                category,
                result,
                ingredientStacks.stream().map(Ingredient::of).toList());
    }
}
