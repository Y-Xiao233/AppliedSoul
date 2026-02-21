package net.yxiao233.appliedsoul.common.item;

import appeng.api.storage.StorageCells;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.localization.PlayerMessages;
import appeng.items.AEBaseItem;
import appeng.items.storage.StorageTier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.yxiao233.appliedsoul.common.me.cell.SoulCellHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

public class SoulCellItem extends AEBaseItem {
    private final StorageTier tier;
    private final ItemLike housing;
    public SoulCellItem(Properties properties, StorageTier tier, ItemLike housing) {
        super(properties);
        this.tier = tier;
        this.housing = housing;
    }
    public StorageTier getTier() {
        return tier;
    }

    public long getTotalBytes() {
        return 10240 * (long) Math.pow(4, tier.index() - 1);
    }

    public double getIdleDrain() {
        return tier.idleDrain();
    }

    public IUpgradeInventory getUpgrades(ItemStack stack) {
        return UpgradeInventories.forItem(stack, 1);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        disassemble(player.getItemInHand(usedHand),level,player);
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()),player.getItemInHand(usedHand));
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
            if (level.isClientSide()) return false;

            var playerInv = player.getInventory();
            var cellInv = StorageCells.getCellInventory(stack, null);

            if (cellInv != null && playerInv.getSelected() == stack) {
                if (cellInv.getAvailableStacks().isEmpty()) {
                    playerInv.setItem(playerInv.selected, ItemStack.EMPTY);
                    playerInv.placeItemBackInInventory(
                            tier.componentSupplier().get().getDefaultInstance());

                    for (var upgrade : getUpgrades(stack)) {
                        playerInv.placeItemBackInInventory(upgrade);
                    }

                    playerInv.placeItemBackInInventory(housing.asItem().getDefaultInstance());

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
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> components, TooltipFlag isAdvanced) {
        SoulCellHandler.INSTANCE.addCellInformationToTooltip(stack, components);
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return SoulCellHandler.INSTANCE.getTooltipImage(stack);
    }
}
