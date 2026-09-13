package com.hedge.hedges_bestiary.blocks;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.registry.HBEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HBBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, HedgesBestiary.MODID);

    public static final DeferredHolder<Block, Block> MURK_EGG = registerBlock("murk_egg",
            () -> new EggBlock<>(BlockBehaviour.Properties.ofFullCopy(Blocks.SNIFFER_EGG),
                    HBEntities.MURK, EggBlock.LARGE_EGG));

    public static final DeferredHolder<Block, Block> DAWN_DOVE_EGG = registerBlock("dawn_dove_egg",
            () -> new EggBlock<>(BlockBehaviour.Properties.ofFullCopy(Blocks.SNIFFER_EGG),
                    HBEntities.DAWN_DOVE, EggBlock.LARGE_EGG));

    public static final DeferredHolder<Block, Block> GURK_EGG = registerBlock("gurk_egg",
            () -> new MultiEggBlock<>(BlockBehaviour.Properties.ofFullCopy(Blocks.TURTLE_EGG),
                    HBEntities.GURK));

    public static final DeferredHolder<Block, Block>  ZAPPET_EGG = registerBlock("zappet_egg",
            () -> new MultiEggBlock<>(BlockBehaviour.Properties.ofFullCopy(Blocks.TURTLE_EGG),
                    HBEntities.ZAPPET));

    private static <T extends Block> Supplier<T> create(String key, Supplier<T> block) {
        return BLOCKS.register(key, block);
    }

    private static DeferredHolder<Block, Block> registerBlock(String name, Supplier<Block> block){
        DeferredHolder<Block, Block> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static DeferredHolder<Item, Item> registerBlockItem(String name, DeferredHolder<Block, Block> block){
        return HBItems.ITEMS.register(name, ()-> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void registerBlocks(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
