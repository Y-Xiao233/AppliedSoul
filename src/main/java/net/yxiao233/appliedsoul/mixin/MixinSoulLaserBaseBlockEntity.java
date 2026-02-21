package net.yxiao233.appliedsoul.mixin;

import com.buuz135.industrial.block.tile.IndustrialMachineTile;
import com.buuz135.industrialforegoingsouls.block.tile.SoulLaserBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.RegistryObject;
import net.yxiao233.appliedsoul.common.capabilities.IUseSoul;
import net.yxiao233.appliedsoul.common.capabilities.SLBSoulCap;
import net.yxiao233.appliedsoul.common.capabilities.SoulCapabilities;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoulLaserBaseBlockEntity.class)
public abstract class MixinSoulLaserBaseBlockEntity extends IndustrialMachineTile<SoulLaserBaseBlockEntity> implements IUseSoul {
    @Shadow private int soulAmount;
    @Shadow public abstract @NotNull SoulLaserBaseBlockEntity getSelf();
    @Unique
    @Override
    public int useSoul(int amount){
        int oldAmount = this.soulAmount;
        this.soulAmount = Math.max(0, this.soulAmount - amount);
        this.syncObject(this.soulAmount);
        return oldAmount - this.soulAmount;
    }

    @Inject(method = "useSoul", at = @At("HEAD"), cancellable = true)
    private void onUseSoul(CallbackInfo ci){
        this.useSoul(1);
        ci.cancel();
    }

    public MixinSoulLaserBaseBlockEntity(Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> basicTileBlock, BlockPos blockPos, BlockState blockState) {
        super(basicTileBlock, blockPos, blockState);
    }

    @NotNull
    @Override
    public <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction side) {
        if(cap == SoulCapabilities.BLOCK){
            return LazyOptional.of(() -> new SLBSoulCap(this.getSelf())).cast();
        }
        return super.getCapability(cap, side);
    }
}
