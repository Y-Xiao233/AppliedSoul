package net.yxiao233.appliedsoul.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class LevelUtil {
    public static boolean checkDimension(ResourceKey<Level> dimension1, ResourceKey<Level> dimension2) {
        if (dimension1 == null || dimension2 == null) {
            return false;
        }
        return dimension1.equals(dimension2);
    }

    public static boolean checkDimension(String dimension1, String dimension2) {
        if (dimension1 == null || dimension2 == null) {
            return false;
        }
        return dimension1.equals(dimension2);
    }
}
