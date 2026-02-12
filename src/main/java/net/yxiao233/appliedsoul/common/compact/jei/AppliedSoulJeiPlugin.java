package net.yxiao233.appliedsoul.common.compact.jei;

import com.buuz135.soulplied_energistics.applied.SoulAEKeyType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.yxiao233.appliedsoul.AppliedSoul;
import org.jetbrains.annotations.NotNull;
import tamaized.ae2jeiintegration.api.integrations.jei.IngredientConverters;

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
        return ResourceLocation.fromNamespaceAndPath(AppliedSoul.MODID,"jei");
    }
}
