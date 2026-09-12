package com.hedge.hedges_bestiary.events;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


@EventBusSubscriber(modid = HedgesBestiary.MODID)

public class ForgeEvent {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {

        BlockPos blockpos = event.getPos();

        BlockState state = event.getLevel().getBlockState(blockpos);
        if (state.getBlock() instanceof JukeboxBlock) {
            boolean flag = state.getValue(JukeboxBlock.HAS_RECORD);
            if (event.getUseItem() == TriState.TRUE) {
                Vec3 origin = new Vec3(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                AABB zone = new AABB(origin.subtract(15, 0, 15), origin.add(15, 3, 15));

                for (HBTamableAnimal dancer : event.getLevel().getEntitiesOfClass(HBTamableAnimal.class, zone)) {
                    dancer.setRecordPlayingNearby(blockpos, flag);
                }
            }
        }
    }


}
