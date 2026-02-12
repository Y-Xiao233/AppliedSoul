package net.yxiao233.appliedsoul.data;

import appeng.core.definitions.BlockDefinition;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.registry.SoulBlocks;

public class SoulBlockStateProvider extends BlockStateProvider {
    public SoulBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, AppliedSoul.MODID,exFileHelper);
    }
    @Override
    protected void registerStatesAndModels() {
        cubeAll(SoulBlocks.SOUL_COLLECTOR);
    }

    private void cubeAll(BlockDefinition<?> blockDefinition){
        simpleBlockWithItem(blockDefinition.block(),cubeAll(blockDefinition.block()));
    }
}
