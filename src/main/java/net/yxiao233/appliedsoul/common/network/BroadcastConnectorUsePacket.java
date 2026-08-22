package net.yxiao233.appliedsoul.common.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.block.entity.SoulBroadcastBlockEntity;
import net.yxiao233.appliedsoul.common.item.BroadcastConnectToolItem;
import net.yxiao233.appliedsoul.common.registry.SoulItems;
import org.jetbrains.annotations.NotNull;

public record BroadcastConnectorUsePacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<BroadcastConnectorUsePacket> TYPE = new Type<>(AppliedSoul.makeId("broadcast_connector_use"));
    public static final Codec<BroadcastConnectorUsePacket> CODEC = RecordCodecBuilder.create(builder ->{
        return builder.group(BlockPos.CODEC.fieldOf("pos").forGetter(BroadcastConnectorUsePacket::pos)).apply(builder,BroadcastConnectorUsePacket::new);
    });
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static void handle(BroadcastConnectorUsePacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                payload.handleOnServer(player);
            }
        });
    }

    private void handleOnServer(ServerPlayer player) {
        var level = player.level();
        if (!level.isLoaded(pos)) return;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BroadcastConnectToolItem)) return;
        if (!player.canInteractWithBlock(pos, 1.0D)) return;

        var targetBe = level.getBlockEntity(pos);
        boolean isHost = targetBe instanceof SoulBroadcastBlockEntity;
        boolean isMachine = targetBe != null;
        if (!isHost && !isMachine) return;

        if(isHost){
            if(BroadcastConnectToolItem.getSoulBroadcastPos(stack) == null){
                BroadcastConnectToolItem.setSoulBroadcastPos(stack,pos,level);
                player.displayClientMessage(
                        Component.translatable("tip.appliedsoul.broadcast_connect_tool.selected",pos.getX(),pos.getY(),pos.getZ()).withStyle(ChatFormatting.GREEN), true);
                return;
            }
            if(BroadcastConnectToolItem.getSoulBroadcastPos(stack).equals(pos) && BroadcastConnectToolItem.checkDimension(stack,level.dimension())){
                BroadcastConnectToolItem.clearSelectedBroadcast(stack);
                player.displayClientMessage(
                        Component.translatable("tip.appliedsoul.broadcast_connect_tool.clear_selected").withStyle(ChatFormatting.GREEN), true);
                return;
            }
            BroadcastConnectToolItem.setSoulBroadcastPos(stack,pos,level);
            player.displayClientMessage(
                    Component.translatable("tip.appliedsoul.broadcast_connect_tool.selected",pos.getX(),pos.getY(),pos.getZ()).withStyle(ChatFormatting.GREEN), true);
            return;
        }
        if(isMachine && !isHost){
            if(BroadcastConnectToolItem.getSoulBroadcastPos(stack) == null){
                player.displayClientMessage(
                        Component.translatable("tip.appliedsoul.broadcast_connect_tool.need_broadcast").withStyle(ChatFormatting.RED), true);
                return;
            }else if(level.getBlockEntity(BroadcastConnectToolItem.getSoulBroadcastPos(stack)) instanceof SoulBroadcastBlockEntity entity){
                if(BroadcastConnectToolItem.checkDimension(stack,level.dimension())){
                    int connectResult = entity.tryConnect(pos);
                    if(connectResult == -1){
                        player.displayClientMessage(
                                Component.translatable("tip.appliedsoul.broadcast_connect_tool.has_connected").withStyle(ChatFormatting.RED), true);
                    }else if(connectResult == 0){
                        entity.tryRemove(pos);
                        player.displayClientMessage(
                                Component.translatable("tip.appliedsoul.broadcast_connect_tool.removed").withStyle(ChatFormatting.GREEN), true);
                    }else if(connectResult == 1){
                        player.displayClientMessage(
                                Component.translatable("tip.appliedsoul.broadcast_connect_tool.connected").withStyle(ChatFormatting.GREEN), true);
                    }else if(connectResult == 2){
                        int radius = (entity.getSoulBroadcastLogic().getUpgrades().getInstalledUpgrades(SoulItems.RANGE_CARD) * 16) + 8;
                        player.displayClientMessage(
                                Component.translatable("tip.appliedsoul.broadcast_connect_tool.out_of_range",radius).withStyle(ChatFormatting.RED), true);
                    }
                    return;
                }
                player.displayClientMessage(
                        Component.translatable("tip.appliedsoul.broadcast_connect_tool.different_dimension").withStyle(ChatFormatting.RED), true);

            }
        }
    }
}
