package com.hedge.hedges_bestiary.events;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.client.particle.*;
import com.hedge.hedges_bestiary.entity.living.*;
import com.hedge.hedges_bestiary.entity.living.ambientfish.GildGliderEntity;
import com.hedge.hedges_bestiary.entity.living.ambientfish.ChubEntity;
import com.hedge.hedges_bestiary.entity.living.ambientfish.SkibEntity;
import com.hedge.hedges_bestiary.registry.HBEntities;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = HedgesBestiary.MODID)

public class ServerEvent {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(HBEntities.BURODON.get(), BurodonEntity.bakeAttributes().build());
        event.put(HBEntities.SPOTTED_STRIKER.get(), SpottedStrikerEntity.bakeAttributes().build());
        event.put(HBEntities.PLOMBO.get(), PlomboEntity.bakeAttributes().build());
        event.put(HBEntities.GURK.get(), GurkEntity.bakeAttributes().build());
        event.put(HBEntities.MURK.get(), MurkEntity.bakeAttributes().build());
        event.put(HBEntities.TEARACUDA.get(), TearacudaEntity.bakeAttributes().build());
        event.put(HBEntities.ZAPPET.get(), ZappetEntity.bakeAttributes().build());
        event.put(HBEntities.GILD_GLIDER.get(), GildGliderEntity.bakeAttributes().build());
        event.put(HBEntities.CHUB.get(), ChubEntity.bakeAttributes().build());
        event.put(HBEntities.FEROCETUS.get(), FerocetusEntity.bakeAttributes().build());
        event.put(HBEntities.ENDGEL.get(), EndgelEntity.bakeAttributes().build());
        event.put(HBEntities.DAWN_DOVE.get(), DawnDoveEntity.bakeAttributes().build());
        event.put(HBEntities.SKIB.get(), SkibEntity.bakeAttributes().build());

    }

    @SubscribeEvent
    public static void entitySpawn(RegisterSpawnPlacementsEvent event) {
        event.register(HBEntities.BURODON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(HBEntities.GURK.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GurkEntity::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(HBEntities.GILD_GLIDER.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GildGliderEntity::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(HBEntities.CHUB.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ChubEntity::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(HBEntities.TEARACUDA.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TearacudaEntity::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(HBEntities.FEROCETUS.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FerocetusEntity::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(HBEntities.MURK.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MurkEntity::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(HBEntities.SPOTTED_STRIKER.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpottedStrikerEntity::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(HBEntities.ENDGEL.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndgelEntity::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(HBEntities.SKIB.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.OCEAN_FLOOR, SkibEntity::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);

    }



}
