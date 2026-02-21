package net.yxiao233.appliedsoul.common.registry;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.AppEng;
import appeng.core.definitions.BlockDefinition;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.block.SoulCollectorBlock;
import net.yxiao233.appliedsoul.common.block.entity.SoulCollectorBlockEntity;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class SoulBlocks {
    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();
    private static final Map<ResourceLocation, BlockEntityType<?>> BLOCK_ENTITIES = new HashMap<>();
    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

    public static final BlockDefinition<SoulCollectorBlock> SOUL_COLLECTOR = block("Soul Collector","soul_collector", SoulCollectorBlock::new);
    public static final BlockEntityType<SoulCollectorBlockEntity> SOUL_COLLECTOR_ENTITY = create("soul_collector",SoulCollectorBlockEntity.class,SoulCollectorBlockEntity::new,SOUL_COLLECTOR);


    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            BLOCKS.forEach(b -> ForgeRegistries.BLOCKS.register(b.id(), b.block()));
        }

        if (event.getRegistryKey().equals(Registries.ITEM)) {
            BLOCKS.forEach(b -> ForgeRegistries.ITEMS.register(b.id(), b.asItem()));
        }

        if (event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE)) {
            BLOCK_ENTITIES.forEach(ForgeRegistries.BLOCK_ENTITY_TYPES::register);
        }
    }
    private static <T extends Block> BlockDefinition<T> block(String englishName, String id, Supplier<T> blockSupplier) {
        var block = blockSupplier.get();
        var item = new BlockItem(block, new Item.Properties());
        var definition = new BlockDefinition<>(englishName, AppliedSoul.makeId(id), block, item);
        BLOCKS.add(definition);
        return definition;
    }

    @SuppressWarnings("all")
    @SafeVarargs
    private static <T extends AEBaseBlockEntity> BlockEntityType<T> create(String shortId, Class<T> entityClass, BlockEntityFactory<T> factory, BlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefinitions) {
        Preconditions.checkArgument(blockDefinitions.length > 0);
        ResourceLocation id = AppEng.makeId(shortId);
        AEBaseEntityBlock[] blocks = Arrays.stream(blockDefinitions).map(BlockDefinition::block).toArray((l) -> {
            return new AEBaseEntityBlock[l];
        });
        AtomicReference<BlockEntityType<T>> typeHolder = new AtomicReference();
        BlockEntityType.BlockEntitySupplier<T> supplier = (blockPos, blockState) -> {
            return (T) factory.create(typeHolder.get(), blockPos, blockState);
        };
        BlockEntityType<T> type = BlockEntityType.Builder.of(supplier, blocks).build(null);
        typeHolder.set(type);
        BLOCK_ENTITIES.put(id, type);
        AEBaseBlockEntity.registerBlockEntityItem(type, blockDefinitions[0].asItem());
        BlockEntityTicker<T> serverTicker = null;
        if (ServerTickingBlockEntity.class.isAssignableFrom(entityClass)) {
            serverTicker = (level, pos, state, entity) -> {
                ((ServerTickingBlockEntity)entity).serverTick();
            };
        }

        BlockEntityTicker<T> clientTicker = null;
        if (ClientTickingBlockEntity.class.isAssignableFrom(entityClass)) {
            clientTicker = (level, pos, state, entity) -> {
                ((ClientTickingBlockEntity)entity).clientTick();
            };
        }

        AEBaseEntityBlock[] var11 = blocks;
        int var12 = blocks.length;

        for(int var13 = 0; var13 < var12; ++var13) {
            AEBaseEntityBlock block = var11[var13];
            block.setBlockEntity(entityClass, type, clientTicker, serverTicker);
        }

        return type;
    }

    @FunctionalInterface
    interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> var1, BlockPos var2, BlockState var3);
    }
}
