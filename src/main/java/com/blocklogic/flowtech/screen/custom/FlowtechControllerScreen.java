package com.blocklogic.flowtech.screen.custom;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.network.ModConfigPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;


public class FlowtechControllerScreen extends AbstractContainerScreen<FlowtechControllerMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "textures/gui/flowtech_controller_gui.png");

    private boolean playerKillMode = false;

    public FlowtechControllerScreen(FlowtechControllerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 140;
        this.imageWidth = 176;
        this.inventoryLabelY = 72-26;
        this.inventoryLabelX = 8;

        syncFromBlockEntity();
    }

    private void syncFromBlockEntity() {
        if (menu.blockEntity != null) {
            playerKillMode = menu.blockEntity.isPlayerKillMode();
        }
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderPlayerKillToggle(guiGraphics, x, y);
    }

    private void renderPlayerKillToggle(GuiGraphics guiGraphics, int x, int y) {
        ResourceLocation toggleHandle = ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "toggle_scroller_handle");
        int handleX = playerKillMode ? x + 161 : x + 153;
        int handleY = y + 22;
        guiGraphics.blitSprite(toggleHandle, handleX, handleY, 6, 10);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (mouseX >= x + 153 && mouseX <= x + 167 && mouseY >= y + 22 && mouseY <= y + 32) {
            playerKillMode = !playerKillMode;

            PacketDistributor.sendToServer(new ModConfigPacket(
                    ModConfigPacket.ConfigTarget.CONTROLLER_BLOCK,
                    menu.blockEntity.getBlockPos(),
                    ModConfigPacket.ConfigType.CONTROLLER_PLAYER_KILL_MODE,
                    0,
                    playerKillMode));

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        syncFromBlockEntity();
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        renderCustomTooltips(pGuiGraphics, mouseX, mouseY);
        this.renderTooltip(pGuiGraphics, mouseX, mouseY);
    }

    private void renderCustomTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (mouseX >= x + 153 && mouseX <= x + 167 && mouseY >= y + 22 && mouseY <= y + 32) {
            Component tooltipText = playerKillMode ?
                    Component.translatable("tooltip.flowtech.controller.player_kill_mode.enabled") :
                    Component.translatable("tooltip.flowtech.controller.player_kill_mode.disabled");
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        syncFromBlockEntity();
    }
}