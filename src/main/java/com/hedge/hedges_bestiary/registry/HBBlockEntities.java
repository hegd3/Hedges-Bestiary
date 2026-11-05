package com.hedge.hedges_bestiary.registry;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.blocks.EggBlockEntity;
import com.hedge.hedges_bestiary.blocks.HBBlocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HBBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, HedgesBestiary.MODID);

    public static final RegistryObject<BlockEntityType<EggBlockEntity<EntityType<?>>>> EGG_BLOCK_ENTITY = DEF_REG.register(
            "egg_block_entity", () -> BlockEntityType.Builder.of(EggBlockEntity::new,
                    HBBlocks.MURK_EGG.get(),
                    HBBlocks.DAWN_DOVE_EGG.get(),
                    HBBlocks.GURK_EGG.get(),
                    HBBlocks.ZAPPET_EGG.get(),
                    HBBlocks.GOPH_EGG.get(),
                    HBBlocks.GAWK_EGG.get(),
                    HBBlocks.BLURPUM_EGGS.get()

            ).build(null));

    public static void register(IEventBus bus) {
        DEF_REG.register(bus);
    }
}
