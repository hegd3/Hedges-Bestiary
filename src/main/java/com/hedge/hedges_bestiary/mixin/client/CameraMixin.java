package com.hedge.hedges_bestiary.mixin.client;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Camera.class)
public abstract class CameraMixin {


    @Shadow
    public abstract void move(double pDistanceOffset, double pVerticalOffset, double pHorizontalOffset);


}
