package net.yxiao233.appliedsoul.common.me.logic;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.MEStorage;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import com.buuz135.industrialforegoingsouls.config.ConfigSoulSurge;
import com.buuz135.soulplied_energistics.applied.SoulKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.yxiao233.appliedsoul.common.registry.SoulItems;
import net.yxiao233.appliedsoul.util.CompoundTagHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class SoulBroadcastLogic {
    protected final SoulBroadcastHost host;
    protected final IManagedGridNode mainNode;
    private @Nullable MEStorage networkStorage;
    private final IUpgradeInventory upgrades;
    private ArrayList<BlockPos> connectMachines = new ArrayList<>();
    private static ArrayList<BlockPos> allConnectMachines = new ArrayList<>();
    private int tickingTime;
    public SoulBroadcastLogic(IManagedGridNode gridNode, SoulBroadcastHost host, Item is) {
        this.host = host;
        this.mainNode = gridNode.setFlags(new GridFlags[]{GridFlags.REQUIRE_CHANNEL}).addService(IGridTickable.class, new SoulBroadcastLogic.Ticker());
        this.upgrades = UpgradeInventories.forMachine(is, 2, this::onUpgradesChanged);
    }

    private void onUpgradesChanged() {
        this.host.saveChanges();
    }

    public void writeToNBT(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTagHelper.writePosListToTag(connectMachines,tag,"connect_machines");
        CompoundTagHelper.writePosListToTag(allConnectMachines,tag,"all_connect_machiens");
        this.upgrades.writeToNBT(tag, "upgrades", registries);
        tag.putInt("tickingTime",tickingTime);
    }

    public void readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {
        this.connectMachines = CompoundTagHelper.readPosListFromTag(tag,"connect_machines");
        allConnectMachines = CompoundTagHelper.readPosListFromTag(tag,"all_connect_machiens");
        this.upgrades.readFromNBT(tag, "upgrades", registries);
        this.tickingTime = tag.getInt("tickingTime");
    }

    public void addDrops(List<ItemStack> drops) {
        for (ItemStack is : this.upgrades) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }


    public void clearContent() {
        this.upgrades.clear();
    }
    public void gridChanged() {
        this.networkStorage = Objects.requireNonNull(this.mainNode.getGrid()).getStorageService().getInventory();
        this.notifyNeighbors();
    }

    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    public ArrayList<BlockPos> getConnectMachines(){
        return this.connectMachines;
    }

    public static ArrayList<BlockPos> getAllConnectMachines(){
        return allConnectMachines;
    }

    public static boolean contains(ArrayList<BlockPos> posList, BlockPos pos){
        return indexOf(posList,pos) != -1;
    }
    public static int indexOf(ArrayList<BlockPos> posList, BlockPos pos){
        for (int i = 0; i < posList.size(); i++) {
            if (posList.get(i).equals(pos)) {
                return i;
            }
        }
        return -1;
    }

    public MEStorage getInventory() {
        return this.networkStorage;
    }

    public void notifyNeighbors() {
        if (this.mainNode.isActive()) {
            this.mainNode.ifPresent((grid, node) -> {
                grid.getTickManager().wakeDevice(node);
            });
        }

        this.host.getBlockEntity().invalidateCapabilities();
    }

    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    public boolean tick(){
        IGrid grid = this.mainNode.getGrid();
        if(grid == null){
            return false;
        }else{
            MEStorage networkInv = grid.getStorageService().getInventory();
            Level level = host.getBlockEntity().getLevel();
            if(level == null){
                return false;
            }
            if (this.tickingTime <= 0) {
                boolean canExtract = networkInv.extract(SoulKey.INSTANCE, 10, Actionable.SIMULATE, IActionSource.empty()) > 0;
                if (canExtract) {
                    networkInv.extract(SoulKey.INSTANCE, 10, Actionable.MODULATE, IActionSource.empty());
                    this.tickingTime = ConfigSoulSurge.SOUL_TIME * 10;
                }else{
                    return false;
                }
            }
            updateConnectMachines(level);
            applyAccelerate(level);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void applyAccelerate(Level level){
        this.connectMachines.forEach(pos -> {
            BlockState blockState = level.getBlockState(pos);
            if(!blockState.is(com.buuz135.industrialforegoingsouls.tag.SoulTags.Blocks.CANT_ACCELERATE) && !blockState.is(com.buuz135.industrialforegoingsouls.tag.SoulTags.Blocks.FORGE_CANT_ACCELERATE)){
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if(blockEntity != null){
                    BlockEntityTicker<BlockEntity> ticker = blockState.getTicker(level,(BlockEntityType<BlockEntity>) blockEntity.getType());
                    if (ticker != null) {
                        for(int i = 0; i < ConfigSoulSurge.ACCELERATION_TICK * 8; ++i) {
                            ticker.tick(level, pos , blockState, blockEntity);
                        }

                        --this.tickingTime;
                    }
                }
            }
        });
    }

    private void updateConnectMachines(Level level){
        int radius = (this.getUpgrades().getInstalledUpgrades(SoulItems.RANGE_CARD) * 16) + 8;
        BlockPos worldPosition = this.host.getBlockEntity().getBlockPos();
        HashSet<BlockPos> set1 = new HashSet<>(allConnectMachines);
        set1.forEach(pos -> {
            if(level.getBlockEntity(pos) == null || Math.abs(worldPosition.getX() - pos.getX()) > radius || Math.abs(worldPosition.getZ() - pos.getZ()) > radius){
                allConnectMachines.remove(pos);
            }
        });

        HashSet<BlockPos> set2 = new HashSet<>(connectMachines);
        set2.forEach(pos -> {
            if(level.getBlockEntity(pos) == null || Math.abs(worldPosition.getX() - pos.getX()) > radius || Math.abs(worldPosition.getZ() - pos.getZ()) > radius){
                connectMachines.remove(pos);
            }
        });
    }


    private class Ticker implements IGridTickable {
        private Ticker() {
        }

        public TickingRequest getTickingRequest(IGridNode node) {
            return new TickingRequest(5,120,false);
        }

        public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
            if (!SoulBroadcastLogic.this.mainNode.isActive()) {
                return TickRateModulation.SLEEP;
            } else {
                return SoulBroadcastLogic.this.tick() ? TickRateModulation.URGENT : TickRateModulation.SLEEP;
            }
        }
    }
}
