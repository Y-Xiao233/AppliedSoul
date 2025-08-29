package net.yxiao233.appliedsoul.common.item;

import appeng.api.storage.StorageCells;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.localization.PlayerMessages;
import appeng.items.AEBaseItem;
import appeng.items.storage.StorageTier;
import appeng.recipes.game.StorageCellDisassemblyRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.yxiao233.appliedsoul.common.me.cell.SoulCellHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

public class SoulCellItem extends AEBaseItem {
    private final StorageTier tier;
    public SoulCellItem(Properties properties, StorageTier tier) {
        super(properties);
        this.tier = tier;
    }
    public StorageTier getTier() {
        return tier;
    }

    public long getTotalBytes() {
        return 100 * (long) Math.pow(4, tier.index() - 1);
    }

    public double getIdleDrain() {
        return tier.idleDrain();
    }

    public IUpgradeInventory getUpgrades(ItemStack stack) {
        return UpgradeInventories.forItem(stack, 1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        disassemble(player.getItemInHand(usedHand),level,player);
        return super.use(level, player, usedHand);
    }
    @NotNull
    @Override
    public InteractionResult onItemUseFirst(@NotNull ItemStack stack, UseOnContext context) {
        return disassemble(stack, context.getLevel(), context.getPlayer())
                ? InteractionResult.sidedSuccess(context.getLevel().isClientSide())
                : InteractionResult.PASS;
    }

    private boolean disassemble(ItemStack stack, Level level, Player player) {
        if (player != null && player.isShiftKeyDown()) {
            if (level.isClientSide()) {
                return false;
            }

            var disassembledStacks = StorageCellDisassemblyRecipe.getDisassemblyResult(level, stack.getItem());

            if (disassembledStacks.isEmpty()) {
                return false;
            }

            var playerInv = player.getInventory();
            var cellInv = StorageCells.getCellInventory(stack, null);

            if (cellInv != null && playerInv.getSelected() == stack) {
                if (cellInv.getAvailableStacks().isEmpty()) {
                    playerInv.setItem(playerInv.selected, ItemStack.EMPTY);

                    for (var upgrade : getUpgrades(stack)) {
                        playerInv.placeItemBackInInventory(upgrade);
                    }

                    for (var disassembled : disassembledStacks) {
                        playerInv.placeItemBackInInventory(disassembled);
                    }

                    return true;
                } else {
                    player.displayClientMessage(PlayerMessages.OnlyEmptyCellsCanBeDisassembled.text(), true);
                }
            }
        }

        return false;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void appendHoverText(
            ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag advTooltips) {
        SoulCellHandler.INSTANCE.addCellInformationToTooltip(stack, lines);
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return SoulCellHandler.INSTANCE.getTooltipImage(stack);
    }
}
