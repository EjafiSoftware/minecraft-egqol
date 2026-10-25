package com.ejafi.egqol.toolcrafting;

import java.util.List;
import net.datafaker.Faker;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Unbreakable;

final class ToolStackFaker {
    private static final Faker FAKER = new Faker();
    private static final List<Item> TOOLS = BuiltInRegistries.ITEM.stream()
            .filter(item -> item.getDefaultInstance().has(DataComponents.MAX_DAMAGE))
            .toList();

    private final Item item = FAKER.options().nextElement(TOOLS);
    private int remainingDurability =
            FAKER.number().numberBetween(2, item.getDefaultInstance().getMaxDamage() + 1);
    private boolean unbreakable;

    ToolStackFaker withRemainingDurability(int remainingDurability) {
        this.remainingDurability = remainingDurability;
        return this;
    }

    ToolStackFaker withUnbreakable() {
        this.unbreakable = true;
        return this;
    }

    ItemStack generate() {
        var tool = new ItemStack(item);
        tool.setDamageValue(tool.getMaxDamage() - remainingDurability);
        if (unbreakable) {
            tool.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
        }

        return tool;
    }
}
