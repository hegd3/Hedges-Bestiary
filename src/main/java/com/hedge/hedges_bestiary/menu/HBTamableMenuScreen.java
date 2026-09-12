package com.hedge.hedges_bestiary.menu;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.message.EntityKeyMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventoryFollowsMouse;

@OnlyIn(Dist.CLIENT)
public class HBTamableMenuScreen extends AbstractContainerScreen<HBTamableMenu> {

    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/gui/container/tamable_menu.png");
    private static final Component COMMAND_TEXT = Component.translatable("entity.hedges_bestiary.tamable_menu.display_command");
    private static final Component HOME_POSITION_TEXT = Component.translatable("entity.hedges_bestiary.tamable_menu.home");
    private static final Component AUTO_ATTACKS_TEXT = Component.translatable("entity.hedges_bestiary.tamable_menu.auto_targets");
    private static final Component ACCESS_TEXT = Component.translatable("entity.hedges_bestiary.tamable_menu.access");
    private static final Component INVENTORY_TEXT = Component.translatable("entity.hedges_bestiary.tamable_menu.inventory");

    private static final Component NONE = Component.translatable("entity.hedges_bestiary.tamable_menu.null");


    private final HBTamableAnimal animal;
    private float xMouse;
    private float yMouse;
    private final HBTamableMenuButton[] buttons;
    public HBTamableMenuScreen(HBTamableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.animal = pMenu.getAnimal();
        this.imageWidth = 225;
        this.imageHeight = 158;
        this.titleLabelX+=30;
        if (!this.animal.canAccessInventory()) {
            buttons = new HBTamableMenuButton[]{
                    new CommandButton(this.leftPos + 150, this.topPos + 100, this),
                    new AutoTargetButton(this.leftPos + 280, this.topPos + 100, this),
                    new HomeButton(this.leftPos + 215, this.topPos + 200, this),

            };
        } else {
            buttons = new HBTamableMenuButton[]{
                    new CommandButton(this.leftPos + 150, this.topPos + 100, this),
                    new AutoTargetButton(this.leftPos + 280, this.topPos + 100, this),
                    new HomeButton(this.leftPos + 150, this.topPos + 200, this),
                    new AccessInventoryButton(this.leftPos + 280, this.topPos + 200, this),
            };
        }

    }


    @Override
    protected void renderBg(GuiGraphics poseStack, float f, int i, int j) {
        this.renderBackground(poseStack, (int) f, i, j);
        poseStack.blit(RESOURCE_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        renderEntityInInventoryFollowsMouse(poseStack, this.leftPos + 112, this.topPos + 120, this.leftPos + 112, this.topPos + 120, (int) ((float) (this.leftPos + 118) - this.xMouse), 0, (float) (this.topPos + 66 - 40) - this.xMouse, (float) (this.topPos + 66 - 40) - this.yMouse, animal);
    }

    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        this.xMouse = (float) i;
        this.yMouse = (float) j;
        super.render(guiGraphics, i, j, f);
        this.renderTooltip(guiGraphics, i, j);
        for (HBTamableMenuButton button : this.buttons) {
            guiGraphics.pose().pushPose();
            button.render(guiGraphics, i, j, f);
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            for (HBTamableMenuButton b : this.buttons) {
                if (b.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        guiGraphics.drawCenteredString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFF);


    }

    public HBTamableAnimal getMob() {
        return this.animal;
    }

    private static class CommandButton extends HBTamableMenuButton {

        public CommandButton(int pX, int pY, HBTamableMenuScreen screen) {
            super(pX, pY, screen, COMMAND_TEXT, 0.7f);
        }


        @Override
        public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            pGuiGraphics.drawCenteredString(this.screen.getMinecraft().font,
                    Component.translatable("entity.hedges_bestiary.tamable_menu.command_" +
                    this.screen.getMob().getCommand()),
                    (int)((this.getX() + 23) / 0.7f),
                    (int)((this.getY() + 15) / 0.7f),
                    0xFFFFFF);
            pGuiGraphics.pose().popPose();

        }

        @Override
        public void onPress() {
            HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.screen.getMob().getId(), this.screen.getMob().getOwner().getId(), 0));
        }
    }

    private static class HomeButton extends HBTamableMenuButton {

        public HomeButton(int pX, int pY, HBTamableMenuScreen screen) {
            super(pX, pY, screen, HOME_POSITION_TEXT, 0.7f);
        }


        @Override
        public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            Component display;
            if (this.screen.getMob().hasHome()) {
                BlockPos pos = this.screen.getMob().getHomePos();
                display = Component.empty().append(" " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
            } else {
                display = NONE;
            }
            pGuiGraphics.drawCenteredString(this.screen.getMinecraft().font,
                    display,
                    (int)((this.getX() + 23) / 0.7f),
                    (int)((this.getY() + 15) / 0.7f),
                    0xFFFFFF);
            pGuiGraphics.pose().popPose();

        }

        @Override
        public void onPress() {
            HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.screen.getMob().getId(), this.screen.getMob().getOwner().getId(), 1));
        }
    }

    private static class AutoTargetButton extends HBTamableMenuButton {

        public AutoTargetButton(int pX, int pY, HBTamableMenuScreen screen) {
            super(pX, pY, screen, AUTO_ATTACKS_TEXT, 0.65f);
        }


        @Override
        public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            pGuiGraphics.pose().popPose();
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().scale(0.7f, 0.7f, 0.7f);
            pGuiGraphics.drawCenteredString(this.screen.getMinecraft().font,
                    this.screen.getMob().getAutoTargetType() == 0 ? NONE : Component.translatable("entity.hedges_bestiary.tamable_menu.auto_targets_type_" + this.screen.getMob().getAutoTargetType()),
                    (int)((this.getX() + 23) / 0.7f),
                    (int)((this.getY() + 15) / 0.7f),
                    0xFFFFFF);

            pGuiGraphics.pose().popPose();

        }

        @Override
        public void onPress() {
            HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.screen.getMob().getId(), this.screen.getMob().getOwner().getId(), 2));
        }
    }

    private static class AccessInventoryButton extends HBTamableMenuButton {
        public AccessInventoryButton(int pX, int pY, HBTamableMenuScreen screen) {
            super (pX, pY, screen, ACCESS_TEXT, 0.65f);
        }

        @Override
        public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            pGuiGraphics.pose().popPose();
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().scale(0.7f, 0.7f, 0.7f);
            pGuiGraphics.drawCenteredString(this.screen.getMinecraft().font,
                    INVENTORY_TEXT,
                    (int)((this.getX() + 23) / 0.7f),
                    (int)((this.getY() + 15) / 0.7f),
                    0xFFFFFF);

            pGuiGraphics.pose().popPose();

        }

        @Override
        public void onPress() {
            HedgesBestiary.sendMSGToServer(new EntityKeyMessage(this.screen.getMob().getId(), this.screen.getMob().getOwner().getId(), 3));
        }
    }
}
