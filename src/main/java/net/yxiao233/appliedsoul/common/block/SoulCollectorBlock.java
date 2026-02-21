package net.yxiao233.appliedsoul.common.block;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.yxiao233.appliedsoul.common.block.entity.SoulCollectorBlockEntity;
import org.jetbrains.annotations.Nullable;

public class SoulCollectorBlock extends AEBaseEntityBlock<SoulCollectorBlockEntity> {
    public SoulCollectorBlock() {
        super(metalProps());
    }
    @Override
    public InteractionResult onActivated(Level level, BlockPos pos, Player player, InteractionHand hand, @Nullable ItemStack heldItem, BlockHitResult hitResult) {
        if (InteractionUtil.isInAlternateUseMode(player)) {
            return InteractionResult.PASS;
        } else {
            SoulCollectorBlockEntity be = this.getBlockEntity(level, pos);
            if (be != null) {
                if (!level.isClientSide()) {
                    be.openMenu(player, MenuLocators.forBlockEntity(be));
                }

                return InteractionResult.sidedSuccess(level.isClientSide());
            } else {
                return InteractionResult.PASS;
            }
        }
    }
}
