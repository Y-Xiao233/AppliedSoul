package net.yxiao233.appliedsoul;

import appeng.api.storage.StorageCells;
import appeng.parts.automation.StackWorldBehaviors;
import com.buuz135.soulplied_energistics.applied.SoulAEKeyType;
import com.hrznstudio.titanium.module.ModuleController;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.yxiao233.appliedsoul.common.me.cell.SoulCellHandler;
import net.yxiao233.appliedsoul.common.me.strategy.SoulStorageExportStrategy;
import net.yxiao233.appliedsoul.common.me.strategy.SoulStorageImportStrategy;
import net.yxiao233.appliedsoul.common.registry.SoulComponents;
import net.yxiao233.appliedsoul.common.registry.SoulCreativeModeTab;
import net.yxiao233.appliedsoul.common.registry.SoulItems;
import net.yxiao233.appliedsoul.data.ModItemModelProvider;
import net.yxiao233.appliedsoul.data.ModRecipeProvider;

@Mod(AppliedSoul.MODID)
public class AppliedSoul extends ModuleController {
    public static final String MODID = "appliedsoul";
    @SuppressWarnings("UnstableApiUsage")
    public AppliedSoul(IEventBus modEventBus, ModContainer modContainer) {
        super(modContainer);
        SoulComponents.COMPONENTS.register(modEventBus);
        SoulItems.ITEMS.register(modEventBus);
        SoulCreativeModeTab.CREATIVE_MODE_TAB.register(modEventBus);

        StorageCells.addCellHandler(SoulCellHandler.INSTANCE);

        StackWorldBehaviors.registerImportStrategy(SoulAEKeyType.TYPE, SoulStorageImportStrategy::new);
        StackWorldBehaviors.registerExportStrategy(SoulAEKeyType.TYPE, SoulStorageExportStrategy::new);
    }

    public static ResourceLocation makeId(String id){
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

    @Override
    protected void initModules() {
    }

    @Override
    public void addDataProvider(GatherDataEvent event) {
        super.addDataProvider(event);
        event.getGenerator().addProvider(event.includeServer(), new ModRecipeProvider(event.getGenerator().getPackOutput(),event.getLookupProvider()));
        event.getGenerator().addProvider(event.includeServer(), new ModItemModelProvider(event.getGenerator().getPackOutput(),event.getExistingFileHelper()));
    }
}