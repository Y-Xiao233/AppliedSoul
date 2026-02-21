package net.yxiao233.appliedsoul.mixin;

import appeng.block.misc.InterfaceBlock;
import com.hrznstudio.titanium.block_network.INetworkDirectionalConnection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InterfaceBlock.class)
public abstract class MixinInterfaceBlock extends Block implements INetworkDirectionalConnection {
    public MixinInterfaceBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canConnect(BlockState blockState, Direction direction) {
        return true;
    }
}
