package com.hedge.hedges_bestiary.worldgen.structure.piece;

import com.hedge.hedges_bestiary.entity.living.DawnDoveEntity;
import com.hedge.hedges_bestiary.entity.living.PlomboEntity;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.registry.HBStructurePieces;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.Vec3;


public class PlomboTerritoryPiece extends TemplateStructurePiece {
    private boolean spawned = false;
    public PlomboTerritoryPiece(StructureTemplateManager manager, ResourceLocation re, StructurePlaceSettings pPlaceSettings, BlockPos pos) {
        super(HBStructurePieces.PLOMBO_TERRITORY.get(), 0, manager, re, re.toString(), pPlaceSettings, pos);

    }

    public PlomboTerritoryPiece(StructureTemplateManager manager, CompoundTag tag) {
        super(HBStructurePieces.PLOMBO_TERRITORY.get(), tag, manager, (x) -> new StructurePlaceSettings());
    }

    public PlomboTerritoryPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        this(context.structureTemplateManager(), tag);

    }


    @Override
    protected void handleDataMarker(String pName, BlockPos pPos, ServerLevelAccessor pLevel, RandomSource pRandom, BoundingBox pBox) {

    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager pStructureManager, ChunkGenerator pGenerator, RandomSource pRandom, BoundingBox pBox, ChunkPos pChunkPos, BlockPos pPos) {
        super.postProcess(level, pStructureManager, pGenerator, pRandom, pBox, pChunkPos, pPos);
        if (!this.spawned) this.spawnMob(level, pPos);
    }

    private void spawnMob(ServerLevelAccessor level, BlockPos pos) {
        PlomboEntity entity = HBEntities.PLOMBO.get().create(level.getLevel());
        while (!level.isEmptyBlock(pos) && pos.getY() < level.getMaxBuildHeight()) {
            pos = pos.above();
        }
        Vec3 v = Vec3.atCenterOf(pos);
        entity.setPos(v.x, v.y, v.z);
        while (!level.isUnobstructed(entity)) {
            entity.setPos(entity.position().add(new Vec3(0, 1, 0)));
        }
        if (HBTamableAnimal.SleepType.CATHERMAL.canSleep(level.dayTime() % 24000)) {
            entity.setNapping(true);
        }
        entity.setHasHome(true);
        entity.setHomePos(entity.blockPosition());
        level.addFreshEntity(entity);
        this.spawned = true;
    }
}
