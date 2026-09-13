package com.hedge.hedges_bestiary.worldgen.structure;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.registry.HBStructures;
import com.hedge.hedges_bestiary.worldgen.structure.piece.ZappetRoostPiece;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.Optional;


public class ZappetRoostStructure extends Structure {

    private static final ResourceLocation NBT_0 = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "zappet_roost_0");
    private static final ResourceLocation NBT_1 = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "zappet_roost_1");

    public static final MapCodec<ZappetRoostStructure> CODEC = simpleCodec(ZappetRoostStructure::new);

    public ZappetRoostStructure(StructureSettings pSettings) {
        super(pSettings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext pContext) {
        return onTopOfChunkCenter(pContext, Heightmap.Types.WORLD_SURFACE_WG, (p_229979_) -> {
            generatePieces(p_229979_, pContext);
        });
    }

    private static void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), getLowestY(context, 15, 15), context.chunkPos().getMinBlockZ());
        builder.addPiece(new ZappetRoostPiece(context.structureTemplateManager(), context.random().nextBoolean() ? NBT_1 : NBT_0, new StructurePlaceSettings(), blockpos));
    }

    @Override
    public StructureType<?> type() {
        return HBStructures.ZAPPET_ROOST.get();
    }
}
