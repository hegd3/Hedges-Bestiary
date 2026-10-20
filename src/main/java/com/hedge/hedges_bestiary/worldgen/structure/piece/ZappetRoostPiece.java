package com.hedge.hedges_bestiary.worldgen.structure.piece;

import com.hedge.hedges_bestiary.entity.living.ZappetEntity;
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


public class ZappetRoostPiece extends TemplateStructurePiece {
    private boolean spawned = false;

    public ZappetRoostPiece(StructureTemplateManager manager, ResourceLocation re, StructurePlaceSettings pPlaceSettings, BlockPos pos) {
        super(HBStructurePieces.ZAPPET_ROOST.get(), 0, manager, re, re.toString(), pPlaceSettings, pos);
    }

    public ZappetRoostPiece(StructureTemplateManager manager, CompoundTag tag) {
        super(HBStructurePieces.ZAPPET_ROOST.get(), tag, manager, (x) -> new StructurePlaceSettings());
    }

    public ZappetRoostPiece(StructurePieceSerializationContext context, CompoundTag tag) {
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
        ZappetEntity entity = HBEntities.ZAPPET.get().create(level.getLevel());
        while (!level.isEmptyBlock(pos) && pos.getY() < level.getMaxBuildHeight()) {
            pos = pos.above();
        }
        Vec3 v = Vec3.atCenterOf(pos);
        entity.setPos(v.x - 1, v.y + 1, v.z);
        while (!level.isUnobstructed(entity)) {
            entity.setPos(entity.position().add(new Vec3(0, 1, 0)));
        }
        if (HBTamableAnimal.SleepType.MATUTINAL.canSleep(level.dayTime() % 24000)) {
            entity.setNapping(true);
        }
        entity.setHasHome(true);
        entity.setHomePos(entity.blockPosition());
        level.addFreshEntity(entity);
        for (int i = 0; i < (int)(entity.getMaxGroupSize() * level.getRandom().nextFloat()); i++) {
            ZappetEntity follower = HBEntities.ZAPPET.get().create(level.getLevel());
            follower.setPos(entity.getX() + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 4, entity.getY(), entity.getZ() + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 4);
            follower.setHasHome(true);
            follower.setHomePos(follower.blockPosition());
            follower.startFollowing(entity);
            level.addFreshEntity(follower);
        }
        this.spawned = true;
    }
}
