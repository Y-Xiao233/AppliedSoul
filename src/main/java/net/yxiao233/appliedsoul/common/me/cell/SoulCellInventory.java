package net.yxiao233.appliedsoul.common.me.cell;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.definitions.AEItems;
import com.buuz135.soulplied_energistics.applied.SoulAEKeyType;
import com.buuz135.soulplied_energistics.applied.SoulKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.yxiao233.appliedsoul.common.item.SoulCellItem;
import net.yxiao233.appliedsoul.common.registry.SoulComponents;

import java.util.Objects;

public class SoulCellInventory implements StorageCell {
    private final SoulCellItem cell;
    private final ItemStack stack;
    private final ISaveProvider container;
    private long soulAmount;
    private boolean isPersisted = true;

    public SoulCellInventory(SoulCellItem cell, ItemStack stack, ISaveProvider container) {
        this.cell = cell;
        this.stack = stack;
        this.container = container;

        soulAmount = stack.getOrDefault(SoulComponents.SOUL_CELL_AMOUNT, 0L);
    }
    public long getTotalBytes() {
        return cell.getTotalBytes();
    }

    public long getUsedBytes() {
        var amountPerByte = SoulAEKeyType.TYPE.getAmountPerByte();
        return (soulAmount + amountPerByte - 1) / amountPerByte;
    }

    public long getMaxSoul() {
        return cell.getTotalBytes() * SoulAEKeyType.TYPE.getAmountPerByte();
    }

    public long getSoulAmount(){
        return this.soulAmount;
    }
    @Override
    public CellState getStatus() {
        if (soulAmount == 0) {
            return CellState.EMPTY;
        }

        if (soulAmount == getMaxSoul()) {
            return CellState.FULL;
        }

        if (soulAmount > getMaxSoul() / 2) {
            return CellState.TYPES_FULL;
        }

        return CellState.NOT_EMPTY;
    }

    @Override
    public double getIdleDrain() {
        return cell.getIdleDrain();
    }

    public IUpgradeInventory getUpgrades() {
        return cell.getUpgrades(stack);
    }
    private void saveChanges() {
        isPersisted = false;

        if (container != null) {
            container.saveChanges();
        } else {
            persist();
        }
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount == 0 || !(what instanceof SoulKey)) {
            return 0;
        }

        var inserted = Math.min(amount, Math.max(0, getMaxSoul() - soulAmount));

        if (mode == Actionable.MODULATE) {
            soulAmount += inserted;
            saveChanges();
        }

        return getUpgrades().isInstalled(AEItems.VOID_CARD) ? amount : inserted;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        var extractAmount = Math.min(Integer.MAX_VALUE, amount);
        var currentAmount = soulAmount;

        if (soulAmount > 0 && Objects.equals(SoulKey.INSTANCE, what)) {
            if (mode == Actionable.MODULATE) {
                soulAmount = Math.max(0, soulAmount - extractAmount);
                saveChanges();
            }

            return Math.min(extractAmount, currentAmount);
        }

        return 0;
    }
    @Override
    public void persist() {
        if (isPersisted) {
            return;
        }

        if (soulAmount < 0) {
            stack.remove(SoulComponents.SOUL_CELL_AMOUNT);
        } else {
            stack.set(SoulComponents.SOUL_CELL_AMOUNT, soulAmount);
        }

        isPersisted = true;
    }

    @Override
    public Component getDescription() {
        return stack.getHoverName();
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (soulAmount > 0) {
            out.add(SoulKey.INSTANCE, soulAmount);
        }
    }
}
