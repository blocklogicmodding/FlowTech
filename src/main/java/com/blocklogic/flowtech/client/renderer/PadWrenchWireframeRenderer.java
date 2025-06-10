package com.blocklogic.flowtech.client.renderer;

import com.blocklogic.flowtech.block.entity.AttackPadBlockEntity;
import com.blocklogic.flowtech.block.entity.FlowtechControllerBlockEntity;
import com.blocklogic.flowtech.component.ModDataComponents;
import com.blocklogic.flowtech.component.PadWrenchData;
import com.blocklogic.flowtech.item.custom.PadWrenchItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public class PadWrenchWireframeRenderer {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Level level = minecraft.level;

        if (player == null || level == null) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        ItemStack wrenchStack = null;
        if (mainHand.getItem() instanceof PadWrenchItem) {
            wrenchStack = mainHand;
        } else if (offHand.getItem() instanceof PadWrenchItem) {
            wrenchStack = offHand;
        }

        if (wrenchStack == null) {
            return;
        }

        PadWrenchData data = wrenchStack.getOrDefault(ModDataComponents.PAD_WRENCH_DATA.get(), PadWrenchData.DEFAULT);

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();

        if (data.selectedController() != null) {
            renderControllerWireframe(poseStack, cameraPos, data.selectedController(), level);
        }

        if (data.selectedController() != null && level.getBlockEntity(data.selectedController()) instanceof FlowtechControllerBlockEntity controller) {
            renderLinkedPadsWireframe(poseStack, cameraPos, controller.getLinkedPads(), level);
        }

        if (data.firstMultiPos() != null && data.selectionMode() == PadWrenchData.SelectionMode.MULTI) {
            renderMultiSelectionWireframe(poseStack, cameraPos, data.firstMultiPos());
        }
    }

    private static void renderControllerWireframe(PoseStack poseStack, Vec3 cameraPos, BlockPos controllerPos, Level level) {
        if (!(level.getBlockEntity(controllerPos) instanceof FlowtechControllerBlockEntity)) {
            return;
        }

        AABB aabb = new AABB(controllerPos);
        renderWireframeBox(poseStack, cameraPos, aabb, 0.0f, 0.8f, 1.0f, 0.8f);
    }

    private static void renderLinkedPadsWireframe(PoseStack poseStack, Vec3 cameraPos, Set<BlockPos> linkedPads, Level level) {
        for (BlockPos padPos : linkedPads) {
            if (level.getBlockEntity(padPos) instanceof AttackPadBlockEntity attackPad && attackPad.isLinked()) {
                AABB aabb = new AABB(padPos);
                renderWireframeBox(poseStack, cameraPos, aabb, 0.0f, 1.0f, 0.0f, 0.6f);
            }
        }
    }

    private static void renderMultiSelectionWireframe(PoseStack poseStack, Vec3 cameraPos, BlockPos firstPos) {
        AABB aabb = new AABB(firstPos);
        renderWireframeBox(poseStack, cameraPos, aabb, 1.0f, 1.0f, 0.0f, 0.8f);
    }

    private static void renderWireframeBox(PoseStack poseStack, Vec3 cameraPos, AABB aabb, float red, float green, float blue, float alpha) {
        poseStack.pushPose();

        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());

        LevelRenderer.renderLineBox(poseStack, buffer, aabb, red, green, blue, alpha);

        bufferSource.endBatch(RenderType.lines());
        poseStack.popPose();
    }
}