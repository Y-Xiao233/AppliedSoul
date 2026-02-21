package net.yxiao233.appliedsoul.mixin;

import com.buuz135.industrialforegoingsouls.block.tile.NetworkBlockEntity;
import com.buuz135.industrialforegoingsouls.block.tile.SoulSurgeBlockEntity;
import com.buuz135.industrialforegoingsouls.block_network.SoulNetwork;
import com.buuz135.industrialforegoingsouls.config.ConfigSoulSurge;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.yxiao233.appliedsoul.common.capabilities.ISoulHandler;
import net.yxiao233.appliedsoul.common.capabilities.SoulCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(SoulSurgeBlockEntity.class)
public abstract class MixinSoulSurgeBlockEntity extends NetworkBlockEntity<SoulSurgeBlockEntity> {
    @Shadow private int tickingTime;

    public MixinSoulSurgeBlockEntity(BasicTileBlock<SoulSurgeBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
    }
    @Inject(
            method = "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lcom/buuz135/industrialforegoingsouls/block/tile/SoulSurgeBlockEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void afterGetBlockEntity(Level level, BlockPos pos, BlockState state, SoulSurgeBlockEntity blockEntity, CallbackInfo ci, SoulNetwork network, Iterator<NetworkElement> iterator, NetworkElement soulLaserDrill) {
        BlockEntity entity = soulLaserDrill.getLevel().getBlockEntity(soulLaserDrill.getPos());
        if (entity != null && entity.getCapability(SoulCapabilities.BLOCK, Direction.UP).isPresent()) {
            entity.getCapability(SoulCapabilities.BLOCK, Direction.UP).ifPresent(cap ->{
                if(cap.getSoulInTank(0) > 0){
                    cap.drain(1, ISoulHandler.Action.EXECUTE);
                    this.tickingTime = ConfigSoulSurge.SOUL_TIME;
                }
            });
        }
    }
}
