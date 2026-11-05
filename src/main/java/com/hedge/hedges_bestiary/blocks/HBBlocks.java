package com.hedge.hedges_bestiary.blocks;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.items.HBItems;
import com.hedge.hedges_bestiary.registry.HBEntities;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class HBBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HedgesBestiary.MODID);

    public static final RegistryObject<Block> MURK_EGG = registerBlock("murk_egg",
            () -> new EggBlock<>(BlockBehaviour.Properties.copy(Blocks.SNIFFER_EGG),
                    HBEntities.MURK, EggBlock.LARGE_EGG));

    public static final RegistryObject<Block> DAWN_DOVE_EGG = registerBlock("dawn_dove_egg",
            () -> new EggBlock<>(BlockBehaviour.Properties.copy(Blocks.SNIFFER_EGG),
                    HBEntities.DAWN_DOVE, EggBlock.LARGE_EGG));

    public static final RegistryObject<Block> GURK_EGG = registerBlock("gurk_egg",
            () -> new MultiEggBlock<>(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG),
                    HBEntities.GURK));

    public static final RegistryObject<Block> ZAPPET_EGG = registerBlock("zappet_egg",
            () -> new MultiEggBlock<>(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG),
                    HBEntities.ZAPPET));

    public static final RegistryObject<Block> GOPH_EGG = registerBlock("goph_egg",
            () -> new MultiEggBlock<>(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG),
                    HBEntities.GOPH));

    public static final RegistryObject<Block> GAWK_EGG = registerBlock("gawk_egg",
            () -> new MultiEggBlock<>(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG),
                    HBEntities.GAWK));

    public static final RegistryObject<Block> BLURPUM_EGGS = registerBlock("blurpum_eggs",
            () -> new RaftEggBlock<>(BlockBehaviour.Properties.copy(Blocks.FROGSPAWN),
                    HBEntities.BLURPUM, 2));

    private static <T extends Block> Supplier<T> create(String key, Supplier<T> block) {
        return BLOCKS.register(key, block);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name,RegistryObject<T> block){
        return HBItems.ITEMS.register(name, ()-> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void registerBlocks(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
