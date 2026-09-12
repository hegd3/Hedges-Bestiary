package com.hedge.hedges_bestiary.items;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.registry.HBEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HBItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, HedgesBestiary.MODID);


    public static final DeferredHolder<Item, Item> MURK_SPIKE = ITEMS.register("murk_spike",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> TEARACUDA_TOOTH = ITEMS.register("tearacuda_tooth",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> ENDGELIC_HEART = ITEMS.register("endgelic_heart",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));


    // CRAFTED ITEMS

    public static final DeferredHolder<Item, Item> PLAIN_TREAT = ITEMS.register("plain_treat",
            () -> new TreatItem(new Item.Properties().rarity(Rarity.COMMON), 0));

    public static final DeferredHolder<Item, Item> SEASONED_TREAT = ITEMS.register("seasoned_treat",
            () -> new TreatItem(new Item.Properties().rarity(Rarity.UNCOMMON).craftRemainder(Items.GLASS_BOTTLE),1));

    public static final DeferredHolder<Item, Item> HEARTY_TREAT = ITEMS.register("hearty_treat",
            () -> new TreatItem(new Item.Properties().rarity(Rarity.RARE),2));

    public static final DeferredHolder<Item, Item> ENDGELIC_JUDGEMENT = ITEMS.register("endgelic_judgement",
            () -> new EndgelicJudgementItem(new Item.Properties().durability(500).rarity(Rarity.EPIC)));

    public static final DeferredHolder<Item, Item> MURKLEVOLENCE = ITEMS.register("murklevolence",
            () -> new MurklevolenceItem(new Item.Properties().durability(800).rarity(Rarity.RARE)));

    // FOODS

    public static final DeferredHolder<Item, Item> RAW_URKMEAT = ITEMS.register("raw_urkmeat",
            () -> new Item(new Item.Properties().food(HBFoodTypes.RAW_URKMEAT)));

    public static final DeferredHolder<Item, Item> COOKED_URKMEAT = ITEMS.register("cooked_urkmeat",
            () -> new Item(new Item.Properties().food(HBFoodTypes.COOKED_URKMEAT)));

    public static final DeferredHolder<Item, Item> SKIB = ITEMS.register("skib",
            () -> new Item(new Item.Properties().food(HBFoodTypes.SKIB)));

    // BUCKETS

    public static final DeferredHolder<Item, Item> CHUB_BUCKET = createBucket("chub", HBEntities.CHUB);

    public static final DeferredHolder<Item, Item> GILD_GLIDER_BUCKET = createBucket("gild_glider", HBEntities.GILD_GLIDER);

    public static final DeferredHolder<Item, Item> SKIB_BUCKET = createBucket("skib", HBEntities.SKIB);


    // SPAWN EGGS
    public static final DeferredHolder<Item, Item> BURODON_SPAWN_EGG = createEgg("burodon", HBEntities.BURODON, 0xAEC8D4, 0x7E83CC);

    public static final DeferredHolder<Item, Item> SPOTTED_STRIKER_SPAWN_EGG = createEgg("spotted_striker", HBEntities.SPOTTED_STRIKER, 0x50555E, 0x8A896B);

    public static final DeferredHolder<Item, Item> PLOMBO_SPAWN_EGG = createEgg("plombo", HBEntities.PLOMBO, 0x2D2F3B, 0x827363);

    public static final DeferredHolder<Item, Item> GURK_SPAWN_EGG = createEgg("gurk", HBEntities.GURK, 0x2E7037, 0x63A64E);

    public static final DeferredHolder<Item, Item> MURK_SPAWN_EGG = createEgg("murk", HBEntities.MURK, 0x473B8A, 0xC4BE84);

    public static final DeferredHolder<Item, Item> TEARACUDA_SPAWN_EGG = createEgg("tearacuda", HBEntities.TEARACUDA, 0x6898D4, 0xD4BB68);

    public static final DeferredHolder<Item, Item> ZAPPET_SPAWN_EGG = createEgg("zappet", HBEntities.ZAPPET, 0xE38E66, 0x88ACE3);

    public static final DeferredHolder<Item, Item> GILD_GLIDER_SPAWN_EGG = createEgg("gild_glider", HBEntities.GILD_GLIDER, 0xE3D188, 0xDED0B1);

    public static final DeferredHolder<Item, Item> SMARM_SPAWN_EGG = createEgg("chub", HBEntities.CHUB, 0x53A6C9, 0xC9536B);

    public static final DeferredHolder<Item, Item> FEROCETUS_SPAWN_EGG = createEgg("ferocetus", HBEntities.FEROCETUS, 0x75604C, 0xC7A169);

    public static final DeferredHolder<Item, Item> ENDGEL_SPAWN_EGG = createEgg("endgel", HBEntities.ENDGEL, 0xC484C4, 0x8EE695);

    public static final DeferredHolder<Item, Item> DAWN_DOVE_SPAWN_EGG = createEgg("dawn_dove", HBEntities.DAWN_DOVE, 0x61523d, 0x2c261c);

    public static final DeferredHolder<Item, Item> SKIB_SPAWN_EGG = createEgg("skib", HBEntities.SKIB, 0x614946, 0xC22D2D);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static DeferredHolder<Item, Item> createEgg(String name, Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor) {
        return ITEMS.register(name + "_spawn_egg",
                () -> new DeferredSpawnEggItem(type, backgroundColor, highlightColor, new Item.Properties()));
    }

    private static DeferredHolder<Item, Item> createBucket(String name, Supplier<? extends EntityType<?>> type) {
        return ITEMS.register(name + "_bucket",
                () -> new WaterMobBucketItem(type));
    }

}
