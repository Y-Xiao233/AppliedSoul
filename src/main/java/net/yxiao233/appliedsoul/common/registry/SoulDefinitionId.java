package net.yxiao233.appliedsoul.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.yxiao233.appliedsoul.AppliedSoul;

public record SoulDefinitionId(String englishName, String shortId) implements EntryIds{
    @Override
    public String getShortId() {
        return shortId;
    }

    @Override
    public String getEnglishName() {
        return englishName;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return AppliedSoul.makeId(shortId);
    }
}
