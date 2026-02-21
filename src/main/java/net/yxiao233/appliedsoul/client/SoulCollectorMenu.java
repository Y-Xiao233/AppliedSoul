package net.yxiao233.appliedsoul.client;

import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.me.logic.SoulCollectorHost;

public class SoulCollectorMenu extends UpgradeableMenu<SoulCollectorHost> {
    public static final MenuType<SoulCollectorMenu> TYPE = MenuTypeBuilder.create(SoulCollectorMenu::new, SoulCollectorHost.class).build("soul_collector");
    public SoulCollectorMenu(MenuType<?> menuType, int id, Inventory ip, SoulCollectorHost host) {
        super(menuType, id, ip, host);
    }
}
