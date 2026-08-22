package net.yxiao233.appliedsoul.client.screen;

import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.me.logic.SoulBroadcastHost;

public class SoulBroadcastMenu extends UpgradeableMenu<SoulBroadcastHost> {
    public static final MenuType<SoulBroadcastMenu> TYPE = MenuTypeBuilder.create(SoulBroadcastMenu::new, SoulBroadcastHost.class).buildUnregistered(AppliedSoul.makeId("soul_broadcast"));
    public SoulBroadcastMenu(MenuType<?> menuType, int id, Inventory ip, SoulBroadcastHost host) {
        super(menuType, id, ip, host);
    }
}
