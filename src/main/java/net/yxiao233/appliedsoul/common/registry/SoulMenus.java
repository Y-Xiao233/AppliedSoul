package net.yxiao233.appliedsoul.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.client.screen.SoulBroadcastMenu;
import net.yxiao233.appliedsoul.client.screen.SoulCollectorMenu;

import java.util.function.Supplier;

public class SoulMenus {
    public static final DeferredRegister<MenuType<?>> DR = DeferredRegister.create(Registries.MENU, AppliedSoul.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<SoulCollectorMenu>> SOUL_COLLECTOR = register(SoulIds.SOUL_COLLECTOR, () -> SoulCollectorMenu.TYPE);
    public static final DeferredHolder<MenuType<?>, MenuType<SoulBroadcastMenu>> SOUL_BROADCAST = register(SoulIds.SOUL_BROADCAST, () -> SoulBroadcastMenu.TYPE);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(EntryIds entry, Supplier<MenuType<T>> sup){
        return DR.register(entry.getShortId(),sup);
    }
}
