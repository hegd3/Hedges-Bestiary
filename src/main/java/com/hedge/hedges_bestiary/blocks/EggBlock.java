package com.hedge.hedges_bestiary.blocks;

import com.hedge.hedges_bestiary.registry.HBBlockEntities;
import com.hedge.hedges_bestiary.registry.HBEntities;
import com.hedge.hedges_bestiary.util.BlockHelpers;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EggBlock<E extends Entity> extends BaseEntityBlock {

    public static final MapCodec<EggBlock<?>> CODEC = simpleCodec(EggBlock::new);

    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;

    public static final VoxelShape LARGE_EGG = BlockHelpers.createRectangular(10, 14);
    protected final DeferredHolder<EntityType<?>, EntityType<E>> toHatch;
    private final VoxelShape shape;

    public EggBlock(Properties pProperties, DeferredHolder<EntityType<?>, EntityType<E>> toHatch, VoxelShape shape) {
        super(pProperties);
        this.toHatch = toHatch;
        this.shape = shape;
        this.registerDefaultState(this.stateDefinition.any().setValue(HATCH, 0));
    }

    public EggBlock(Properties properties) {
        this(properties, null, LARGE_EGG);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;

        return createTickerHelper(type, HBBlockEntities.EGG_BLOCK_ENTITY.get(),
                (pLevel,pos,blockState,entity) -> entity.tick(pLevel, pos, blockState));
    }

    @Override
    public @Nullable <T extends BlockEntity> GameEventListener getListener(ServerLevel pLevel, T pBlockEntity) {
        return super.getListener(pLevel, pBlockEntity);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof EggBlockEntity egg && placer != null) {
            egg.setOwnerUUID(placer.getStringUUID());
        }

        super.setPlacedBy(level, pos, state, placer, stack);
    }

    public int getHatchLevel(BlockState state) {
        return state.getValue(HATCH);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HATCH);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EggBlockEntity<>(pPos, pState, toHatch);
    }

    public DeferredHolder<EntityType<?>, EntityType<E>> getToHatch() {
        return this.toHatch;
    }

    public void crack(BlockState state, Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + level.getRandom().nextFloat() * 0.2F);
        level.setBlock(pos, state.setValue(HATCH, Math.min(this.getHatchLevel(state) + 1, 2)), 2);
        level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));

    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

}
