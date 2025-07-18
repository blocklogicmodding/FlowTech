package com.blocklogic.flowtech.client.renderer;

import com.blocklogic.flowtech.block.entity.FlowtechCollectorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public class CollectorWireframeRenderer {

    private static boolean showWireframes = false;
    private static BlockPos activeCollectorPos = null;

    public static void toggleWireframe(BlockPos collectorPos) {
        if (activeCollectorPos != null && activeCollectorPos.equals(collectorPos)) {
            showWireframes = false;
            activeCollectorPos = null;
        } else {
            showWireframes = true;
            activeCollectorPos = collectorPos;
        }
    }

    public static boolean isWireframeActive(BlockPos collectorPos) {
        return showWireframes && activeCollectorPos != null && activeCollectorPos.equals(collectorPos);
    }

    public static void clearWireframes() {
        showWireframes = false;
        activeCollectorPos = null;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        if (!showWireframes || activeCollectorPos == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Level level = minecraft.level;

        if (player == null || level == null) {
            return;
        }

        if (level.getBlockEntity(activeCollectorPos) instanceof FlowtechCollectorBlockEntity collector) {
            AABB pickupZone = calculatePickupZone(collector);
            renderWireframe(event.getPoseStack(), pickupZone, event.getCamera().getPosition());
        } else {
            clearWireframes();
        }
    }

    private static AABB calculatePickupZone(FlowtechCollectorBlockEntity collector) {
        BlockPos pos = collector.getBlockPos();
        int range = collector.getPickupRange();

        double minX = pos.getX() - range + collector.getEastWestOffset();
        double maxX = pos.getX() + range + 1 + collector.getEastWestOffset();
        double minY = pos.getY() - range + collector.getDownUpOffset();
        double maxY = pos.getY() + range + 1 + collector.getDownUpOffset();
        double minZ = pos.getZ() - range + collector.getNorthSouthOffset();
        double maxZ = pos.getZ() + range + 1 + collector.getNorthSouthOffset();

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static void renderWireframe(PoseStack poseStack, AABB aabb, Vec3 cameraPos) {
        poseStack.pushPose();

        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());

        float red = 0.0f;
        float green = 1.0f;
        float blue = 1.0f;
        float alpha = 0.8f;

        LevelRenderer.renderLineBox(poseStack, buffer, aabb, red, green, blue, alpha);

        bufferSource.endBatch(RenderType.lines());
        poseStack.popPose();
    }
}