package net.yxiao233.appliedsoul.common.registry;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.DeferredBlockEntityType;
import com.google.common.base.Preconditions;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.block.entity.SoulBroadcastBlockEntity;
import net.yxiao233.appliedsoul.common.block.entity.SoulCollectorBlockEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SoulBlockEntities {
    private static final List<DeferredBlockEntityType<?>> BLOCK_ENTITY_TYPES = new ArrayList<>();
    public static final DeferredRegister<BlockEntityType<?>> DR = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AppliedSoul.MODID);
    public static final DeferredBlockEntityType<SoulCollectorBlockEntity> SOUL_COLLECTOR = create(SoulIds.SOUL_COLLECTOR, SoulCollectorBlockEntity.class, SoulCollectorBlockEntity::new, SoulBlocks.SOUL_COLLECTOR);
    public static final DeferredBlockEntityType<SoulBroadcastBlockEntity> SOUL_BROADCAST = create(SoulIds.SOUL_BROADCAST, SoulBroadcastBlockEntity.class, SoulBroadcastBlockEntity::new, SoulBlocks.SOUL_BROADCAST);

    @SafeVarargs
    @SuppressWarnings("all")
    private static <T extends AEBaseBlockEntity> DeferredBlockEntityType<T> create(EntryIds entry, Class<T> entityClass, BlockEntityFactory<T> factory, BlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefinitions) {
        Preconditions.checkArgument(blockDefinitions.length > 0);
        String shortId = entry.getShortId();
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> deferred = DR.register(shortId, () -> {
            AtomicReference<BlockEntityType<T>> typeHolder = new AtomicReference<>();
            BlockEntityType.BlockEntitySupplier<T> supplier = (blockPos, blockState) -> {
                return (T) factory.create((BlockEntityType)typeHolder.get(), blockPos, blockState);
            };
            AEBaseEntityBlock[] blocks = (AEBaseEntityBlock[]) Arrays.stream(blockDefinitions).map(BlockDefinition::block).toArray(AEBaseEntityBlock[]::new);
            BlockEntityType<T> type = BlockEntityType.Builder.of(supplier, blocks).build((Type)null);
            typeHolder.setPlain(type);
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

            int var10 = blocks.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                AEBaseEntityBlock block = blocks[var11];
                block.setBlockEntity(entityClass, type, clientTicker, serverTicker);
            }

            return type;
        });
        DeferredBlockEntityType<T> result = new DeferredBlockEntityType<>(entityClass, deferred);
        BLOCK_ENTITY_TYPES.add(result);
        return result;
    }

    public static List<DeferredBlockEntityType<?>> getBlockEntityTypes(){
        return BLOCK_ENTITY_TYPES;
    }

    @FunctionalInterface
    interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> var1, BlockPos var2, BlockState var3);
    }
}
