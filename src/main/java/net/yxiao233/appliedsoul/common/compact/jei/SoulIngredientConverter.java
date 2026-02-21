package net.yxiao233.appliedsoul.common.compact.jei;

import appeng.api.integrations.jei.IngredientConverter;
import appeng.api.stacks.GenericStack;
import mezz.jei.api.ingredients.IIngredientType;
import net.yxiao233.appliedsoul.common.key.SoulAEKeyType;
import net.yxiao233.appliedsoul.common.key.SoulKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
