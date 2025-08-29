package net.yxiao233.appliedsoul.common.me.strategy;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import com.buuz135.industrialforegoingsouls.capabilities.ISoulHandler;
import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilities;
import com.buuz135.soulplied_energistics.applied.SoulAEKeyType;
import com.buuz135.soulplied_energistics.applied.SoulKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

@SuppressWarnings("UnstableApiUsage")
public class SoulStorageImportStrategy implements StackImportStrategy {
    private final BlockCapabilityCache<ISoulHandler, Direction> cache;

    public SoulStorageImportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        cache = BlockCapabilityCache.create(SoulCapabilities.BLOCK, level, fromPos, fromSide);
    }
    @Override
    public boolean transfer(StackTransferContext context) {
        if (!context.isKeyTypeEnabled(SoulAEKeyType.TYPE)) {
            return false;
        }

        var soulTile = cache.getCapability();

        if (soulTile == null) {
            return false;
        }

        int remainingTransferAmount = context.getOperationsRemaining() * SoulAEKeyType.TYPE.getAmountPerOperation();
        int rawAmount = Math.min(remainingTransferAmount, soulTile.getSoulInTank(0));

        var inv = context.getInternalStorage().getInventory();


        var amount = (int) inv.insert(SoulKey.INSTANCE, rawAmount, Actionable.SIMULATE, context.getActionSource());

        if (amount > 0) {
            soulTile.drain(amount, ISoulHandler.Action.EXECUTE);
            var inserted = inv.insert(SoulKey.INSTANCE, amount, Actionable.MODULATE, context.getActionSource());

            if (inserted < amount) {
                var leftover = amount - inserted;
                var backFill = (int) Math.min(leftover, soulTile.getTankCapacity(0) - soulTile.getSoulInTank(0));

                if (backFill > 0) {
                    soulTile.fill(backFill, ISoulHandler.Action.EXECUTE);
                }
            }

            var opsUsed = Math.max(1, inserted / SoulAEKeyType.TYPE.getAmountPerOperation());
            context.reduceOperationsRemaining(opsUsed);
        }

        return amount > 0;
    }
}
