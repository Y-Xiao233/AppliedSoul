package net.yxiao233.appliedsoul.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.yxiao233.appliedsoul.common.registry.SoulBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SoulLootTablesProvider {
    public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> provider){
        return new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(SoulBlockLootTables::new, LootContextParamSets.BLOCK)
        ),provider);
    }

    public static class SoulBlockLootTables extends BlockLootSubProvider {
        public SoulBlockLootTables(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }
        @Override
        protected void generate() {
            dropSelf(SoulBlocks.SOUL_COLLECTOR.block());
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            //Adding ".nonLootTable()" after the registration method will not generate it here
            ArrayList<Block> list = new ArrayList<>();
            SoulBlocks.DR.getEntries().forEach(entry ->{
                list.add(entry.get());
            });
            return List.copyOf(list);
        }
    }
}
