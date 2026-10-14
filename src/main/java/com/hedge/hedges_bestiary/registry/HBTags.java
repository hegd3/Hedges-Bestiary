package com.hedge.hedges_bestiary.registry;

import com.hedge.hedges_bestiary.HedgesBestiary;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class HBTags {

    public static final TagKey<EntityType<?>> BURODON_TARGETS = registerEntityTag("burodon_targets");


    public static final TagKey<EntityType<?>> DAWN_DOVE_TARGETS = registerEntityTag("dawn_dove_targets");

    public static final TagKey<EntityType<?>> FEROCETUS_TARGETS = registerEntityTag("ferocetus_targets");

    public static final TagKey<EntityType<?>> SPOTTED_STRIKER_TARGETS = registerEntityTag("spotted_striker_targets");

    public static final TagKey<EntityType<?>> TEARACUDA_AVOIDS = registerEntityTag("tearacuda_avoids");

    public static final TagKey<EntityType<?>> BYPASSES_ZAPPET_SHIELD = registerEntityTag("bypasses_zappet_shield");

    public static final TagKey<EntityType<?>> BLURPUM_TARGETS = registerEntityTag("blurpum_targets");

    public static final TagKey<Item> DAWN_DOVE_FOOD = registerItemTag("dawn_dove_food");

    public static final TagKey<Item> TREATS = registerItemTag("treats");

    private static TagKey<EntityType<?>> registerEntityTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, name));
    }

    private static TagKey<Item> registerForgeItemTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", name));
    }

    private static TagKey<Item> registerItemTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, name));
    }

    private static TagKey<Block> registerBlockTag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, name));
    }

    private static TagKey<Biome> registerBiomeTag(String name) {
        return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, name));
    }
}
