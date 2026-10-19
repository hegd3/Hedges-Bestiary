package com.hedge.hedges_bestiary.menu;


import com.hedge.hedges_bestiary.HedgesBestiary;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HBTamableMenuButton extends ImageButton {
    private static final ResourceLocation BUTTON = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/gui/container/tamable_menu_button.png");
    private static final ResourceLocation HIGLIGHTED = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/gui/container/tamable_menu_button_highlighted.png");

    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(BUTTON, HIGLIGHTED, HIGLIGHTED, BUTTON);
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
        ResourceLocation resourcelocation = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
        pGuiGraphics.blit(resourcelocation, this.getX(), this.getY(), 0, 0, this.width, this.height, 64, 64);
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
