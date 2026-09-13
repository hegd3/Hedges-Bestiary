package com.hedge.hedges_bestiary.registry;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.blocks.EggBlockEntity;
import com.hedge.hedges_bestiary.blocks.HBBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class HBBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> DEF_REG = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HedgesBestiary.MODID);

    public static final DeferredHolder<BlockEntityType<EggBlockEntity<EntityType<?>>>, BlockEntityType<EggBlockEntity<EntityType<?>>>> EGG_BLOCK_ENTITY = DEF_REG.register(
            "egg_block_entity", () -> BlockEntityType.Builder.of(EggBlockEntity::new,
                    HBBlocks.MURK_EGG.get(),
                    HBBlocks.DAWN_DOVE_EGG.get(),
                    HBBlocks.GURK_EGG.get(),
                    HBBlocks.ZAPPET_EGG.get()
            ).build(null));

    public static void register(IEventBus bus) {
        DEF_REG.register(bus);
    }
}
