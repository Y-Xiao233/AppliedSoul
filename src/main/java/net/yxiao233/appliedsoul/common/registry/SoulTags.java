package net.yxiao233.appliedsoul.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.yxiao233.appliedsoul.AppliedSoul;

public class SoulTags {
    public static class Items{
        public static final TagKey<Item> DUSTS = createForgeTag("dusts");
        public static final TagKey<Item> ENDER_PEARL_DUSTS = createForgeTag("dusts/ender_pearl");
        private static TagKey<Item> createTag(String name){
            return ItemTags.create(AppliedSoul.makeId(name));
        }

        private static TagKey<Item> createForgeTag(String name){
            return ItemTags.create(new ResourceLocation("forge",name));
        }
    }
}
