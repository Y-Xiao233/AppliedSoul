package net.yxiao233.appliedsoul.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.yxiao233.appliedsoul.common.block.entity.SoulBroadcastBlockEntity;
import net.yxiao233.appliedsoul.common.network.BroadcastConnectorUsePacket;
import net.yxiao233.appliedsoul.common.registry.SoulComponents;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class BroadcastConnectToolItem extends Item {
    public BroadcastConnectToolItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        var stack = player.getMainHandItem();
        if (level.isClientSide()) {
            return InteractionResultHolder.pass(stack);
        }

        if (getSoulBroadcastPos(stack) != null) {
            clearSelectedBroadcast(stack);
            BroadcastConnectToolItem.clearSelectedBroadcast(stack);
            player.displayClientMessage(
                    Component.translatable("tip.appliedsoul.broadcast_connect_tool.clear_selected").withStyle(ChatFormatting.GREEN), true);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        return handle(context);
    }

    private InteractionResult handle(UseOnContext context) {
        var player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        var level = context.getLevel();
        var pos = context.getClickedPos();
        var targetBe = level.getBlockEntity(pos);
        boolean isHost = targetBe instanceof SoulBroadcastBlockEntity;
        boolean isMachine = targetBe != null;

        if (!isHost && !isMachine) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            PacketDistributor.sendToServer(new BroadcastConnectorUsePacket(pos));
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        }
        return InteractionResult.SUCCESS;
    }

    public static BlockPos getSoulBroadcastPos(ItemStack stack){
        if(stack.getItem() instanceof BroadcastConnectToolItem){
            return stack.get(SoulComponents.BLOCK_POS);
        }
        return null;
    }

    public static String getSoulBroadcastDimension(ItemStack stack){
        if(stack.getItem() instanceof BroadcastConnectToolItem){
            return Objects.requireNonNull(stack.get(DataComponents.CUSTOM_DATA)).copyTag().getString("dimension");
        }
        return null;
    }

    public static boolean checkDimension(ItemStack stack, String other){
        String soulBroadcastDimension = BroadcastConnectToolItem.getSoulBroadcastDimension(stack);
        return soulBroadcastDimension != null && soulBroadcastDimension.equals(other);
    }

    public static boolean checkDimension(ItemStack stack, ResourceKey<Level> other){
        String soulBroadcastDimension = BroadcastConnectToolItem.getSoulBroadcastDimension(stack);
        return soulBroadcastDimension != null && soulBroadcastDimension.equals(other.location().toString());
    }

    public static void setSoulBroadcastPos(ItemStack stack, BlockPos pos, Level level){
        if(stack.getItem() instanceof BroadcastConnectToolItem){
            stack.set(SoulComponents.BLOCK_POS, pos);
            CompoundTag dimension = new CompoundTag();
            dimension.putString("dimension", level.dimension().location().toString());
            CustomData.set(DataComponents.CUSTOM_DATA,stack,dimension);
        }
    }

    public static void clearSelectedBroadcast(ItemStack stack){
        stack.remove(SoulComponents.BLOCK_POS);
        stack.remove(DataComponents.CUSTOM_DATA);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltips, @NotNull TooltipFlag tooltipFlag) {
        if(stack.has(SoulComponents.BLOCK_POS)){
            BlockPos pos = stack.get(SoulComponents.BLOCK_POS);
            if(pos != null){
                tooltips.add(Component.translatable("tooltip.appliedsoul.connection_pos",pos.getX(),pos.getY(),pos.getZ()).withStyle(ChatFormatting.GOLD));
            }
        }
        if(stack.has(DataComponents.CUSTOM_DATA)){
            String dimension = stack.get(DataComponents.CUSTOM_DATA).copyTag().getString("dimension");
            tooltips.add(Component.translatable("tooltip.appliedsoul.connection_dimension",dimension).withStyle(ChatFormatting.GREEN));
        }
    }
}
