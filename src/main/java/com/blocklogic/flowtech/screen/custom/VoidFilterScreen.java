package com.blocklogic.flowtech.screen.custom;

import com.blocklogic.flowtech.FlowTech;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VoidFilterScreen extends AbstractContainerScreen<VoidFilterMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "textures/gui/void_filter_gui.png");

    public VoidFilterScreen(VoidFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 206;
        this.imageWidth = 176;
        this.inventoryLabelY = 111;
        this.inventoryLabelX = 8;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderToggleHandles(guiGraphics, x, y);
    }

    private void renderToggleHandles(GuiGraphics guiGraphics, int x, int y) {
        ResourceLocation toggleHandle = ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "toggle_scroller_handle");

        int nbtHandleX = menu.isIgnoreNBT() ? x + 127 : x + 117;
        int nbtHandleY = y + 107;
        guiGraphics.blitSprite(toggleHandle, nbtHandleX, nbtHandleY, 6, 10);

        int durabilityHandleX = menu.isIgnoreDurability() ? x + 161 : x + 153;
        int durabilityHandleY = y + 107;
        guiGraphics.blitSprite(toggleHandle, durabilityHandleX, durabilityHandleY, 6, 10);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (mouseX >= x + 117 && mouseX <= x + 133 && mouseY >= y + 107 && mouseY <= y + 117) {
            menu.setIgnoreNBT(!menu.isIgnoreNBT());
            return true;
        }

        if (mouseX >= x + 153 && mouseX <= x + 167 && mouseY >= y + 107 && mouseY <= y + 117) {
            menu.setIgnoreDurability(!menu.isIgnoreDurability());
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderCustomTooltips(guiGraphics, mouseX, mouseY);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderCustomTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (mouseX >= x + 117 && mouseX <= x + 133 && mouseY >= y + 107 && mouseY <= y + 117) {
            Component tooltipText = menu.isIgnoreNBT() ?
                    Component.translatable("tooltip.flowtech.void_filter.ignore_nbt.enabled") :
                    Component.translatable("tooltip.flowtech.void_filter.ignore_nbt.disabled");
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }

        if (mouseX >= x + 153 && mouseX <= x + 167 && mouseY >= y + 107 && mouseY <= y + 117) {
            Component tooltipText = menu.isIgnoreDurability() ?
                    Component.translatable("tooltip.flowtech.void_filter.ignore_durability.enabled") :
                    Component.translatable("tooltip.flowtech.void_filter.ignore_durability.disabled");
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }
}