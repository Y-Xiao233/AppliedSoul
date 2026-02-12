package net.yxiao233.appliedsoul.common.compact.jei;

import com.buuz135.soulplied_energistics.applied.SoulAEKeyType;
import com.buuz135.soulplied_energistics.applied.SoulKey;
import com.buuz135.soulplied_energistics.client.SoulKeyRenderHandler;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AppSoulTypes {
    public static final IIngredientType<SoulAEKeyType> SOUL_TYPE = () -> SoulAEKeyType.class;
    public static class SoulStackHelper implements IIngredientHelper<SoulAEKeyType> {

        @Override
        public @NotNull IIngredientType<SoulAEKeyType> getIngredientType() {
            return SOUL_TYPE;
        }

        @Override
        public @NotNull String getDisplayName(@NotNull SoulAEKeyType soulAEKeyType) {
            return "soul";
        }

        @Override
        @SuppressWarnings("removal")
        public @NotNull String getUniqueId(@NotNull SoulAEKeyType soulAEKeyType, @NotNull UidContext uidContext) {
            return soulAEKeyType.getId().getNamespace() + ":" + "soul";
        }

        @Override
        public @NotNull ResourceLocation getResourceLocation(@NotNull SoulAEKeyType soulAEKeyType) {
            return ResourceLocation.fromNamespaceAndPath(soulAEKeyType.getId().getNamespace(),"soul");
        }

        @Override
        public @NotNull SoulAEKeyType copyIngredient(@NotNull SoulAEKeyType soulAEKeyType) {
            return soulAEKeyType;
        }

        @Override
        public @NotNull String getErrorInfo(@Nullable SoulAEKeyType energyType) {
            return null;
        }
    }

    public static class SoulStackRenderer implements IIngredientRenderer<SoulAEKeyType> {
        private final SoulKeyRenderHandler handler = new SoulKeyRenderHandler();
        @Override
        public void render(@NotNull GuiGraphics guiGraphics, @NotNull SoulAEKeyType type) {
            handler.drawInGui(Minecraft.getInstance(),guiGraphics,0,0, SoulKey.INSTANCE);
        }

        @Override
        public @NotNull List<Component> getTooltip(@NotNull SoulAEKeyType soulAEKeyType, @NotNull TooltipFlag tooltipFlag) {
            List<Component> tips = new ArrayList<>();
            tips.add(Component.translatable("aekey.soulkey.description"));
            return tips;
        }
    }
}
