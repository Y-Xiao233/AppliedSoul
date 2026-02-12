package net.yxiao233.appliedsoul;

import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.init.client.InitScreens;
import appeng.parts.automation.StackWorldBehaviors;
import com.buuz135.soulplied_energistics.applied.SoulAEKeyType;
import com.hrznstudio.titanium.module.ModuleController;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.yxiao233.appliedsoul.client.SoulCollectorScreen;
import net.yxiao233.appliedsoul.common.me.cell.SoulCellHandler;
import net.yxiao233.appliedsoul.common.me.strategy.SoulStorageExportStrategy;
import net.yxiao233.appliedsoul.common.me.strategy.SoulStorageImportStrategy;
import net.yxiao233.appliedsoul.common.registry.*;
import net.yxiao233.appliedsoul.data.*;

@Mod(AppliedSoul.MODID)
public class AppliedSoul extends ModuleController {
    public static final String MODID = "appliedsoul";
    @SuppressWarnings("UnstableApiUsage")
    public AppliedSoul(IEventBus modEventBus, ModContainer modContainer) {
        super(modContainer);
        SoulComponents.COMPONENTS.register(modEventBus);
        SoulItems.ITEMS.register(modEventBus);
        SoulCreativeModeTab.CREATIVE_MODE_TAB.register(modEventBus);
        SoulBlocks.DR.register(modEventBus);
        SoulBlockEntities.DR.register(modEventBus);
        SoulMenus.DR.register(modEventBus);

        StorageCells.addCellHandler(SoulCellHandler.INSTANCE);

        StackWorldBehaviors.registerImportStrategy(SoulAEKeyType.TYPE, SoulStorageImportStrategy::new);
        StackWorldBehaviors.registerExportStrategy(SoulAEKeyType.TYPE, SoulStorageExportStrategy::new);

        modEventBus.addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        Upgrades.add(SoulItems.RANGE_CARD, SoulBlocks.SOUL_COLLECTOR, 2);
        AEBaseBlockEntity.registerBlockEntityItem(SoulBlocks.SOUL_COLLECTOR.block().getBlockEntityType(), SoulBlocks.SOUL_COLLECTOR.asItem());
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
        event.getGenerator().addProvider(event.includeServer(), new SoulBlockStateProvider(event.getGenerator().getPackOutput(),event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), SoulLootTablesProvider.create(event.getGenerator().getPackOutput(),event.getLookupProvider()));
        event.getGenerator().addProvider(event.includeServer(), new SoulBlockTagProvider(event.getGenerator().getPackOutput(),event.getLookupProvider(),event.getExistingFileHelper()));
    }

    @SuppressWarnings({"removal"})
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
            InitScreens.register(event,SoulMenus.SOUL_COLLECTOR.get(),SoulCollectorScreen::new,"/screens/soul_collector.json");
        }
    }
}