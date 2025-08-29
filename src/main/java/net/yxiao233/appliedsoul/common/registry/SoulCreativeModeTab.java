package net.yxiao233.appliedsoul.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yxiao233.appliedsoul.AppliedSoul;

public class SoulCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AppliedSoul.MODID);
    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> SOUL_TAB = CREATIVE_MODE_TAB.register("mic_tab", () -> CreativeModeTab.builder()
            .icon(() -> SoulItems.SOUL_CELL_256K.get().getDefaultInstance())
            .displayItems((parameters, output) -> {

                SoulItems.ITEMS.getEntries().forEach((reg) ->{
                    output.accept(reg.get());
                });

            })
            .title(Component.translatable("itemGroup.appliedsoul"))
            .build()
    );
}
