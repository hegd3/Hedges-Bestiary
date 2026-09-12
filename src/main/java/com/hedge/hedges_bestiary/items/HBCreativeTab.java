package com.hedge.hedges_bestiary.items;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.blocks.HBBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HBCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HedgesBestiary.MODID);


    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("hedges_bestiary_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(HBItems.SEASONED_TREAT.get()))
                    .title(Component.translatable("creativetab.hedges_bestiary_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // blocks
                        output.accept(HBBlocks.GURK_EGG.get());
                        output.accept(HBBlocks.ZAPPET_EGG.get());
                        output.accept(HBBlocks.DAWN_DOVE_EGG.get());
                        output.accept(HBBlocks.MURK_EGG.get());

                        // crafting items
                        output.accept(HBItems.MURK_SPIKE.get());
                        output.accept(HBItems.ENDGELIC_HEART.get());
                        //output.accept(HBItems.TEARACUDA_TOOTH.get());

                        //crafted items

                        output.accept(HBItems.PLAIN_TREAT.get());
                        output.accept(HBItems.SEASONED_TREAT.get());
                        output.accept(HBItems.HEARTY_TREAT.get());
                        output.accept(HBItems.ENDGELIC_JUDGEMENT.get());
                        output.accept(HBItems.MURKLEVOLENCE.get());

                        // foods
                        output.accept(HBItems.RAW_URKMEAT.get());
                        output.accept(HBItems.COOKED_URKMEAT.get());
                        output.accept(HBItems.SKIB.get());

                        // buckets
                        output.accept(HBItems.GILD_GLIDER_BUCKET.get());
                        output.accept(HBItems.SKIB_BUCKET.get());
                        output.accept(HBItems.CHUB_BUCKET.get());
                        // spawn eggs
                        output.accept(HBItems.BURODON_SPAWN_EGG.get());
                        output.accept(HBItems.SPOTTED_STRIKER_SPAWN_EGG.get());
                        output.accept(HBItems.PLOMBO_SPAWN_EGG.get());
                        output.accept(HBItems.GURK_SPAWN_EGG.get());
                        output.accept(HBItems.MURK_SPAWN_EGG.get());
                        output.accept(HBItems.TEARACUDA_SPAWN_EGG.get());
                        output.accept(HBItems.ZAPPET_SPAWN_EGG.get());
                        output.accept(HBItems.GILD_GLIDER_SPAWN_EGG.get());
                        output.accept(HBItems.SMARM_SPAWN_EGG.get());
                        output.accept(HBItems.FEROCETUS_SPAWN_EGG.get());
                        output.accept(HBItems.ENDGEL_SPAWN_EGG.get());
                        output.accept(HBItems.DAWN_DOVE_SPAWN_EGG.get());
                        output.accept(HBItems.SKIB_SPAWN_EGG.get());


                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);

    }
}
