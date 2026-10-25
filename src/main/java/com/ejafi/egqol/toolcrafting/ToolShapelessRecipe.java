package com.ejafi.egqol.toolcrafting;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public final class ToolShapelessRecipe extends ShapelessRecipe {
    ToolShapelessRecipe(
            String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
    }

    public static ToolShapelessRecipe copyOf(ShapelessRecipe recipe) {
        return new ToolShapelessRecipe(
                recipe.getGroup(),
                recipe.category(),
                recipe.getResultItem(RegistryAccess.EMPTY),
                recipe.getIngredients());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ToolCraftingRegisters.TOOL_SHAPELESS.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remainders = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int slot = 0; slot < input.size(); slot++) {
            remainders.set(slot, computeRemainder(input.getItem(slot)));
        }

        return remainders;
    }

    private static ItemStack computeRemainder(ItemStack ingredient) {
        if (!ingredient.has(DataComponents.MAX_DAMAGE)) {
            return ingredient.getCraftingRemainingItem();
        }

        ItemStack tool = ingredient.copyWithCount(1);
        if (!tool.isDamageableItem()) {
            return tool;
        }

        int damage = tool.getDamageValue() + 1;
        if (damage >= tool.getMaxDamage()) {
            return ItemStack.EMPTY;
        }

        tool.setDamageValue(damage);
        return tool;
    }

    static final class Serializer implements RecipeSerializer<ToolShapelessRecipe> {
        private final MapCodec<ToolShapelessRecipe> codec =
                RecipeSerializer.SHAPELESS_RECIPE.codec().xmap(ToolShapelessRecipe::copyOf, Function.identity());
        private final StreamCodec<RegistryFriendlyByteBuf, ToolShapelessRecipe> streamCodec =
                RecipeSerializer.SHAPELESS_RECIPE.streamCodec().map(ToolShapelessRecipe::copyOf, Function.identity());

        @Override
        public MapCodec<ToolShapelessRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ToolShapelessRecipe> streamCodec() {
            return streamCodec;
        }
    }
}
