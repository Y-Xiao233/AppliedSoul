package net.yxiao233.appliedsoul.common.block.entity;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.me.helpers.BlockEntityNodeListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.yxiao233.appliedsoul.common.me.logic.SoulBroadcastHost;
import net.yxiao233.appliedsoul.common.me.logic.SoulBroadcastLogic;
import net.yxiao233.appliedsoul.common.registry.SoulItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SoulBroadcastBlockEntity extends AENetworkedBlockEntity implements SoulBroadcastHost {
    private static final IGridNodeListener<SoulBroadcastBlockEntity> NODE_LISTENER = new BlockEntityNodeListener<>() {
        public void onGridChanged(SoulBroadcastBlockEntity nodeOwner, IGridNode node) {
            nodeOwner.logic.gridChanged();
        }
    };
    private final SoulBroadcastLogic logic = this.createLogic();
    public SoulBroadcastBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }
    protected SoulBroadcastLogic createLogic() {
        return new SoulBroadcastLogic(this.getMainNode(),this, this.getItemFromBlockEntity().asItem());
    }
    public void clearContent() {
        super.clearContent();
        this.logic.clearContent();
    }
    @Override
    protected IManagedGridNode createMainNode() {
        return GridHelper.createManagedNode(this, NODE_LISTENER);
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return this.logic.getCableConnectionType(dir);
    }

    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        if (this.getMainNode().hasGridBooted()) {
            this.logic.notifyNeighbors();
        }
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        this.logic.addDrops(drops);
    }

    @Override
    public @Nullable InternalInventory getSubInventory(ResourceLocation id) {
        return id.equals(UPGRADES) ? this.logic.getUpgrades() : super.getSubInventory(id);
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.logic.writeToNBT(data, registries);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.logic.readFromNBT(data, registries);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag data = super.getUpdateTag(registries);
        this.logic.writeToNBT(data, registries);
        return data;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag data, HolderLookup.@NotNull Provider registries) {
        super.handleUpdateTag(data, registries);
        this.logic.readFromNBT(data, registries);
    }

    @Override
    public SoulBroadcastLogic getSoulBroadcastLogic() {
        return this.logic;
    }

    public ArrayList<BlockPos> getConnectMachines(){
        return this.logic.getConnectMachines();
    }

    public int tryConnect(BlockPos pos){
        if(SoulBroadcastLogic.contains(SoulBroadcastLogic.getAllConnectMachines(),pos)){
            if(SoulBroadcastLogic.contains(this.getConnectMachines(),pos)){
                return 0;
            }
            return -1;
        }
        if(!SoulBroadcastLogic.contains(this.getConnectMachines(),pos)){
            int radius = (this.getUpgrades().getInstalledUpgrades(SoulItems.RANGE_CARD) * 16) + 8;
            if(Math.abs(this.worldPosition.getX() - pos.getX()) <= radius && Math.abs(this.worldPosition.getZ() - pos.getZ()) <= radius){
                this.getConnectMachines().add(pos);
                SoulBroadcastLogic.getAllConnectMachines().add(pos);
                this.setChanged();
                this.syncToClient();
                return 1;
            }
            return 2;
        }else{
            return 0;
        }
    }

    public void tryRemove(BlockPos pos){
        if(SoulBroadcastLogic.contains(this.getConnectMachines(),pos)){
            this.getConnectMachines().remove(SoulBroadcastLogic.indexOf(this.getConnectMachines(),pos));
            SoulBroadcastLogic.getAllConnectMachines().remove(SoulBroadcastLogic.indexOf(SoulBroadcastLogic.getAllConnectMachines(),pos));
            this.setChanged();
            this.syncToClient();
        }
    }

    private void syncToClient() {
        Level level = this.getLevel();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public void clear(){
        SoulBroadcastLogic.getAllConnectMachines().removeAll(this.getConnectMachines());
        this.getConnectMachines().clear();
    }
}
