package com.ejafi.egqol.datagen;

import com.ejafi.egqol.Egqol;
import com.ejafi.egqol.datagen.recipe.EgqolRecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Egqol.MOD_ID)
public final class DataGatherer {
    private DataGatherer() {}

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        event.createProvider(EgqolRecipeProvider::new);
    }
}
