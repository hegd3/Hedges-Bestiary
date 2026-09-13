package com.hedge.hedges_bestiary.blocks;

import com.hedge.hedges_bestiary.util.BlockHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;

public class MultiEggBlock<E extends Entity> extends EggBlock<E> {

    private static final VoxelShape SINGULAR_EGG = BlockHelpers.createRectangular(10, 9);
    private static final VoxelShape MULTIPLE_EGGS = BlockHelpers.createRectangular(15, 9);

    public static final IntegerProperty EGGS = BlockStateProperties.EGGS;

    public MultiEggBlock(Properties pProperties, DeferredHolder<EntityType<?>, EntityType<E>> toHatch) {
        super(pProperties, toHatch, SINGULAR_EGG);
        this.registerDefaultState(this.stateDefinition.any().setValue(EGGS, 1));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(EGGS) > 1 ? MULTIPLE_EGGS : SINGULAR_EGG;
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return useContext.getItemInHand().getItem() == this.asItem() && state.getValue(EGGS) < 4 || super.canBeReplaced(state, useContext);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        return blockstate.getBlock() == this ? blockstate.setValue(EGGS, Integer.valueOf(Math.min(4, blockstate.getValue(EGGS) + 1))) : super.getStateForPlacement(context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EGGS);
    }

    private void removeEgg(Level level, BlockPos pos, BlockState state) {
        int eggs =  state.getValue(EGGS);
        if (eggs <= 1) {
            level.destroyBlock(pos, true);
        } else {
            level.setBlockAndUpdate(pos, state.setValue(EGGS, eggs - 1));
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
            level.levelEvent(2001, pos, Block.getId(state));
        }
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @org.jetbrains.annotations.Nullable BlockEntity pBlockEntity, ItemStack pTool) {
        super.playerDestroy(pLevel, pPlayer, pPos, pState, pBlockEntity, pTool);
        this.removeEgg(pLevel, pPos, pState);
    }
}
