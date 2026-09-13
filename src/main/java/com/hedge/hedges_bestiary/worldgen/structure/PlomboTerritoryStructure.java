package com.hedge.hedges_bestiary.worldgen.structure;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.registry.HBStructures;
import com.hedge.hedges_bestiary.worldgen.structure.piece.PlomboTerritoryPiece;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.Optional;

public class PlomboTerritoryStructure extends Structure {

    private static final ResourceLocation NBT_0 = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "plombo_territory_0");
    private static final ResourceLocation NBT_1 = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "plombo_territory_1");
    private static final ResourceLocation NBT_2 = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "plombo_territory_2");


    public static final MapCodec<PlomboTerritoryStructure> CODEC = simpleCodec(PlomboTerritoryStructure::new);

    public PlomboTerritoryStructure(StructureSettings pSettings) {
        super(pSettings);
    }



    public Optional<GenerationStub> findGenerationPoint(GenerationContext pContext) {
        return onTopOfChunkCenter(pContext, Heightmap.Types.WORLD_SURFACE_WG, (p_229979_) -> {
            generatePieces(p_229979_, pContext);
        });
    }

    private static void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {

        BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), getLowestY(context, 12, 12), context.chunkPos().getMinBlockZ());
        builder.addPiece(new PlomboTerritoryPiece(context.structureTemplateManager(), getNBT(context.random()), new StructurePlaceSettings(), blockpos));
    }

    private static ResourceLocation getNBT(RandomSource random) {
        return switch(random.nextInt(2)) {
            case 0 -> NBT_0;
            case 1 -> NBT_1;
            default -> NBT_2;
        };
    }

    @Override
    public StructureType<?> type() {
        return HBStructures.PLOMBO_TERRITORY.get();
    }
}
