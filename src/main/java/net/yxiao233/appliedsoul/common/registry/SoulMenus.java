package net.yxiao233.appliedsoul.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.client.SoulCollectorMenu;

public class SoulMenus {
    public static final DeferredRegister<MenuType<?>> DR = DeferredRegister.create(Registries.MENU, AppliedSoul.MODID);
    public static final RegistryObject<MenuType<SoulCollectorMenu>> SOUL_COLLECTOR = DR.register("soul_collector",() -> SoulCollectorMenu.TYPE);
}
