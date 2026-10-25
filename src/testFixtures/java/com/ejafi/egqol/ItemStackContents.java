package com.ejafi.egqol;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** The value of an {@link ItemStack}, which has no value equality of its own. */
public record ItemStackContents(Item item, int count, DataComponentPatch components) {
    public static ItemStackContents read(ItemStack stack) {
        return new ItemStackContents(stack.getItem(), stack.getCount(), stack.getComponentsPatch());
    }
}
