package com.hedge.hedges_bestiary.items;

import com.hedge.hedges_bestiary.HedgesBestiary;
import com.hedge.hedges_bestiary.registry.HBEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class HBItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HedgesBestiary.MODID);

    public static final Rarity RARITY_ENDGELIC = Rarity.create("hedges_bestiary:endgelic", ChatFormatting.GREEN);
    public static final Rarity RARITY_AQUATIC = Rarity.create("hedges_bestiary:aquatic", ChatFormatting.BLUE);

    //DROPS | RESOURCES
    public static final RegistryObject<Item> MURK_SPIKE = ITEMS.register("murk_spike",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TEARACUDA_TOOTH = ITEMS.register("tearacuda_tooth",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ENDGELIC_HEART = ITEMS.register("endgelic_heart",
            () -> new Item(new Item.Properties().rarity(RARITY_ENDGELIC)));

    public static final RegistryObject<Item> FINE_MILK = ITEMS.register("fine_milk_bottle",
            () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE)));

    public static final RegistryObject<Item> RICH_MILK = ITEMS.register("rich_milk_bottle",
            () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE)));

    public static final RegistryObject<Item> ZIPPERMOUTH_SCALES = ITEMS.register("zippermouth_scales",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    // CRAFTED ITEMS

    public static final RegistryObject<Item> PLAIN_TREAT = ITEMS.register("plain_treat",
            () -> new TreatItem(new Item.Properties().rarity(Rarity.COMMON), 0));

    public static final RegistryObject<Item> SEASONED_TREAT = ITEMS.register("seasoned_treat",
            () -> new TreatItem(new Item.Properties().rarity(Rarity.UNCOMMON),1));

    public static final RegistryObject<Item> HEARTY_TREAT = ITEMS.register("hearty_treat",
            () -> new TreatItem(new Item.Properties().rarity(Rarity.RARE),2));

    public static final RegistryObject<Item> LUSCIOUS_TREAT = ITEMS.register("luscious_treat",
            () -> new TreatItem(new Item.Properties().rarity(Rarity.EPIC),3));

    public static final RegistryObject<Item> ENDGELIC_JUDGEMENT = ITEMS.register("endgelic_judgement",
            () -> new EndgelicJudgementItem(new Item.Properties().durability(500).rarity(RARITY_ENDGELIC)));

    public static final RegistryObject<Item> MURKLEVOLENCE = ITEMS.register("murklevolence",
            () -> new MurklevolenceItem(new Item.Properties().durability(800).rarity(RARITY_AQUATIC)));

    // FOODS

    public static final RegistryObject<Item> RAW_URKMEAT = ITEMS.register("raw_urkmeat",
            () -> new Item(new Item.Properties().food(HBFoodTypes.RAW_URKMEAT)));

    public static final RegistryObject<Item> COOKED_URKMEAT = ITEMS.register("cooked_urkmeat",
            () -> new Item(new Item.Properties().food(HBFoodTypes.COOKED_URKMEAT)));

    public static final RegistryObject<Item> SKIB = ITEMS.register("skib",
            () -> new Item(new Item.Properties().food(HBFoodTypes.SKIB)));

    // BUCKETS

    public static final RegistryObject<Item> CHUB_BUCKET = createBucket("chub", HBEntities.CHUB);

    public static final RegistryObject<Item> GILD_GLIDER_BUCKET = createBucket("gild_glider", HBEntities.GILD_GLIDER);

    public static final RegistryObject<Item> SKIB_BUCKET = createBucket("skib", HBEntities.SKIB);


    // SPAWN EGGS
    public static final RegistryObject<Item> BURODON_SPAWN_EGG = ITEMS.register("burodon_spawn_egg",
            () -> new ForgeSpawnEggItem(HBEntities.BURODON, 0xAEC8D4, 0x7E83CC, new Item.Properties()));

    public static final RegistryObject<Item> SPOTTED_STRIKER_SPAWN_EGG = ITEMS.register("spotted_striker_spawn_egg",
            () -> new ForgeSpawnEggItem(HBEntities.SPOTTED_STRIKER, 0x50555E, 0x8A896B, new Item.Properties()));

    public static final RegistryObject<Item> PLOMBO_SPAWN_EGG = createEgg("plombo", HBEntities.PLOMBO, 0x2D2F3B, 0x827363);

    public static final RegistryObject<Item> GURK_SPAWN_EGG = ITEMS.register("gurk_spawn_egg",
            () -> new ForgeSpawnEggItem(HBEntities.GURK, 0x2E7037, 0x63A64E, new Item.Properties()));

    public static final RegistryObject<Item> MURK_SPAWN_EGG = ITEMS.register("murk_spawn_egg",
            () -> new ForgeSpawnEggItem(HBEntities.MURK, 0x473B8A, 0xC4BE84, new Item.Properties()));

    public static final RegistryObject<Item> TEARACUDA_SPAWN_EGG = ITEMS.register("tearacuda_spawn_egg",
            () -> new ForgeSpawnEggItem(HBEntities.TEARACUDA, 0x6898D4, 0xD4BB68, new Item.Properties()));

    public static final RegistryObject<Item> ZAPPET_SPAWN_EGG = createEgg("zappet", HBEntities.ZAPPET, 0xE38E66, 0x88ACE3);

    public static final RegistryObject<Item> GILD_GLIDER_SPAWN_EGG = createEgg("gild_glider", HBEntities.GILD_GLIDER, 0xE3D188, 0xDED0B1);

    public static final RegistryObject<Item> SMARM_SPAWN_EGG = createEgg("chub", HBEntities.CHUB, 0x53A6C9, 0xC9536B);

    public static final RegistryObject<Item> FEROCETUS_SPAWN_EGG = createEgg("ferocetus", HBEntities.FEROCETUS, 0x75604C, 0xC7A169);

    public static final RegistryObject<Item> ENDGEL_SPAWN_EGG = createEgg("endgel", HBEntities.ENDGEL, 0xC484C4, 0x8EE695);

    public static final RegistryObject<Item> DAWN_DOVE_SPAWN_EGG = createEgg("dawn_dove", HBEntities.DAWN_DOVE, 0x61523d, 0x2c261c);

    public static final RegistryObject<Item> SKIB_SPAWN_EGG = createEgg("skib", HBEntities.SKIB, 0x614946, 0xC22D2D);

    public static final RegistryObject<Item> BLURPUM_SPAWN_EGG = createEgg("blurpum", HBEntities.BLURPUM, 0x614946, 0x68787D);

    public static final RegistryObject<Item> GAWK_SPAWN_EGG = createEgg("gawk", HBEntities.GAWK, 0x2D2933, 0x7584C7);

    public static final RegistryObject<Item> GOPH_SPAWN_EGG = createEgg("goph", HBEntities.GOPH, 0x9E7C68, 0xB55E5E);

    public static final RegistryObject<Item> ZIPPERMOUTH_SPAWN_EGG = createEgg("zippermouth", HBEntities.ZIPPERMOUTH, 0xB52F16, 0x389FBA);

    public static final RegistryObject<Item> ZIPPERLING_SPAWN_EGG = createEgg("zipperling", HBEntities.ZIPPERLING, 0x6B4B45, 0x8A624A);

    public static final RegistryObject<Item> NIB_NIB_SPAWN_EGG = createEgg("nib_nib", HBEntities.NIB_NIB, 0xB5AA57, 0xABC1C4);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static RegistryObject<Item> createEgg(String name, Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor) {
        return ITEMS.register(name + "_spawn_egg",
                () -> new ForgeSpawnEggItem(type, backgroundColor, highlightColor, new Item.Properties()));
    }

    private static RegistryObject<Item> createBucket(String name, Supplier<? extends EntityType<?>> type) {
        return ITEMS.register(name + "_bucket",
                () -> new WaterMobBucketItem(type));
    }

}
