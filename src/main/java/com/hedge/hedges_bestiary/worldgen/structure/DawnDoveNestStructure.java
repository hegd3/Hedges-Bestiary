package com.hedge.hedges_bestiary.worldgen.structure;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.registry.HBStructures;
import com.hedge.hedges_bestiary.worldgen.structure.piece.DawnDoveNestPiece;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.Optional;

public class DawnDoveNestStructure extends Structure {

    private static final ResourceLocation NBT_0 = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "dawn_dove_nest_0");
    private static final ResourceLocation NBT_1 = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "dawn_dove_nest_1");



    public static final MapCodec<DawnDoveNestStructure> CODEC = simpleCodec(DawnDoveNestStructure::new);

    public DawnDoveNestStructure(StructureSettings pSettings) {
        super(pSettings);
    }



    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext pContext) {
        return onTopOfChunkCenter(pContext, Heightmap.Types.WORLD_SURFACE_WG, (p_229979_) -> {
            generatePieces(p_229979_, pContext);
        });
    }

    private static void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), getLowestY(context, 11, 11), context.chunkPos().getMinBlockZ());
        builder.addPiece(new DawnDoveNestPiece(context.structureTemplateManager(), context.random().nextBoolean() ? NBT_1 : NBT_0, new StructurePlaceSettings(), blockpos));
    }

    @Override
    public StructureType<?> type() {
        return HBStructures.DAWN_DOVE_NEST.get();
    }
}
