package net.yxiao233.appliedsoul.common.compact.jei;

import appeng.api.integrations.jei.IngredientConverters;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.key.SoulAEKeyType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class AppliedSoulJeiPlugin implements IModPlugin {
    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        IngredientConverters.register(new SoulIngredientConverter(AppSoulTypes.SOUL_TYPE));
    }

    @Override
    @SuppressWarnings("removal")
    public void registerIngredients(@NotNull IModIngredientRegistration registration) {
        registration.register( AppSoulTypes.SOUL_TYPE, List.of(SoulAEKeyType.TYPE),new AppSoulTypes.SoulStackHelper(),new AppSoulTypes.SoulStackRenderer());
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(AppliedSoul.MODID,"jei");
    }
}
