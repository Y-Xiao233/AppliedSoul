package net.yxiao233.appliedsoul.data;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.recipes.game.StorageCellDisassemblyRecipe;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapelessRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.yxiao233.appliedsoul.common.registry.SoulBlocks;
import net.yxiao233.appliedsoul.common.registry.SoulItems;
import net.yxiao233.appliedsoul.common.registry.SoulTags;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends VanillaRecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        for (var cell : SoulItems.getCells()) {
            var tier = cell.asItem().getTier();
            var prefix = tier.namePrefix();
            var component = tier.componentSupplier().get();

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                    .requires(SoulItems.SOUL_CELL_HOUSING)
                    .requires(component)
                    .unlockedBy("has_soul_cell_housing", has(SoulItems.SOUL_CELL_HOUSING))
                    .unlockedBy("has_cell_component_" + prefix, has(component))
                    .save(output, cell.id() + "_storage");
            output.accept(
                    cell.id().withSuffix("_disassembly"),
                    new StorageCellDisassemblyRecipe(
                            cell.asItem(),
                            List.of(component.getDefaultInstance(), SoulItems.SOUL_CELL_HOUSING.stack())),
                    null);
        }

        for (var cell : SoulItems.getCells()){
            var tier = cell.asItem().getTier();
            var component = tier.componentSupplier().get();
            TitaniumShapedRecipeBuilder.shapedRecipe(cell)
                    .pattern("ABA")
                    .pattern("BEB")
                    .pattern("CDC")
                    .define('A', AEBlocks.QUARTZ_GLASS.asItem())
                    .define('B', Items.ECHO_SHARD)
                    .define('C', IndustrialTags.Items.PLASTIC)
                    .define('D', ModuleCore.PINK_SLIME_INGOT.get())
                    .define('E', component)
                    .save(output, cell.id());
        }

        TitaniumShapedRecipeBuilder.shapedRecipe(SoulItems.SOUL_CELL_HOUSING)
                .pattern("ABA")
                .pattern("B B")
                .pattern("CDC")
                .define('A', AEBlocks.QUARTZ_GLASS.asItem())
                .define('B', Items.ECHO_SHARD)
                .define('C', IndustrialTags.Items.PLASTIC)
                .define('D',ModuleCore.PINK_SLIME_INGOT.get())
                .save(output,SoulItems.SOUL_CELL_HOUSING.id());


        TitaniumShapelessRecipeBuilder.shapelessRecipe(SoulItems.RANGE_CARD)
                .requires(AEItems.ADVANCED_CARD)
                .requires(SoulTags.Items.ENDER_PEARL_DUSTS)
                .save(output);


        InscriberRecipeBuilder.inscribe(Items.NETHER_STAR,SoulItems.ENDER_STAR,1)
                .setBottom(Ingredient.of(Tags.Items.ENDER_PEARLS))
                .setTop(Ingredient.of(AEItems.SINGULARITY))
                .setMode(InscriberProcessType.PRESS)
                .save(output,SoulItems.ENDER_STAR.id());

        TitaniumShapedRecipeBuilder.shapedRecipe(SoulBlocks.SOUL_COLLECTOR)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', SoulItems.ENDER_STAR)
                .define('B', Items.ECHO_SHARD)
                .define('C', AEBlocks.INTERFACE)
                .save(output);
    }
}
