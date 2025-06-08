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

    // Toggle wireframe visibility for a specific collector
    public static void toggleWireframe(BlockPos collectorPos) {
        if (activeCollectorPos != null && activeCollectorPos.equals(collectorPos)) {
            // Same collector - toggle off
            showWireframes = false;
            activeCollectorPos = null;
        } else {
            // New collector or first time - toggle on
            showWireframes = true;
            activeCollectorPos = collectorPos;
        }
    }

    // Check if wireframes are currently shown for a collector
    public static boolean isWireframeActive(BlockPos collectorPos) {
        return showWireframes && activeCollectorPos != null && activeCollectorPos.equals(collectorPos);
    }

    // Clear wireframes (e.g., when collector is broken)
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

        // Check if the collector still exists and has wireframe enabled
        if (level.getBlockEntity(activeCollectorPos) instanceof FlowtechCollectorBlockEntity collector) {
            // Calculate pickup zone with modules and offsets
            AABB pickupZone = calculatePickupZone(collector);

            // Render wireframe
            renderWireframe(event.getPoseStack(), pickupZone, player.position());
        } else {
            // Collector no longer exists, clear wireframes
            clearWireframes();
        }
    }

    private static AABB calculatePickupZone(FlowtechCollectorBlockEntity collector) {
        BlockPos pos = collector.getBlockPos();
        int range = collector.getPickupRange();

        // Apply offsets
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

        // Translate to camera-relative coordinates
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());

        // Define wireframe color (cyan with transparency)
        float red = 0.0f;
        float green = 1.0f;
        float blue = 1.0f;
        float alpha = 0.8f;

        // Render wireframe box
        LevelRenderer.renderLineBox(poseStack, buffer, aabb, red, green, blue, alpha);

        bufferSource.endBatch();
        poseStack.popPose();
    }
}