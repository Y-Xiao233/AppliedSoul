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
import net.yxiao233.appliedsoul.common.me.logic.SoulCollectorHost;
import net.yxiao233.appliedsoul.common.me.logic.SoulCollectorLogic;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulCollectorBlockEntity extends AENetworkedBlockEntity implements SoulCollectorHost {
    private static final IGridNodeListener<SoulCollectorBlockEntity> NODE_LISTENER = new BlockEntityNodeListener<>() {
        public void onGridChanged(SoulCollectorBlockEntity nodeOwner, IGridNode node) {
            nodeOwner.logic.gridChanged();
        }
    };
    private final SoulCollectorLogic logic = this.createLogic();
    public SoulCollectorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }
    protected SoulCollectorLogic createLogic() {
        return new SoulCollectorLogic(this.getMainNode(),this, this.getItemFromBlockEntity().asItem());
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
    public SoulCollectorLogic getSoulCollectorLogic() {
        return logic;
    }

    @Override
    public @Nullable InternalInventory getSubInventory(ResourceLocation id) {
        return (InternalInventory)(id.equals(UPGRADES) ? this.logic.getUpgrades() : super.getSubInventory(id));
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
}
