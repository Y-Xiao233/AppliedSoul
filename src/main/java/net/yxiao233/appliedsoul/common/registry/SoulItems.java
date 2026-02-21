package net.yxiao233.appliedsoul.common.registry;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.items.storage.StorageTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.item.EnderStarItem;
import net.yxiao233.appliedsoul.common.item.SoulCellItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SoulItems {

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ITEM)) {
            ITEMS.forEach(i -> ForgeRegistries.ITEMS.register(i.id(), i.asItem()));
        }
    }
    public static List<ItemDefinition<SoulCellItem>> getCells() {
        return List.of(SOUL_CELL_1K, SOUL_CELL_4K, SOUL_CELL_16K, SOUL_CELL_64K, SOUL_CELL_256K);
    }

    public static final ItemDefinition<MaterialItem> SOUL_CELL_HOUSING = item("Soul Cell Housing","soul_cell_housing", MaterialItem::new);
    public static final ItemDefinition<SoulCellItem> SOUL_CELL_1K = cell(StorageTier.SIZE_1K);
    public static final ItemDefinition<SoulCellItem> SOUL_CELL_4K = cell(StorageTier.SIZE_4K);
    public static final ItemDefinition<SoulCellItem> SOUL_CELL_16K = cell(StorageTier.SIZE_16K);
    public static final ItemDefinition<SoulCellItem> SOUL_CELL_64K = cell(StorageTier.SIZE_64K);
    public static final ItemDefinition<SoulCellItem> SOUL_CELL_256K = cell(StorageTier.SIZE_256K);
    public static final ItemDefinition<Item> RANGE_CARD = item("Range Card","range_card", Upgrades::createUpgradeCardItem);
    public static final ItemDefinition<Item> ENDER_STAR = item("Ender Star","ender_star", EnderStarItem::new);


    private static ItemDefinition<SoulCellItem> cell(StorageTier tier) {
        return item(
                tier.namePrefix() + " ME Soul Storage Cell",
                "soul_storage_cell_" + tier.namePrefix(),
                p -> new SoulCellItem(p.stacksTo(1), tier, SOUL_CELL_HOUSING));
    }

    public static <T extends Item> ItemDefinition<T> item(String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(englishName, AppliedSoul.makeId(id), factory.apply(new Item.Properties()));
        ITEMS.add(definition);
        return definition;
    }
}
