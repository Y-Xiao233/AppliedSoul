package net.yxiao233.appliedsoul.data;

import appeng.recipes.game.StorageCellDisassemblyRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.registry.SoulItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends VanillaRecipeProvider {
    public static final String modId = AppliedSoul.MODID;
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        super.buildRecipes(output);

        for (var cell : SoulItems.getCells()) {
            var tier = cell.asItem().getTier();
            var prefix = tier.namePrefix();
            var component = tier.componentSupplier().get();

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                    .requires(SoulItems.SOUL_CELL_HOUSING)
                    .requires(component)
                    .unlockedBy("has_soul_cell_housing", has(SoulItems.SOUL_CELL_HOUSING))
                    .unlockedBy("has_cell_component_" + prefix, has(component))
                    .save(output, cell.id());
            output.accept(
                    cell.id().withSuffix("_disassembly"),
                    new StorageCellDisassemblyRecipe(
                            cell.asItem(),
                            List.of(component.getDefaultInstance(), SoulItems.SOUL_CELL_HOUSING.stack())),
                    null);
        }
    }
}
