package com.hedge.hedges_bestiary.entity.types;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface HUDMount {
    @OnlyIn(Dist.CLIENT)
    void renderHUD(GuiGraphics guiGraphics);

    default float getCameraOffsetX() {
        return 0F;
    }

    default float getCameraOffsetY() {
        return 0.5F;
    }

    default float getCameraOffsetZ() {
        return -3F;
    }


}
