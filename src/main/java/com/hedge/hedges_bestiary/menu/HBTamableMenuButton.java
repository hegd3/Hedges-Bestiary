package com.hedge.hedges_bestiary.menu;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HBTamableMenuButton extends ImageButton {
    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(HBTamableMenuScreen.RESOURCE_LOCATION, HBTamableMenuScreen.RESOURCE_LOCATION);
    protected final HBTamableMenuScreen screen;
    private final float scale;
    private static final HBOnPress CHANGE_COMMAND = new HBOnPress();
    public HBTamableMenuButton(int pX, int pY, HBTamableMenuScreen screen, Component message, float scale) {
        super(pX, pY, 45, 29, BUTTON_SPRITES, CHANGE_COMMAND);
        this.screen = screen;
        this.setMessage(message);
        this.scale = scale;

    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.pose().scale(scale, scale, 1);

        pGuiGraphics.drawCenteredString(this.screen.getMinecraft().font, this.getMessage(),
                (int)((this.getX() + 23) / scale),
                (int)((this.getY() + 5) / scale),
                0xFFFFFF);
    }



    private static class HBOnPress implements ImageButton.OnPress {

        @Override
        public void onPress(Button pButton) {

        }
    }
}
