package net.yxiao233.appliedsoul.common.registry;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.block.SoulBroadcastBlock;
import net.yxiao233.appliedsoul.common.block.SoulCollectorBlock;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class SoulBlocks {
    public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(AppliedSoul.MODID);
    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();
    public static final BlockDefinition<SoulCollectorBlock> SOUL_COLLECTOR = block(SoulIds.SOUL_COLLECTOR, SoulCollectorBlock::new);
    public static final BlockDefinition<SoulBroadcastBlock> SOUL_BROADCAST = block(SoulIds.SOUL_BROADCAST, SoulBroadcastBlock::new);

    private static <T extends Block> BlockDefinition<T> block(EntryIds entry, Supplier<T> blockSupplier) {
        return block(entry.getEnglishName(), entry.getResourceLocation(), blockSupplier, null);
    }
    @SuppressWarnings("all")
    private static <T extends Block> BlockDefinition<T> block(String englishName, ResourceLocation id, Supplier<T> blockSupplier, @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        Preconditions.checkArgument(id.getNamespace().equals(AppliedSoul.MODID));
        DeferredBlock<T> deferredBlock = DR.register(id.getPath(), blockSupplier);
        DeferredItem<BlockItem> deferredItem = SoulItems.ITEMS.register(id.getPath(), () -> {
            T block = deferredBlock.get();
            Item.Properties itemProperties = new Item.Properties();
            if (itemFactory != null) {
                BlockItem item = itemFactory.apply(block, itemProperties);
                if (item == null) {
                    throw new IllegalArgumentException("BlockItem factory for " + String.valueOf(id) + " returned null");
                } else {
                    return item;
                }
            } else {
                return block instanceof AEBaseBlock ? new AEBaseBlockItem(block, itemProperties) : new BlockItem(block, itemProperties);
            }
        });
        ItemDefinition<BlockItem> itemDef = new ItemDefinition<>(englishName, deferredItem);
        BlockDefinition<T> definition = new BlockDefinition<>(englishName, deferredBlock, itemDef);
        BLOCKS.add(definition);
        return definition;
    }

    public static List<BlockDefinition<?>> getBlocks(){
        return BLOCKS;
    }
}
