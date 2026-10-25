package com.ejafi.egqol.toolcrafting;

import java.util.List;
import net.datafaker.Faker;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

final class IngredientStackFaker {
    private static final Faker FAKER = new Faker();
    private static final List<Item> MATERIALS = BuiltInRegistries.ITEM.stream()
            .map(Item::getDefaultInstance)
            .filter(stack -> !stack.isEmpty() && !stack.has(DataComponents.MAX_DAMAGE))
            .map(ItemStack::getItem)
            .toList();
    private static final List<Item> MATERIALS_WITHOUT_REMAINDER = MATERIALS.stream()
            .filter(item -> !item.getDefaultInstance().hasCraftingRemainingItem())
            .toList();
    private static final List<Item> MATERIALS_WITH_REMAINDER = MATERIALS.stream()
            .filter(item -> item.getDefaultInstance().hasCraftingRemainingItem())
            .toList();

    private List<Item> candidates = MATERIALS_WITHOUT_REMAINDER;

    IngredientStackFaker withCraftingRemainder() {
        this.candidates = MATERIALS_WITH_REMAINDER;
        return this;
    }

    ItemStack generate() {
        var stack = new ItemStack(FAKER.options().nextElement(candidates));
        stack.setCount(FAKER.number().numberBetween(1, stack.getMaxStackSize() + 1));
        return stack;
    }
}
