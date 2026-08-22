package net.yxiao233.appliedsoul.common.block;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.locator.MenuLocators;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.yxiao233.appliedsoul.common.block.entity.SoulCollectorBlockEntity;
import org.jetbrains.annotations.NotNull;

public class SoulCollectorBlock extends AEBaseEntityBlock<SoulCollectorBlockEntity> {
    public SoulCollectorBlock() {
        super(metalProps());
    }
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SoulCollectorBlockEntity be) {
            if (!level.isClientSide()) {
                be.openMenu(player, MenuLocators.forBlockEntity(be));
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
    }
}
