package net.yxiao233.appliedsoul.mixin;

import com.buuz135.industrialforegoingsouls.block_network.SoulNetwork;
import com.hrznstudio.titanium.block_network.Network;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.yxiao233.appliedsoul.common.capabilities.SoulCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SoulNetwork.class)
public abstract class MixinSoulNetwork extends Network {
    @Shadow private List<NetworkElement> queueNetworkElements;

    @Shadow private List<NetworkElement> soulLaserDrills;

    public MixinSoulNetwork(BlockPos originPos, String id) {
        super(originPos, id);
    }

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void onUpdate(Level level, CallbackInfo ci){
        super.update(level);
        for (NetworkElement element : this.queueNetworkElements) {
            BlockEntity tile = element.getLevel().getBlockEntity(element.getPos());
            if(tile != null && tile.getCapability(SoulCapabilities.BLOCK, Direction.UP).isPresent()){
                this.soulLaserDrills.add(element);
            }
        }

        this.queueNetworkElements.clear();
        ci.cancel();
    }
}
