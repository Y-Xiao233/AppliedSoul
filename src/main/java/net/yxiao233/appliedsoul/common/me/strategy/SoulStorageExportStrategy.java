package net.yxiao233.appliedsoul.common.me.strategy;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import com.buuz135.industrialforegoingsouls.capabilities.ISoulHandler;
import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilities;
import com.buuz135.soulplied_energistics.applied.SoulKey;
import com.google.common.primitives.Ints;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

@SuppressWarnings("UnstableApiUsage")
public class SoulStorageExportStrategy implements StackExportStrategy {
    private final BlockCapabilityCache<ISoulHandler, Direction> cache;

    public SoulStorageExportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        cache = BlockCapabilityCache.create(SoulCapabilities.BLOCK, level, fromPos, fromSide);
    }

    @Override
    public long transfer(StackTransferContext context, AEKey what, long amount) {
        if (!(what instanceof SoulKey)) {
            return 0;
        }

        var sourceTile = cache.getCapability();

        if (sourceTile != null) {
            var insertable = sourceTile.fill(Ints.saturatedCast(amount), ISoulHandler.Action.SIMULATE);
            var extracted = (int) StorageHelper.poweredExtraction(
                    context.getEnergySource(),
                    context.getInternalStorage().getInventory(),
                    SoulKey.INSTANCE,
                    insertable,
                    context.getActionSource(),
                    Actionable.MODULATE);

            if (extracted > 0) {
                sourceTile.fill(extracted, ISoulHandler.Action.EXECUTE);
            }

            return extracted;
        }

        return 0;
    }

    @Override
    public long push(AEKey what, long amount, Actionable mode) {
        if (!(what instanceof SoulKey)) {
            return 0;
        }

        var sourceTile = cache.getCapability();
        return sourceTile != null ? sourceTile.fill(Ints.saturatedCast(amount), mode.isSimulate() ? ISoulHandler.Action.SIMULATE : ISoulHandler.Action.EXECUTE) : 0;
    }
}
