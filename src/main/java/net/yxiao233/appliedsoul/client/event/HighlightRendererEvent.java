package net.yxiao233.appliedsoul.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.yxiao233.appliedsoul.AppliedSoul;
import net.yxiao233.appliedsoul.client.renderer.SoulRenderTypes;
import net.yxiao233.appliedsoul.common.block.entity.SoulBroadcastBlockEntity;
import net.yxiao233.appliedsoul.common.item.BroadcastConnectToolItem;
import net.yxiao233.appliedsoul.common.me.logic.SoulBroadcastLogic;
import net.yxiao233.appliedsoul.util.LevelUtil;

import java.util.ArrayList;

@SuppressWarnings({"removal","unused"})
@EventBusSubscriber(modid = AppliedSoul.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HighlightRendererEvent {
    @SubscribeEvent
    public static void onRenderer(RenderLevelStageEvent event){
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES){
            renderBroadcastConnections(event);
        }
    }

    public static void renderBroadcastConnections(RenderLevelStageEvent event){
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if(player != null && level != null){
            ItemStack stack = player.getMainHandItem();
            BlockPos soulBroadcastPos = BroadcastConnectToolItem.getSoulBroadcastPos(stack);
            if(soulBroadcastPos != null){
                if(level.getBlockEntity(soulBroadcastPos) instanceof SoulBroadcastBlockEntity soulBroadcast){
                    highlight(event,soulBroadcastPos,0,1,0,0.5f);
                    ArrayList<BlockPos> connectMachines = soulBroadcast.getConnectMachines();
                    ArrayList<BlockPos> allConnectMachines = SoulBroadcastLogic.getAllConnectMachines();
                    allConnectMachines.forEach(machinePos ->{
                        BlockEntity machine = level.getBlockEntity(machinePos);
                        if(machine != null && machine.getLevel() != null && LevelUtil.checkDimension(Minecraft.getInstance().level.dimension(),machine.getLevel().dimension())){;
                            if(SoulBroadcastLogic.contains(connectMachines,machinePos)){
                                highlight(event,machinePos,1,1,0,0.5f);
                            }else{
                                highlight(event,machinePos,1,0,0,0.5f);
                            }
                        }
                    });
                }
            }
        }
    }

    private static void highlight(RenderLevelStageEvent event, BlockPos machinePos,int red,int green,int blue, float alpha){
        Vec3 camera = event.getCamera().getPosition().reverse();
        AABB box = new AABB(machinePos).move(camera);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        LevelRenderer.renderLineBox(poseStack,bufferSource.getBuffer(RenderType.lines()),box,red,green,blue,alpha);
        for(Direction direction : Direction.values()){
            LevelRenderer.renderFace(poseStack,bufferSource.getBuffer(SoulRenderTypes.WORK_AREA), direction,(float) box.maxX,(float) box.maxY,(float) box.maxZ,(float) box.minX,(float) box.minY,(float) box.minZ,red,green,blue,alpha);
        }
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        poseStack.popPose();
    }
}
