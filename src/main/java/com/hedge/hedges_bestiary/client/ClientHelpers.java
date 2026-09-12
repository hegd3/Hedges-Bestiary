package com.hedge.hedges_bestiary.client;

import com.hedge.hedges_bestiary.HedgesBestiary;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ClientHelpers {


    public static ResourceLocation[] generateVariants(String path, int count) {
        ResourceLocation[] textures = new ResourceLocation[count];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/" + path + "_" + i + ".png");
        }
        return textures;
    }

    public static RenderType[] generateEyeVariants(String path, int count) {
        RenderType[] textures = new RenderType[count];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(HedgesBestiary.MODID, "textures/entity/" + path + "_" + i + ".png"));
        }
        return textures;
    }

}

