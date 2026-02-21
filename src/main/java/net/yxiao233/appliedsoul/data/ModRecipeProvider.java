package net.yxiao233.appliedsoul.data;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapelessRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.yxiao233.appliedsoul.common.registry.SoulBlocks;
import net.yxiao233.appliedsoul.common.registry.SoulItems;
import net.yxiao233.appliedsoul.common.registry.SoulTags;

import java.util.function.Consumer;

public class ModRecipeProvider extends VanillaRecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        for (var cell : SoulItems.getCells()) {
            var tier = cell.asItem().getTier();
            var prefix = tier.namePrefix();
            var component = tier.componentSupplier().get();

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell)
                    .requires(SoulItems.SOUL_CELL_HOUSING)
                    .requires(component)
                    .unlockedBy("has_soul_cell_housing", has(SoulItems.SOUL_CELL_HOUSING))
                    .unlockedBy("has_cell_component_" + prefix, has(component))
                    .save(consumer, cell.id() + "_storage");
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
                    .save(consumer, cell.id());
        }

        TitaniumShapedRecipeBuilder.shapedRecipe(SoulItems.SOUL_CELL_HOUSING)
                .pattern("ABA")
                .pattern("B B")
                .pattern("CDC")
                .define('A', AEBlocks.QUARTZ_GLASS.asItem())
                .define('B', Items.ECHO_SHARD)
                .define('C', IndustrialTags.Items.PLASTIC)
                .define('D',ModuleCore.PINK_SLIME_INGOT.get())
                .save(consumer,SoulItems.SOUL_CELL_HOUSING.id());


        TitaniumShapelessRecipeBuilder.shapelessRecipe(SoulItems.RANGE_CARD)
                .requires(AEItems.ADVANCED_CARD)
                .requires(SoulTags.Items.ENDER_PEARL_DUSTS)
                .save(consumer);


        InscriberRecipeBuilder.inscribe(Items.NETHER_STAR,SoulItems.ENDER_STAR,1)
                .setBottom(Ingredient.of(Tags.Items.ENDER_PEARLS))
                .setTop(Ingredient.of(AEItems.SINGULARITY))
                .setMode(InscriberProcessType.PRESS)
                .save(consumer,SoulItems.ENDER_STAR.id());

        TitaniumShapedRecipeBuilder.shapedRecipe(SoulBlocks.SOUL_COLLECTOR)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', SoulItems.ENDER_STAR)
                .define('B', Items.ECHO_SHARD)
                .define('C', AEBlocks.INTERFACE)
                .save(consumer);
    }
}
