package com.hedge.hedges_bestiary.entity.types;

import net.minecraft.client.gui.GuiGraphics;

public interface HUDMount {
    int TOP_OF_BAR = 21;

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
