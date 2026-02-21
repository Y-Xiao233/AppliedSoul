package net.yxiao233.appliedsoul.common.me.strategy;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import com.buuz135.industrialforegoingsouls.IndustrialForegoingSouls;
import com.buuz135.industrialforegoingsouls.block.tile.SoulLaserBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.yxiao233.appliedsoul.common.capabilities.ISoulHandler;
import net.yxiao233.appliedsoul.common.capabilities.SoulCapabilities;
import net.yxiao233.appliedsoul.common.key.SoulKey;

import javax.annotation.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class SoulExternalStorageStrategy implements ExternalStorageStrategy {

    private final ServerLevel level;
    private final BlockPos fromPos;

    public SoulExternalStorageStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.level = level;
        this.fromPos = fromPos;
    }

    @Override
    public @Nullable MEStorage createWrapper(boolean b, Runnable runnable) {
        BlockEntity blockEntity = this.level.getBlockEntity(fromPos, IndustrialForegoingSouls.SOUL_LASER_BLOCK.getRight().get()).get();
        if (blockEntity instanceof SoulLaserBaseBlockEntity entity){
            return new SoulLaserDrillMEStorage(entity.getCapability(SoulCapabilities.BLOCK));
        }
        return null;
    }

    private record SoulLaserDrillMEStorage(LazyOptional<ISoulHandler> handler) implements MEStorage {

        @Override
        public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
            return MEStorage.super.isPreferredStorageFor(what, source);
        }

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            handler.ifPresent(capability ->{
                capability.fill((int) amount, mode.isSimulate() ? ISoulHandler.Action.SIMULATE : ISoulHandler.Action.EXECUTE);
            });
            return 0;
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            handler.ifPresent(capability ->{
                capability.drain((int) amount, mode.isSimulate() ? ISoulHandler.Action.SIMULATE : ISoulHandler.Action.EXECUTE);
            });
            return 0;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            handler.ifPresent(capability ->{
                for (int i = 0; i < capability.getSoulTanks(); i ++){
                    out.add(SoulKey.INSTANCE, capability.getSoulInTank(i));
                }
            });
        }

        @Override
        public Component getDescription() {
            return Component.translatable("aekey.soulkey.description");
        }

        @Override
        public KeyCounter getAvailableStacks() {
            return MEStorage.super.getAvailableStacks();
        }
    }
}