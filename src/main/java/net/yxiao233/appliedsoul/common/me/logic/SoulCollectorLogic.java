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
import com.buuz135.industrialforegoingsouls.IndustrialForegoingSouls;
import com.buuz135.industrialforegoingsouls.block.tile.SoulLaserBaseBlockEntity;
import com.buuz135.industrialforegoingsouls.capabilities.ISoulHandler;
import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilities;
import com.buuz135.soulplied_energistics.applied.SoulKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.yxiao233.appliedsoul.common.registry.SoulItems;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SoulCollectorLogic {
    protected final SoulCollectorHost host;
    protected final IManagedGridNode mainNode;
    private @Nullable MEStorage networkStorage;
    private final IUpgradeInventory upgrades;
    public SoulCollectorLogic(IManagedGridNode gridNode, SoulCollectorHost host, Item is) {
        this.host = host;
        this.mainNode = gridNode.setFlags(new GridFlags[]{GridFlags.REQUIRE_CHANNEL}).addService(IGridTickable.class, new SoulCollectorLogic.Ticker());
        this.upgrades = UpgradeInventories.forMachine(is, 2, this::onUpgradesChanged);
    }

    private void onUpgradesChanged() {
        this.host.saveChanges();
//        if (!this.upgrades.isInstalled(AEItems.CRAFTING_CARD)) {
//            this.cancelCrafting();
//        }
//
//        this.updatePlan();
    }

    public void writeToNBT(CompoundTag tag, HolderLookup.Provider registries) {
        this.upgrades.writeToNBT(tag, "upgrades", registries);
    }

    public void readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {
        this.upgrades.readFromNBT(tag, "upgrades", registries);
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
        this.networkStorage = this.mainNode.getGrid().getStorageService().getInventory();
        this.notifyNeighbors();
    }

    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
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
            int radius = this.getUpgrades().getInstalledUpgrades(SoulItems.RANGE_CARD);
            List<?> blockEntities = getBlockEntities(level, getCheckArea(radius), IndustrialForegoingSouls.SOUL_LASER_BLOCK.type().get());
            blockEntities.forEach(entity ->{
                if(entity instanceof SoulLaserBaseBlockEntity soulLaserBaseBlockEntity){
                    ISoulHandler capability = level.getCapability(SoulCapabilities.BLOCK, soulLaserBaseBlockEntity.getBlockPos(), Direction.UP);
                    if(capability != null){
                        int amount = capability.getSoulInTank(0);
                        long curAmount =  networkInv.insert(SoulKey.INSTANCE,amount,Actionable.SIMULATE,IActionSource.empty());
                        networkInv.insert(SoulKey.INSTANCE,curAmount,Actionable.MODULATE,IActionSource.empty());
                        capability.drain((int) curAmount,ISoulHandler.Action.EXECUTE);
                    }
                }
            });
            return true;
        }
    }

    private <T extends BlockEntity> List<T> getBlockEntities(Level level, AABB area, BlockEntityType<T> type){
        if(level == null || type == null){
            return List.of();
        }else{
            List<T> entities = new ArrayList<>();
            BlockPos.betweenClosed((int) area.minX, (int) area.minY, (int) area.minZ, (int) area.maxX, (int) area.maxY, (int) area.maxZ).forEach(pos ->{
                level.getBlockEntity(pos,type).ifPresent(entities::add);
            });
            return entities;
        }
    }

    private AABB getCheckArea(int radius){
        BlockEntity blockEntity = host.getBlockEntity();
        BlockPos blockPos = blockEntity.getBlockPos();
        Level level = blockEntity.getLevel();
        if(level != null){
            ChunkAccess curChunk = level.getChunk(blockPos);
            ChunkPos curChunkPos = curChunk.getPos();
            ChunkPos min = level.getChunk(curChunkPos.x - radius, curChunkPos.z - radius).getPos();
            ChunkPos max = level.getChunk(curChunkPos.x + radius, curChunkPos.z + radius).getPos();
            return new AABB(min.getMinBlockX(), level.getMinBuildHeight(),min.getMinBlockZ(), max.getMaxBlockX(), level.getMaxBuildHeight(), max.getMaxBlockZ());

        }
        return new AABB(blockPos);
    }

    private class Ticker implements IGridTickable {
        private Ticker() {
        }

        public TickingRequest getTickingRequest(IGridNode node) {
            return new TickingRequest(5,120,false);
        }

        public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
            if (!SoulCollectorLogic.this.mainNode.isActive()) {
                return TickRateModulation.SLEEP;
            } else {
                return SoulCollectorLogic.this.tick() ? TickRateModulation.URGENT : TickRateModulation.SLEEP;
            }
        }
    }
}
