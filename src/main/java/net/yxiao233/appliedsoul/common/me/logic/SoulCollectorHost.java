package net.yxiao233.appliedsoul.common.me.logic;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.yxiao233.appliedsoul.client.screen.SoulCollectorMenu;

public interface SoulCollectorHost extends IUpgradeableObject {
    BlockEntity getBlockEntity();
    void saveChanges();
    SoulCollectorLogic getSoulCollectorLogic();
    default void openMenu(Player player, MenuHostLocator locator) {
        MenuOpener.open(SoulCollectorMenu.TYPE, player, locator);
    }

    default IUpgradeInventory getUpgrades() {
        return getSoulCollectorLogic().getUpgrades();
    }
}
