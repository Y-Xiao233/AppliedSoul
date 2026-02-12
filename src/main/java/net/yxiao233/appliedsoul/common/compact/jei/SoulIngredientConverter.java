package net.yxiao233.appliedsoul.common.compact.jei;

import appeng.api.stacks.GenericStack;
import com.buuz135.soulplied_energistics.applied.SoulAEKeyType;
import com.buuz135.soulplied_energistics.applied.SoulKey;
import mezz.jei.api.ingredients.IIngredientType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tamaized.ae2jeiintegration.api.integrations.jei.IngredientConverter;

public record SoulIngredientConverter(IIngredientType<SoulAEKeyType> type) implements IngredientConverter<SoulAEKeyType> {

    @Override
    public @NotNull IIngredientType<SoulAEKeyType> getIngredientType() {
        return type;
    }

    @Override
    public @Nullable SoulAEKeyType getIngredientFromStack(@NotNull GenericStack stack) {
        if(stack.what() instanceof SoulKey soulKey && type.getIngredientClass().isInstance(soulKey.getType())){
            return (SoulAEKeyType) soulKey.getType();
        }
        return null;
    }

    @Override
    public @NotNull GenericStack getStackFromIngredient(@NotNull SoulAEKeyType soulAEKeyType) {
        return new GenericStack(SoulKey.INSTANCE,1);
    }
}
