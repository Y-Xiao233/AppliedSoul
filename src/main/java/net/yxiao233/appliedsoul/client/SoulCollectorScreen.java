package net.yxiao233.appliedsoul.client;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SoulCollectorScreen extends UpgradeableScreen<SoulCollectorMenu> {
    public SoulCollectorScreen(SoulCollectorMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
