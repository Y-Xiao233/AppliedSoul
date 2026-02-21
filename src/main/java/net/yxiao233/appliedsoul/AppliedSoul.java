package net.yxiao233.appliedsoul;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.client.AEKeyRendering;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.init.client.InitScreens;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import appeng.parts.automation.StackWorldBehaviors;
import com.hrznstudio.titanium.module.ModuleController;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.yxiao233.appliedsoul.client.SoulCollectorMenu;
import net.yxiao233.appliedsoul.client.SoulCollectorScreen;
import net.yxiao233.appliedsoul.client.SoulKeyRenderHandler;
import net.yxiao233.appliedsoul.common.capabilities.ISoulHandler;
import net.yxiao233.appliedsoul.common.key.SoulAEKeyType;
import net.yxiao233.appliedsoul.common.key.SoulKey;
import net.yxiao233.appliedsoul.common.me.cell.SoulCellHandler;
import net.yxiao233.appliedsoul.common.me.strategy.SoulContainerStrategy;
import net.yxiao233.appliedsoul.common.me.strategy.SoulExternalStorageStrategy;
import net.yxiao233.appliedsoul.common.me.strategy.SoulStorageExportStrategy;
import net.yxiao233.appliedsoul.common.me.strategy.SoulStorageImportStrategy;
import net.yxiao233.appliedsoul.common.registry.*;
import net.yxiao233.appliedsoul.data.*;

@Mod(AppliedSoul.MODID)
public class AppliedSoul extends ModuleController {
    public static final String MODID = "appliedsoul";
    @SuppressWarnings("UnstableApiUsage")
    public AppliedSoul() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SoulCreativeModeTab.CREATIVE_MODE_TAB.register(modEventBus);
        SoulMenus.DR.register(modEventBus);
        modEventBus.addListener(SoulItems::register);
        modEventBus.addListener(SoulBlocks::register);
        modEventBus.addListener(SoulAEKeyType::register);

        StorageCells.addCellHandler(SoulCellHandler.INSTANCE);

        StackWorldBehaviors.registerImportStrategy(SoulAEKeyType.TYPE, SoulStorageImportStrategy::new);
        StackWorldBehaviors.registerExportStrategy(SoulAEKeyType.TYPE, SoulStorageExportStrategy::new);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onRegisterCapabilities);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void commonSetup(FMLCommonSetupEvent event) {
        GenericSlotCapacities.register(SoulAEKeyType.TYPE,16L);
        StackWorldBehaviors.registerExternalStorageStrategy(SoulAEKeyType.TYPE, SoulExternalStorageStrategy::new);
        ContainerItemStrategy.register(SoulAEKeyType.TYPE, SoulKey.class, new SoulContainerStrategy());
        Upgrades.add(SoulItems.RANGE_CARD, SoulBlocks.SOUL_COLLECTOR, 2);
        AEBaseBlockEntity.registerBlockEntityItem(SoulBlocks.SOUL_COLLECTOR_ENTITY, SoulBlocks.SOUL_COLLECTOR.asItem());
    }

    public static ResourceLocation makeId(String id){
        return new ResourceLocation(MODID, id);
    }

    @Override
    protected void initModules() {
    }

    @Override
    public void addDataProvider(GatherDataEvent event) {
        super.addDataProvider(event);
        event.getGenerator().addProvider(event.includeServer(), new ModRecipeProvider(event.getGenerator().getPackOutput()));
        event.getGenerator().addProvider(event.includeServer(), new ModItemModelProvider(event.getGenerator().getPackOutput(),event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new SoulBlockStateProvider(event.getGenerator().getPackOutput(),event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), SoulLootTablesProvider.create(event.getGenerator().getPackOutput(),event.getLookupProvider()));
        event.getGenerator().addProvider(event.includeServer(), new SoulBlockTagProvider(event.getGenerator().getPackOutput(),event.getLookupProvider(),event.getExistingFileHelper()));
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            InitScreens.register(SoulMenus.SOUL_COLLECTOR.get(), SoulCollectorScreen::new,"/screens/soul_collector.json");
            AEKeyRendering.register(SoulAEKeyType.TYPE, SoulKey.class,new SoulKeyRenderHandler());
        }
    }

    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ISoulHandler.class);
    }
}