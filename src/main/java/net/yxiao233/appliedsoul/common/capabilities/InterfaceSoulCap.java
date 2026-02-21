package net.yxiao233.appliedsoul.common.capabilities;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.config.Actionable;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.yxiao233.appliedsoul.common.key.SoulAEKeyType;
import net.yxiao233.appliedsoul.common.key.SoulKey;

@SuppressWarnings("UnstableApiUsage")
public class InterfaceSoulCap implements ISoulHandler{
    private final GenericInternalInventory blockEntity;

    public InterfaceSoulCap(GenericInternalInventory blockEntity) {
        this.blockEntity = blockEntity;
    }
    @Override
    public int getSoulTanks() {
        return this.blockEntity.size();
    }

    @Override
    public int getSoulInTank(int tank) {
        return this.blockEntity.getKey(tank) instanceof SoulKey ? (int) Math.min(Integer.MAX_VALUE,  this.blockEntity.getAmount(tank)) : 0 ;
    }

    @Override
    public int getTankCapacity(int tank) {
        return (int) Math.min(Integer.MAX_VALUE,  this.blockEntity.getCapacity(SoulAEKeyType.TYPE));
    }

    @Override
    public int fill(int amount, Action action) {
        return 0;
    }

    @Override
    public int drain(int maxDrain, Action action) {
        if (maxDrain <= 0) {
            return 0;
        }
        for (int i = 0; i < this.blockEntity.size(); i++) {
            var amount = this.blockEntity.extract(i, SoulKey.INSTANCE, maxDrain, Actionable.of(action == Action.EXECUTE ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE));
            if (amount > 0) {
                return (int) amount;
            }
        }
        return 0;
    }
}
