package net.yxiao233.appliedsoul.common.registry;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.items.storage.StorageTier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.item.BroadcastConnectToolItem;
import net.yxiao233.appliedsoul.common.item.EnderStarItem;
import net.yxiao233.appliedsoul.common.item.SoulCellItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SoulItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AppliedSoul.MODID);

    private static final List<ItemDefinition<?>> LIST = new ArrayList<>();

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(LIST);
    }
    public static List<ItemDefinition<SoulCellItem>> getCells() {
        return List.of(SOUL_CELL_1K, SOUL_CELL_4K, SOUL_CELL_16K, SOUL_CELL_64K, SOUL_CELL_256K);
    }

    public static final ItemDefinition<MaterialItem> SOUL_CELL_HOUSING = item(SoulIds.SOUL_CELL_HOUSING, MaterialItem::new);
    public static final ItemDefinition<SoulCellItem> SOUL_CELL_1K = cell(StorageTier.SIZE_1K);
    public static final ItemDefinition<SoulCellItem> SOUL_CELL_4K = cell(StorageTier.SIZE_4K);
    public static final ItemDefinition<SoulCellItem> SOUL_CELL_16K = cell(StorageTier.SIZE_16K);
    public static final ItemDefinition<SoulCellItem> SOUL_CELL_64K = cell(StorageTier.SIZE_64K);
    public static final ItemDefinition<SoulCellItem> SOUL_CELL_256K = cell(StorageTier.SIZE_256K);
    public static final ItemDefinition<Item> RANGE_CARD = item(SoulIds.RANGE_CARD, Upgrades::createUpgradeCardItem);
    public static final ItemDefinition<Item> ENDER_STAR = item(SoulIds.ENDER_STAR, EnderStarItem::new);
    public static final ItemDefinition<BroadcastConnectToolItem> BROADCAST_CONNECT_TOOL = item(SoulIds.BROADCAST_CONNECT_TOOL, BroadcastConnectToolItem::new);


    private static ItemDefinition<SoulCellItem> cell(StorageTier tier) {
        return item(new SoulDefinitionId(tier.namePrefix() + " ME Soul Storage Cell","soul_storage_cell_" + tier.namePrefix()), p -> new SoulCellItem(p.stacksTo(1), tier));
    }

    public static <T extends Item> ItemDefinition<T> item(EntryIds entry, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(entry.getEnglishName(), ITEMS.registerItem(entry.getShortId(), factory));
        LIST.add(definition);
        return definition;
    }
}
