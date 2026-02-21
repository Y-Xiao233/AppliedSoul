package net.yxiao233.appliedsoul.mixin;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.capabilities.Capabilities;
import appeng.helpers.InterfaceLogic;
import com.buuz135.industrialforegoingsouls.block_network.DefaultSoulNetworkElement;
import com.buuz135.industrialforegoingsouls.block_network.SoulNetwork;
import com.hrznstudio.titanium.block_network.NetworkManager;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.yxiao233.appliedsoul.common.capabilities.InterfaceSoulCap;
import net.yxiao233.appliedsoul.common.capabilities.SoulCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(InterfaceBlockEntity.class)
public abstract class MixinInterfaceBlockEntity extends AENetworkBlockEntity {
    @Unique
    private boolean appliedSoul$unloaded;
    @Shadow @Final private InterfaceLogic logic;

    public MixinInterfaceBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == SoulCapabilities.BLOCK){
            LazyOptional<GenericInternalInventory> capability = appliedSoul$getCap(Capabilities.GENERIC_INTERNAL_INV, side);
            if (appliedSoul$getCap(Capabilities.GENERIC_INTERNAL_INV,side).isPresent()) {
                return LazyOptional.of(() -> new InterfaceSoulCap(capability.orElseGet(null))).cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Unique
    public @NotNull <T> LazyOptional<T> appliedSoul$getCap(@NotNull Capability<T> cap, @Nullable Direction side){
        LazyOptional<T> result = this.logic.getCapability(cap, side);
        return result.isPresent() ? result : super.getCapability(cap, side);
    }

    public void clearRemoved() {
        super.clearRemoved();
        Level var2 = this.level;
        if (var2 instanceof ServerLevel serverLevel) {
            serverLevel.getServer().submitAsync(() -> {
                NetworkManager networkManager = NetworkManager.get(this.level);
                if (networkManager.getElement(this.worldPosition) == null) {
                    networkManager.addElement(this.createElement(this.level, this.worldPosition));
                }
            });
        }
    }

    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.appliedSoul$unloaded = true;
    }

    public void setRemoved() {
        super.setRemoved();
        if (!this.level.isClientSide && !this.appliedSoul$unloaded) {
            NetworkManager networkManager = NetworkManager.get(this.level);
            NetworkElement pipe = networkManager.getElement(this.worldPosition);
            if (pipe != null) {
                networkManager.removeElement(this.worldPosition);
            }
        }

    }

    protected NetworkElement createElement(Level level, BlockPos pos) {
        return new DefaultSoulNetworkElement(level, pos);
    }

    public SoulNetwork getNetwork() {
        return (SoulNetwork)NetworkManager.get(this.level).getElement(this.worldPosition).getNetwork();
    }
}
