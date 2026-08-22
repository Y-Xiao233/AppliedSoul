package net.yxiao233.appliedsoul.mixin;

import com.buuz135.industrialforegoingsouls.block.tile.SoulSurgeBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.yxiao233.appliedsoul.common.me.logic.SoulBroadcastLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoulSurgeBlockEntity.class)
public class SoulSurgeBlockEntityMixin {
    @Inject(
            method = "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lcom/buuz135/industrialforegoingsouls/block/tile/SoulSurgeBlockEntity;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void appliedsoul$serverTick(Level level, BlockPos pos, BlockState state, SoulSurgeBlockEntity blockEntity, CallbackInfo ci){
        BlockPos relativePos = pos.relative(state.getValue(RotatableBlock.FACING_ALL).getOpposite());
        if(SoulBroadcastLogic.contains(SoulBroadcastLogic.getAllConnectMachines(),relativePos)){
            ci.cancel();
        }
    }
}
