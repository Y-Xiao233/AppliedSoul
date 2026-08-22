package net.yxiao233.appliedsoul.common.me.logic;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.yxiao233.appliedsoul.client.screen.SoulBroadcastMenu;

public interface SoulBroadcastHost extends IUpgradeableObject {
    BlockEntity getBlockEntity();
    void saveChanges();
    SoulBroadcastLogic getSoulBroadcastLogic();
    default void openMenu(Player player, MenuHostLocator locator) {
        MenuOpener.open(SoulBroadcastMenu.TYPE, player, locator);
    }
    default IUpgradeInventory getUpgrades() {
        return getSoulBroadcastLogic().getUpgrades();
    }
}
