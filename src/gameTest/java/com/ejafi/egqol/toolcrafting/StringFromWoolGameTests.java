package com.ejafi.egqol.toolcrafting;

import com.ejafi.egqol.Egqol;
import com.ejafi.egqol.GameTestTemplates;
import com.ejafi.egqol.ItemStackContents;
import java.util.List;
import net.datafaker.Faker;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Egqol.MOD_ID)
@PrefixGameTestTemplate(false)
public final class StringFromWoolGameTests {
    private static final Faker FAKER = new Faker();

    private StringFromWoolGameTests() {}

    @GameTest(template = GameTestTemplates.EMPTY_7X7X7)
    public static void assemble_returns_four_string_when_grid_holds_wool_and_shears(GameTestHelper helper) {
        CraftingInput grid = createGrid(createShears());

        ItemStack result =
                findRecipe(helper, grid).assemble(grid, helper.getLevel().registryAccess());

        helper.assertValueEqual(
                ItemStackContents.read(result), ItemStackContents.read(new ItemStack(Items.STRING, 4)), "result");
        helper.succeed();
    }

    @GameTest(template = GameTestTemplates.EMPTY_7X7X7)
    public static void get_remaining_items_returns_damaged_shears_when_grid_holds_wool_and_shears(
            GameTestHelper helper) {
        ItemStack shears = createShears();
        CraftingInput grid = createGrid(shears);

        List<ItemStack> remainders = findRecipe(helper, grid).getRemainingItems(grid);

        ItemStack damagedShears = shears.copy();
        damagedShears.setDamageValue(shears.getDamageValue() + 1);
        helper.assertValueEqual(
                remainders.stream().map(ItemStackContents::read).toList(),
                List.of(ItemStackContents.read(ItemStack.EMPTY), ItemStackContents.read(damagedShears)),
                "remainders");
        helper.succeed();
    }

    private static ItemStack createShears() {
        var shears = new ItemStack(pickTagMember(Tags.Items.TOOLS_SHEAR));
        shears.setDamageValue(FAKER.number().numberBetween(0, shears.getMaxDamage() - 1));
        return shears;
    }

    private static CraftingInput createGrid(ItemStack shears) {
        return CraftingInput.of(2, 1, List.of(new ItemStack(pickTagMember(ItemTags.WOOL)), shears));
    }

    private static Item pickTagMember(TagKey<Item> tag) {
        List<Item> members = BuiltInRegistries.ITEM.getTag(tag).stream()
                .flatMap(HolderSet::stream)
                .map(Holder::value)
                .toList();
        return FAKER.options().nextElement(members);
    }

    private static ToolShapelessRecipe findRecipe(GameTestHelper helper, CraftingInput grid) {
        return helper.getLevel()
                .getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel())
                .map(RecipeHolder::value)
                .filter(ToolShapelessRecipe.class::isInstance)
                .map(ToolShapelessRecipe.class::cast)
                .orElseThrow(() -> new GameTestAssertException("No tool shapeless recipe matches the grid"));
    }
}
