package net.yxiao233.appliedsoul.common.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.common.network.BroadcastConnectorUsePacket;
import net.yxiao233.appliedsoul.util.CodecHelper;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = AppliedSoul.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetWorkRegistryEvent {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playBidirectional(
                BroadcastConnectorUsePacket.TYPE,
                CodecHelper.fromCodec(BroadcastConnectorUsePacket.CODEC),
                new DirectionalPayloadHandler<>(
                        BroadcastConnectorUsePacket::handle,
                        BroadcastConnectorUsePacket::handle
                )
        );
    }
}
