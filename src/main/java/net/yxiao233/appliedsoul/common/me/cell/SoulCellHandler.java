package net.yxiao233.appliedsoul.common.me.cell;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.AEConfig;
import appeng.core.localization.Tooltips;
import appeng.items.storage.StorageCellTooltipComponent;
import com.buuz135.soulplied_energistics.applied.SoulKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.yxiao233.appliedsoul.common.item.SoulCellItem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SoulCellHandler implements ICellHandler {
    public static final SoulCellHandler INSTANCE = new SoulCellHandler();

    private SoulCellHandler() {}
    @Override
    public boolean isCell(ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() instanceof SoulCellItem;
    }

    @Override
    public @Nullable SoulCellInventory getCellInventory(ItemStack itemStack, @Nullable ISaveProvider iSaveProvider) {
        return isCell(itemStack) ? new SoulCellInventory((SoulCellItem) itemStack.getItem(), itemStack, iSaveProvider) : null;
    }

    public void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        var handler = getCellInventory(is, null);

        if (handler != null) {
            lines.add(Tooltips.bytesUsed(handler.getUsedBytes(), handler.getTotalBytes()));
        }
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack is) {
        var handler = getCellInventory(is, null);
        if (handler == null) return Optional.empty();

        List<GenericStack> list = new ArrayList<>();
        var upgrades = new ArrayList<ItemStack>();
        boolean showAmounts = false;
        long amount = handler.getSoulAmount();
        if (AEConfig.instance().isTooltipShowCellUpgrades() && amount > 0) {
            showAmounts = true;
            handler.getUpgrades().forEach(upgrades::add);
            list.add(new GenericStack(SoulKey.INSTANCE,handler.getSoulAmount()));
        }else{
            list = Collections.emptyList();
        }

        return Optional.of(new StorageCellTooltipComponent(upgrades, list, false, showAmounts));
    }
}
