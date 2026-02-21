package net.yxiao233.appliedsoul.common.me.strategy;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.util.BlockApiCache;
import com.google.common.primitives.Ints;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.yxiao233.appliedsoul.common.capabilities.ISoulHandler;
import net.yxiao233.appliedsoul.common.capabilities.SoulCapabilities;
import net.yxiao233.appliedsoul.common.key.SoulKey;

@SuppressWarnings("UnstableApiUsage")
public class SoulStorageExportStrategy implements StackExportStrategy {
    private final BlockApiCache<ISoulHandler> cache;
    private final Direction fromSide;

    public SoulStorageExportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        cache = BlockApiCache.create(SoulCapabilities.BLOCK, level, fromPos);
        this.fromSide = fromSide;
    }

    @Override
    public long transfer(StackTransferContext context, AEKey what, long amount) {
        if (!(what instanceof SoulKey)) {
            return 0;
        }

        var sourceTile = cache.find(fromSide);

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

        var sourceTile = cache.find(fromSide);
        return sourceTile != null ? sourceTile.fill(Ints.saturatedCast(amount), mode.isSimulate() ? ISoulHandler.Action.SIMULATE : ISoulHandler.Action.EXECUTE) : 0;
    }
}
