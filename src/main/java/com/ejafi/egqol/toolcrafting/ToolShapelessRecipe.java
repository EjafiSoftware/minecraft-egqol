package com.ejafi.egqol.toolcrafting;

import com.mojang.serialization.MapCodec;
import java.util.List;
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
    ToolShapelessRecipe(String group, CraftingBookCategory category, ItemStack result, List<Ingredient> ingredients) {
        super(group, category, result, NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new)));
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
        ItemStack[] remainders = input.items().stream()
                .map(ToolShapelessRecipe::computeRemainder)
                .toArray(ItemStack[]::new);
        return NonNullList.of(ItemStack.EMPTY, remainders);
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
        private static final MapCodec<ToolShapelessRecipe> MAP_CODEC =
                RecipeSerializer.SHAPELESS_RECIPE.codec().xmap(ToolShapelessRecipe::copyOf, Function.identity());
        private static final StreamCodec<RegistryFriendlyByteBuf, ToolShapelessRecipe> STREAM_CODEC =
                RecipeSerializer.SHAPELESS_RECIPE.streamCodec().map(ToolShapelessRecipe::copyOf, Function.identity());

        @Override
        public MapCodec<ToolShapelessRecipe> codec() {
            return MAP_CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ToolShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
