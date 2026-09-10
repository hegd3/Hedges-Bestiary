package com.hedge.hedges_bestiary.menu;

import com.hedge.hedges_bestiary.entity.types.HBTamableAnimal;
import com.hedge.hedges_bestiary.registry.HBMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class HBTamableMenu extends AbstractContainerMenu {
    private final HBTamableAnimal animal;

    public HBTamableMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        this(containerId, (HBTamableAnimal) Minecraft.getInstance().level.getEntity(buf.readInt()));
    }

    public HBTamableMenu(int pContainerId, HBTamableAnimal animal) {
        super(HBMenus.TAMABLE_MENU.get(), pContainerId);
        this.animal = animal;
    }


    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.animal.isAlive() && this.animal.distanceTo(player) < 8.0f;
    }

    public HBTamableAnimal getAnimal() {
        return this.animal;
    }
}
