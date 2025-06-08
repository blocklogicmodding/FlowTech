package com.blocklogic.flowtech.screen.custom;

import com.blocklogic.flowtech.FlowTech;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class FlowtechCollectorScreen extends AbstractContainerScreen<FlowtechCollectorMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "textures/gui/collector_gui.png");

    // Side Config Button Sprites
    private static final WidgetSprites SIDE_CONFIG_SIDES_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "side_config_sides_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "side_config_sides_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "side_config_sides_btn_hover"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "side_config_sides_btn_hover")
    );

    private static final WidgetSprites SIDE_CONFIG_FRONT_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "side_config_front_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "side_config_front_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "side_config_front_btn_hover"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "side_config_front_btn_hover")
    );

    private static final WidgetSprites SIDE_CONFIG_BACK_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "side_config_back_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "side_config_back_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "side_config_back_btn_hover"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "side_config_back_btn_hover")
    );

    // Offset Control Button Sprites
    private static final WidgetSprites REDUCE_OFFSET_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "reduce_offset_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "reduce_offset_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "reduce_offset_btn_hover"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "reduce_offset_btn_hover")
    );

    private static final WidgetSprites INCREASE_OFFSET_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "increase_offset_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "increase_offset_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "increase_offset_btn_hover"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "increase_offset_btn_hover")
    );

    // Zone Wireframe Toggle Sprites
    private static final WidgetSprites TOGGLE_WIREFRAME_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "toggle_zone_wireframe_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "toggle_zone_wireframe_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "toggle_zone_wireframe_btn_hover"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "toggle_zone_wireframe_btn_hover")
    );

    // XP Button Sprites
    private static final WidgetSprites WITHDRAW_XP_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "withdraw_xp_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "withdraw_xp_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "withdraw_xp_btn_hover"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "withdraw_xp_btn_hover")
    );

    private static final WidgetSprites DEPOSIT_XP_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "deposit_xp_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "deposit_xp_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "deposit_xp_btn_hover"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "deposit_xp_btn_hover")
    );

    // UI Components
    private EditBox xpInputField;

    // Config State Variables (these would normally sync with the block entity)
    private boolean topSideActive = false;
    private boolean eastSideActive = false;
    private boolean frontSideActive = false;
    private boolean westSideActive = false;
    private boolean bottomSideActive = false;
    private boolean backSideActive = false;

    private int downUpOffset = 0;
    private int northSouthOffset = 0;
    private int eastWestOffset = 0;

    private boolean showWireframe = false;
    private boolean xpCollectionEnabled = false;

    // XP System
    private int storedXP = 0;
    private int maxStoredXP = 2147483647;

    public FlowtechCollectorScreen(FlowtechCollectorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 236;
        this.imageWidth = 234;
        this.inventoryLabelY = 72 + 70;
        this.inventoryLabelX = 8 + 30;
    }

    @Override
    protected void init() {
        super.init();

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        // Clear existing widgets
        this.clearWidgets();

        // Initialize XP Input Field
        this.xpInputField = new EditBox(this.font, leftPos + 9, topPos + 110, 39, 8, Component.translatable("gui.flowtech.collector.xp_input"));
        this.xpInputField.setMaxLength(6);
        this.xpInputField.setValue("0");
        this.xpInputField.setBordered(false);
        this.xpInputField.setTextColor(0xFFFFFF);
        this.xpInputField.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.xp_input")));
        this.addRenderableWidget(this.xpInputField);

        // Side Config Buttons with tooltips
        ImageButton topSideButton = new ImageButton(leftPos + 203, topPos + 15, 12, 12,
                createConditionalSprites(SIDE_CONFIG_SIDES_SPRITES, () -> topSideActive),
                button -> toggleSideConfig("top"));
        topSideButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.side_config.top")));
        this.addRenderableWidget(topSideButton);

        ImageButton eastSideButton = new ImageButton(leftPos + 191, topPos + 27, 12, 12,
                createConditionalSprites(SIDE_CONFIG_SIDES_SPRITES, () -> eastSideActive),
                button -> toggleSideConfig("east"));
        eastSideButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.side_config.east")));
        this.addRenderableWidget(eastSideButton);

        ImageButton frontSideButton = new ImageButton(leftPos + 203, topPos + 27, 12, 12,
                createConditionalSprites(SIDE_CONFIG_FRONT_SPRITES, () -> frontSideActive),
                button -> toggleSideConfig("front"));
        frontSideButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.side_config.front")));
        this.addRenderableWidget(frontSideButton);

        ImageButton westSideButton = new ImageButton(leftPos + 215, topPos + 27, 12, 12,
                createConditionalSprites(SIDE_CONFIG_SIDES_SPRITES, () -> westSideActive),
                button -> toggleSideConfig("west"));
        westSideButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.side_config.west")));
        this.addRenderableWidget(westSideButton);

        ImageButton bottomSideButton = new ImageButton(leftPos + 203, topPos + 39, 12, 12,
                createConditionalSprites(SIDE_CONFIG_SIDES_SPRITES, () -> bottomSideActive),
                button -> toggleSideConfig("bottom"));
        bottomSideButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.side_config.bottom")));
        this.addRenderableWidget(bottomSideButton);

        ImageButton backSideButton = new ImageButton(leftPos + 215, topPos + 39, 12, 12,
                createConditionalSprites(SIDE_CONFIG_BACK_SPRITES, () -> backSideActive),
                button -> toggleSideConfig("back"));
        backSideButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.side_config.back")));
        this.addRenderableWidget(backSideButton);

        // Down/Up Offset Controls with tooltips
        ImageButton duDecreaseButton = new ImageButton(leftPos + 190, topPos + 64, 10, 10, REDUCE_OFFSET_SPRITES,
                button -> adjustOffset("downUp", -1));
        duDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.down_up.decrease")));
        this.addRenderableWidget(duDecreaseButton);

        ImageButton duIncreaseButton = new ImageButton(leftPos + 218, topPos + 64, 10, 10, INCREASE_OFFSET_SPRITES,
                button -> adjustOffset("downUp", 1));
        duIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.down_up.increase")));
        this.addRenderableWidget(duIncreaseButton);

        // North/South Offset Controls with tooltips
        ImageButton nsDecreaseButton = new ImageButton(leftPos + 190, topPos + 86, 10, 10, REDUCE_OFFSET_SPRITES,
                button -> adjustOffset("northSouth", -1));
        nsDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.north_south.decrease")));
        this.addRenderableWidget(nsDecreaseButton);

        ImageButton nsIncreaseButton = new ImageButton(leftPos + 218, topPos + 86, 10, 10, INCREASE_OFFSET_SPRITES,
                button -> adjustOffset("northSouth", 1));
        nsIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.north_south.increase")));
        this.addRenderableWidget(nsIncreaseButton);

        // East/West Offset Controls with tooltips
        ImageButton ewDecreaseButton = new ImageButton(leftPos + 190, topPos + 108, 10, 10, REDUCE_OFFSET_SPRITES,
                button -> adjustOffset("eastWest", -1));
        ewDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.east_west.decrease")));
        this.addRenderableWidget(ewDecreaseButton);

        ImageButton ewIncreaseButton = new ImageButton(leftPos + 218, topPos + 108, 10, 10, INCREASE_OFFSET_SPRITES,
                button -> adjustOffset("eastWest", 1));
        ewIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.east_west.increase")));
        this.addRenderableWidget(ewIncreaseButton);

        // Show Wireframe Toggle with tooltip
        ImageButton wireframeButton = new ImageButton(leftPos + 155, topPos + 109, 10, 10,
                createConditionalSprites(TOGGLE_WIREFRAME_SPRITES, () -> showWireframe),
                button -> toggleWireframe());
        wireframeButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.wireframe_toggle")));
        this.addRenderableWidget(wireframeButton);

        // XP Buttons with tooltips
        ImageButton withdrawButton = new ImageButton(leftPos + 52, topPos + 109, 10, 10, WITHDRAW_XP_SPRITES,
                button -> withdrawXP());
        withdrawButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.xp.withdraw")));
        this.addRenderableWidget(withdrawButton);

        ImageButton depositButton = new ImageButton(leftPos + 65, topPos + 109, 10, 10, DEPOSIT_XP_SPRITES,
                button -> depositXP());
        depositButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.xp.deposit")));
        this.addRenderableWidget(depositButton);
    }

    private WidgetSprites createConditionalSprites(WidgetSprites baseSprites, java.util.function.Supplier<Boolean> isActive) {
        if (isActive.get()) {
            return new WidgetSprites(
                    ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, baseSprites.enabled().getPath() + "_active"),
                    baseSprites.disabled(),
                    ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, baseSprites.enabledFocused().getPath().replace("_hover", "_active")),
                    baseSprites.disabledFocused()
            );
        }
        return baseSprites;
    }

    private void toggleSideConfig(String side) {
        switch (side) {
            case "top" -> topSideActive = !topSideActive;
            case "east" -> eastSideActive = !eastSideActive;
            case "front" -> frontSideActive = !frontSideActive;
            case "west" -> westSideActive = !westSideActive;
            case "bottom" -> bottomSideActive = !bottomSideActive;
            case "back" -> backSideActive = !backSideActive;
        }
        // TODO: Send packet to server to update block entity
        this.init(); // Rebuild GUI to update button sprites
    }

    private void adjustOffset(String axis, int delta) {
        switch (axis) {
            case "downUp" -> {
                downUpOffset = Math.max(-10, Math.min(10, downUpOffset + delta));
            }
            case "northSouth" -> {
                northSouthOffset = Math.max(-10, Math.min(10, northSouthOffset + delta));
            }
            case "eastWest" -> {
                eastWestOffset = Math.max(-10, Math.min(10, eastWestOffset + delta));
            }
        }
        // TODO: Send packet to server to update block entity
    }

    private void toggleWireframe() {
        showWireframe = !showWireframe;
        // TODO: Send packet to server and/or handle client-side wireframe rendering respecting the installed modules
        this.init(); // Rebuild GUI to update button sprite
    }

    private void withdrawXP() {
        try {
            int amount = Integer.parseInt(xpInputField.getValue());
            if (amount > 0 && amount <= storedXP) {
                // TODO: Send packet to server to withdraw XP
                storedXP -= amount;
                xpInputField.setValue("0");
            }
        } catch (NumberFormatException e) {
            xpInputField.setValue("0");
        }
    }

    private void depositXP() {
        try {
            int amount = Integer.parseInt(xpInputField.getValue());
            if (amount > 0) {
                // TODO: Send packet to server to deposit XP
                // For now, just simulate adding to stored XP
                storedXP = Math.min(maxStoredXP, storedXP + amount);
                xpInputField.setValue("0");
            }
        } catch (NumberFormatException e) {
            xpInputField.setValue("0");
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Render custom elements
        renderOffsetValues(guiGraphics, x, y);
        renderXPCollectionToggle(guiGraphics, x, y);
        renderXPBar(guiGraphics, x, y);
    }

    private void renderOffsetValues(GuiGraphics guiGraphics, int x, int y) {
        var poseStack = guiGraphics.pose();

        float scale = 0.65f; // scale factor for smaller text

        // ───── Scale and render the offset values ─────
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0f);

        // Adjusted positions (scaled down, so coordinates must be scaled up)
        guiGraphics.drawString(this.font,
                (downUpOffset >= 0 ? "+" : "") + downUpOffset,
                (int)((x + 203) / scale),
                (int)((y + 67) / scale),
                0x000000, false);

        guiGraphics.drawString(this.font,
                (northSouthOffset >= 0 ? "+" : "") + northSouthOffset,
                (int)((x + 203) / scale),
                (int)((y + 89) / scale),
                0x000000, false);

        guiGraphics.drawString(this.font,
                (eastWestOffset >= 0 ? "+" : "") + eastWestOffset,
                (int)((x + 203) / scale),
                (int)((y + 111) / scale),
                0x000000, false);

        poseStack.popPose();

        // ───── Scale and render the labels ─────
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0f);

        guiGraphics.drawString(this.font, "D/U Offset", (int)((x + 190) / scale), (int)((y + 58) / scale), 0x000000, false);
        guiGraphics.drawString(this.font, "N/S Offset", (int)((x + 190) / scale), (int)((y + 80) / scale), 0x000000, false);
        guiGraphics.drawString(this.font, "E/W Offset", (int)((x + 190) / scale), (int)((y + 102) / scale), 0x000000, false);

        poseStack.popPose();
    }

    private void renderXPCollectionToggle(GuiGraphics guiGraphics, int x, int y) {
        // Render XP Collection Toggle Slider
        ResourceLocation toggleHandle = ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "toggle_scroller_handle");

        int handleX = xpCollectionEnabled ? x + 125 : x + 117; // End position vs Start position
        int handleY = y + 110;

        // Render toggle handle using blitSprite for GUI sprites
        guiGraphics.blitSprite(toggleHandle, handleX, handleY, 6, 10);
    }

    private void renderXPBar(GuiGraphics guiGraphics, int x, int y) {
        var poseStack = guiGraphics.pose();

        // Render XP Bar fill
        if (storedXP > 0) {
            int fillWidth = (int) ((218.0f * storedXP) / maxStoredXP);
            guiGraphics.fill(x + 8, y + 125, x + 8 + fillWidth, y + 133, 0xFF00FF00);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle XP Collection Toggle click
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (mouseX >= x + 118 && mouseX <= x + 134 && mouseY >= y + 110 && mouseY <= y + 118) {
            xpCollectionEnabled = !xpCollectionEnabled;
            // TODO: Send packet to server to update block entity and update blockstate to show collection on/off
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);

        // Render custom tooltips for areas that don't have widgets
        renderCustomTooltips(pGuiGraphics, mouseX, mouseY);

        this.renderTooltip(pGuiGraphics, mouseX, mouseY);
    }

    private void renderCustomTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // XP Collection Toggle tooltip
        if (mouseX >= x + 117 && mouseX <= x + 131 && mouseY >= y + 110 && mouseY <= y + 120) {
            Component tooltipText = xpCollectionEnabled ?
                    Component.translatable("tooltip.flowtech.collector.xp_collection.enabled") :
                    Component.translatable("tooltip.flowtech.collector.xp_collection.disabled");
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }

        // Offset value tooltips
        if (mouseX >= x + 203 && mouseX <= x + 215 && mouseY >= y + 67 && mouseY <= y + 75) {
            Component tooltipText = Component.translatable("tooltip.flowtech.collector.offset.down_up.value", downUpOffset);
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }

        if (mouseX >= x + 203 && mouseX <= x + 215 && mouseY >= y + 89 && mouseY <= y + 97) {
            Component tooltipText = Component.translatable("tooltip.flowtech.collector.offset.north_south.value", northSouthOffset);
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }

        if (mouseX >= x + 203 && mouseX <= x + 215 && mouseY >= y + 111 && mouseY <= y + 119) {
            Component tooltipText = Component.translatable("tooltip.flowtech.collector.offset.east_west.value", eastWestOffset);
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }
    }
}