package net.yxiao233.appliedsoul.data;

import appeng.core.definitions.ItemDefinition;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.registry.SoulItems;
import org.jetbrains.annotations.NotNull;

public class ModItemModelProvider extends ItemModelProvider{
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AppliedSoul.MODID, existingFileHelper);
    }

    @Override
    public @NotNull String getName() {
        return AppliedSoul.MODID + " - ItemModel";
    }

    @Override
    protected void registerModels() {
        flatSingleLayer(SoulItems.SOUL_CELL_HOUSING);
        SoulItems.getCells().forEach(cell -> flatSingleLayer(cell).texture("layer1", AppliedSoul.makeId("item/storage_cell_led")));
    }
    private ItemModelBuilder flatSingleLayer(ItemDefinition<?> item) {
        var path = item.id().getPath();
        return singleTexture(path, mcLoc("item/generated"), "layer0", AppliedSoul.makeId("item/" + path));
    }
}
