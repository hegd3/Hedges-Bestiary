package com.hedge.hedges_bestiary.config;

import com.hedge.hedges_bestiary.HedgesBestiary;
import net.neoforged.neoforge.common.ModConfigSpec;

public class HBConfig
{


    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue VALUE_TAMING_DISABLED;
    public static final ModConfigSpec.BooleanValue VALUE_BREEDING_REQUIRES_TAME;

    public static final ModConfigSpec.BooleanValue VALUE_GURK_TAMABLE;
    public static final ModConfigSpec.BooleanValue VALUE_BURODON_TAMABLE;
    public static final ModConfigSpec.BooleanValue VALUE_ZAPPET_TAMABLE;
    public static final ModConfigSpec.BooleanValue VALUE_PLOMBO_TAMABLE;
    public static final ModConfigSpec.BooleanValue VALUE_MURK_TAMABLE;
    public static final ModConfigSpec.BooleanValue VALUE_DAWN_DOVE_TAMABLE;
    public static final ModConfigSpec.BooleanValue VALUE_FEROCETUS_TAMABLE;


    public static boolean TAMING_DISABLED = false;
    public static boolean BREEDING_REQUIRES_TAME = true;
    public static boolean GURK_IS_TAMABLE = true;
    public static boolean BURODON_IS_TAMABLE = true;
    public static boolean ZAPPET_IS_TAMABLE = true;
    public static boolean PLOMBO_IS_TAMABLE = true;
    public static boolean MURK_IS_TAMABLE = true;
    public static boolean DAWN_DOVE_IS_TAMABLE = true;
    public static boolean FEROCETUS_IS_TAMABLE = true;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("Hedge's Bestiary");
        VALUE_TAMING_DISABLED = buildBoolean(builder, "Disable all taming", false, "Disables ALL Hedge's Bestiary pets from being tamed. DEFAULT: false");
        VALUE_BREEDING_REQUIRES_TAME = buildBoolean(builder, "Only breed when tamed", true, "Whether Hedge's Bestiary pets must be tame in order to breed. Ignored by mobs configured to be untamable. DEFAULT: true");
        VALUE_GURK_TAMABLE = buildBoolean(builder, "Gurk taming enabled", true, "Whether gurks can be tamed or not. DEFAULT: true");
        VALUE_BURODON_TAMABLE = buildBoolean(builder, "Burodon taming enabled", true, "Whether burodons can be tamed or not. DEFAULT: true");
        VALUE_ZAPPET_TAMABLE = buildBoolean(builder, "Zappet taming enabled", true, "Whether zappets can be tamed or not. DEFAULT: true");
        VALUE_PLOMBO_TAMABLE = buildBoolean(builder, "Plombo taming enabled", true, "Whether plombos can be tamed or not. DEFAULT: true");
        VALUE_DAWN_DOVE_TAMABLE = buildBoolean(builder, "Dawn Dove taming enabled", true, "Whether dawn doves can be tamed or not. DEFAULT: true");
        VALUE_MURK_TAMABLE = buildBoolean(builder, "Murk taming enabled", true, "Whether murks can be tamed or not. DEFAULT: true");
        VALUE_FEROCETUS_TAMABLE = buildBoolean(builder, "Ferocetus taming enabled", true, "Whether fercetus can be tamed or not. DEFAULT: true");

        builder.pop();
        SPEC = builder.build();

    }

    public static void bake() {
        try {
            TAMING_DISABLED = VALUE_TAMING_DISABLED.get();
            BREEDING_REQUIRES_TAME = VALUE_BREEDING_REQUIRES_TAME.get();
            GURK_IS_TAMABLE = VALUE_GURK_TAMABLE.get();
            BURODON_IS_TAMABLE = VALUE_BURODON_TAMABLE.get();
            ZAPPET_IS_TAMABLE = VALUE_ZAPPET_TAMABLE.get();
            PLOMBO_IS_TAMABLE = VALUE_PLOMBO_TAMABLE.get();
            MURK_IS_TAMABLE = VALUE_MURK_TAMABLE.get();
            DAWN_DOVE_IS_TAMABLE = VALUE_DAWN_DOVE_TAMABLE.get();
            FEROCETUS_IS_TAMABLE = VALUE_FEROCETUS_TAMABLE.get();

        } catch (Exception e) {
            HedgesBestiary.LOGGER.warn("An exception was caused trying to load the config for Hedge's Bestiary", e);
            e.printStackTrace();
        }

    }


    private static ModConfigSpec.BooleanValue buildBoolean(ModConfigSpec.Builder builder, String name, boolean defaultValue, String comment) {
        return builder.comment(comment).define(name, defaultValue);
    }


    private static ModConfigSpec.IntValue buildInt(ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

}
