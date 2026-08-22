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
import net.yxiao233.appliedsoul.common.block.entity.SoulBroadcastBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SoulBroadcastBlock extends AEBaseEntityBlock<SoulBroadcastBlockEntity> {
    public SoulBroadcastBlock() {
        super(metalProps());
    }

    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SoulBroadcastBlockEntity be) {
            if (!level.isClientSide()) {
                be.openMenu(player, MenuLocators.forBlockEntity(be));
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        ((SoulBroadcastBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos))).clear();
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
