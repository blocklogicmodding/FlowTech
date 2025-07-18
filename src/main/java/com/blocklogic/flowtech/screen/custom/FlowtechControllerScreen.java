package com.blocklogic.flowtech.screen.custom;

import com.blocklogic.flowtech.FlowTech;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;


public class FlowtechControllerScreen extends AbstractContainerScreen<FlowtechControllerMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "textures/gui/flowtech_controller_gui.png");

    public FlowtechControllerScreen(FlowtechControllerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 140;
        this.imageWidth = 176;
        this.inventoryLabelY = 72-26;
        this.inventoryLabelX = 8;

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
    }


    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(pGuiGraphics, mouseX, mouseY);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }
}