package com.hedge.hedges_bestiary.entity.types;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface HUDMount {
    int TOP_OF_BAR = 21;

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
