package com.hedge.hedges_bestiary.entity.ai.goal;

import com.hedge.hedges_bestiary.config.HBConfig;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.crafting.Ingredient;

public class HBTemptGoal extends TemptGoal {
    private final HBTamableAnimal animal;
    public HBTemptGoal(HBTamableAnimal animal, double pSpeedModifier, Ingredient pItems, boolean pCanScare) {
        super(animal, pSpeedModifier, pItems, pCanScare);
        this.animal = animal;

    }

    @Override
    public boolean canUse() {
        if (super.canUse()) {
            if (!HBConfig.BREEDING_REQUIRES_TAME) {
                return true;
            }
            if (this.animal.isTamable()) {
                return this.animal.isTame() && this.animal.getOwner() == player;
            }
        }
        return false;
    }


}
