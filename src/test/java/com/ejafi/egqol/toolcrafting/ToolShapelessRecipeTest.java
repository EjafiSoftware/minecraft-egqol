package com.ejafi.egqol.toolcrafting;

import static org.assertj.core.api.Assertions.assertThat;

import com.ejafi.egqol.ItemStackContents;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.junit.jupiter.api.Test;

class ToolShapelessRecipeTest {
    private final ItemStack ingredient = new IngredientStackFaker().generate();

    @Test
    void getRemainingItems_returnsToolWithOneMoreDamage_whenToolIsInGrid() {
        ItemStack tool = new ToolStackFaker().generate();

        List<ItemStack> remainders = collectRemainders(List.of(ingredient, tool));

        ItemStack damagedTool = tool.copy();
        damagedTool.setDamageValue(tool.getDamageValue() + 1);
        assertThat(remainders)
                .map(ItemStackContents::read)
                .containsExactly(ItemStackContents.read(ItemStack.EMPTY), ItemStackContents.read(damagedTool));
    }

    @Test
    void getRemainingItems_breaksTool_whenToolHasOneDurabilityLeft() {
        ItemStack tool = new ToolStackFaker().withRemainingDurability(1).generate();

        List<ItemStack> remainders = collectRemainders(List.of(ingredient, tool));

        assertThat(remainders)
                .map(ItemStackContents::read)
                .containsExactly(ItemStackContents.read(ItemStack.EMPTY), ItemStackContents.read(ItemStack.EMPTY));
    }

    @Test
    void getRemainingItems_returnsToolUndamaged_whenToolIsUnbreakable() {
        ItemStack tool = new ToolStackFaker().withUnbreakable().generate();

        List<ItemStack> remainders = collectRemainders(List.of(ingredient, tool));

        assertThat(remainders)
                .map(ItemStackContents::read)
                .containsExactly(ItemStackContents.read(ItemStack.EMPTY), ItemStackContents.read(tool));
    }

    @Test
    void getRemainingItems_returnsCraftingRemainder_whenIngredientHasOne() {
        ItemStack container = new IngredientStackFaker().withCraftingRemainder().generate();

        List<ItemStack> remainders = collectRemainders(List.of(container));

        assertThat(remainders)
                .map(ItemStackContents::read)
                .containsExactly(ItemStackContents.read(container.getCraftingRemainingItem()));
    }

    private static List<ItemStack> collectRemainders(List<ItemStack> grid) {
        ToolShapelessRecipe recipe =
                new ToolShapelessRecipeFaker().withIngredientsMatching(grid).generate();
        return recipe.getRemainingItems(CraftingInput.of(grid.size(), 1, grid));
    }
}
