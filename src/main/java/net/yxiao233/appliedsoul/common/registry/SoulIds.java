package net.yxiao233.appliedsoul.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.yxiao233.appliedsoul.AppliedSoul;

public enum SoulIds implements EntryIds{
    //Block
    SOUL_COLLECTOR("Soul Collector","soul_collector"),
    SOUL_BROADCAST("Soul Broadcast","soul_broadcast"),

    //Item
    RANGE_CARD("Range Card","range_card"),
    ENDER_STAR("Ender Star","ender_star"),
    BROADCAST_CONNECT_TOOL("Broadcast Connect Tool","broadcast_connect_tool"),
    SOUL_CELL_HOUSING("ME Soul Cell Housing","soul_cell_housing")
    ;

    private final String englishName;
    private final String shortId;
    SoulIds(String englishName, String shortId){
        this.englishName = englishName;
        this.shortId = shortId;
    }

    @Override
    public String getEnglishName() {
        return englishName;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return AppliedSoul.makeId(this.shortId);
    }

    @Override
    public String getShortId(){
        return this.shortId;
    }
}
