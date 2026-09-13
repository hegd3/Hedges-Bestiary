package com.hedge.hedges_bestiary.registry;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.worldgen.structure.DawnDoveNestStructure;
import com.hedge.hedges_bestiary.worldgen.structure.PlomboTerritoryStructure;
import com.hedge.hedges_bestiary.worldgen.structure.ZappetRoostStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class HBStructures {

    public static final DeferredRegister<StructureType<?>> DEF_REG = DeferredRegister.create(Registries.STRUCTURE_TYPE, HedgesBestiary.MODID);


    public static final Supplier<StructureType<DawnDoveNestStructure>> DAWN_DOVE_NEST = DEF_REG.register("dawn_dove_nest", () -> () -> DawnDoveNestStructure.CODEC);

    public static final Supplier<StructureType<ZappetRoostStructure>> ZAPPET_ROOST = DEF_REG.register("zappet_roost", () -> () -> ZappetRoostStructure.CODEC);

    public static final Supplier<StructureType<PlomboTerritoryStructure>> PLOMBO_TERRITORY = DEF_REG.register("plombo_territory", () -> () -> PlomboTerritoryStructure.CODEC);

    public static void register(IEventBus bus) {
        DEF_REG.register(bus);
    }
}

