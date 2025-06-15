package com.blocklogic.flowtech.screen.custom;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.client.renderer.CollectorWireframeRenderer;
import com.blocklogic.flowtech.network.ModConfigPacket;
import com.blocklogic.flowtech.network.CollectorXpPacket;
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
import net.neoforged.neoforge.network.PacketDistributor;

public class FlowtechCollectorScreen extends AbstractContainerScreen<FlowtechCollectorMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "textures/gui/collector_gui.png");

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

    private static final WidgetSprites TOGGLE_WIREFRAME_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "toggle_zone_wireframe_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "toggle_zone_wireframe_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "toggle_zone_wireframe_btn_hover"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "toggle_zone_wireframe_btn_hover")
    );

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

    private static final WidgetSprites WITHDRAW_ALL_XP_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "withdraw_all_xp_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "withdraw_all_xp_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "withdraw_all_xp_btn_hover"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "withdraw_all_xp_btn_hover")
    );

    private static final WidgetSprites DEPOSIT_ALL_XP_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "deposit_all_xp_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "deposit_all_xp_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "deposit_all_xp_btn_hover"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "deposit_all_xp_btn_hover")
    );

    private static final WidgetSprites RESET_OFFSET_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "collection_zone_offset_reset_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "collection_zone_offset_reset_btn"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "collection_zone_offset_reset_btn_hover"),
            ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "collection_zone_offset_reset_btn_hover")
    );

    private EditBox xpInputField;

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

    private int storedXP = 0;
    private int maxStoredXP = 2147483647;

    public FlowtechCollectorScreen(FlowtechCollectorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 236;
        this.imageWidth = 234;
        this.inventoryLabelY = 72 + 70;
        this.inventoryLabelX = 8 + 30;

        syncFromBlockEntity();
    }

    private void syncFromBlockEntity() {
        if (menu.blockEntity != null) {
            topSideActive = menu.blockEntity.isTopSideActive();
            eastSideActive = menu.blockEntity.isEastSideActive();
            frontSideActive = menu.blockEntity.isFrontSideActive();
            westSideActive = menu.blockEntity.isWestSideActive();
            bottomSideActive = menu.blockEntity.isBottomSideActive();
            backSideActive = menu.blockEntity.isBackSideActive();

            downUpOffset = menu.blockEntity.getDownUpOffset();
            northSouthOffset = menu.blockEntity.getNorthSouthOffset();
            eastWestOffset = menu.blockEntity.getEastWestOffset();

            xpCollectionEnabled = menu.blockEntity.isXpCollectionEnabled();
            storedXP = menu.blockEntity.getStoredXP();
        }
    }

    private int xpToLevel(int xp) {
        if (xp < 0) return 0;

        int level = 0;
        int remaining = xp;

        while (remaining > 0) {
            int xpForNextLevel = getXpNeededForLevel(level);
            if (remaining >= xpForNextLevel) {
                remaining -= xpForNextLevel;
                level++;
            } else {
                break;
            }
        }
        return level;
    }

    private int getXpNeededForLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 16) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }

    private int levelToXp(int level) {
        if (level <= 0) return 0;

        int totalXp = 0;
        for (int i = 0; i < level; i++) {
            totalXp += getXpNeededForLevel(i);
        }
        return totalXp;
    }

    @Override
    protected void init() {
        super.init();

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        this.clearWidgets();

        this.xpInputField = new EditBox(this.font, leftPos + 9, topPos + 110, 39, 8, Component.translatable("gui.flowtech.collector.xp_input_levels"));
        this.xpInputField.setMaxLength(4);
        this.xpInputField.setValue("0");
        this.xpInputField.setBordered(false);
        this.xpInputField.setTextColor(0xFFFFFF);
        this.xpInputField.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.xp_input_levels")));
        this.addRenderableWidget(this.xpInputField);

        addSideConfigButtons(leftPos, topPos);

        addOffsetButtons(leftPos, topPos);

        addWireframeButton(leftPos, topPos);

        addXpButtons(leftPos, topPos);
    }

    private void addSideConfigButtons(int leftPos, int topPos) {
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
    }

    private void addOffsetButtons(int leftPos, int topPos) {
        ImageButton duDecreaseButton = new ImageButton(leftPos + 190, topPos + 64, 10, 10, REDUCE_OFFSET_SPRITES,
                button -> adjustOffset("downUp", -1));
        duDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.down_up.decrease")));
        this.addRenderableWidget(duDecreaseButton);

        ImageButton duIncreaseButton = new ImageButton(leftPos + 218, topPos + 64, 10, 10, INCREASE_OFFSET_SPRITES,
                button -> adjustOffset("downUp", 1));
        duIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.down_up.increase")));
        this.addRenderableWidget(duIncreaseButton);

        ImageButton nsDecreaseButton = new ImageButton(leftPos + 190, topPos + 86, 10, 10, REDUCE_OFFSET_SPRITES,
                button -> adjustOffset("northSouth", -1));
        nsDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.north_south.decrease")));
        this.addRenderableWidget(nsDecreaseButton);

        ImageButton nsIncreaseButton = new ImageButton(leftPos + 218, topPos + 86, 10, 10, INCREASE_OFFSET_SPRITES,
                button -> adjustOffset("northSouth", 1));
        nsIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.north_south.increase")));
        this.addRenderableWidget(nsIncreaseButton);

        ImageButton ewDecreaseButton = new ImageButton(leftPos + 190, topPos + 108, 10, 10, REDUCE_OFFSET_SPRITES,
                button -> adjustOffset("eastWest", -1));
        ewDecreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.east_west.decrease")));
        this.addRenderableWidget(ewDecreaseButton);

        ImageButton ewIncreaseButton = new ImageButton(leftPos + 218, topPos + 108, 10, 10, INCREASE_OFFSET_SPRITES,
                button -> adjustOffset("eastWest", 1));
        ewIncreaseButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.east_west.increase")));
        this.addRenderableWidget(ewIncreaseButton);

        ImageButton resetOffsetButton = new ImageButton(leftPos + 215, topPos + 125, 12, 12, RESET_OFFSET_SPRITES,
                button -> resetAllOffsets());
        resetOffsetButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.offset.reset_all")));
        this.addRenderableWidget(resetOffsetButton);
    }

    private void addWireframeButton(int leftPos, int topPos) {
        ImageButton wireframeButton = new ImageButton(leftPos + 155, topPos + 109, 10, 10,
                createConditionalSprites(TOGGLE_WIREFRAME_SPRITES, () -> showWireframe),
                button -> toggleWireframe());
        wireframeButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.wireframe_toggle")));
        this.addRenderableWidget(wireframeButton);
    }

    private void addXpButtons(int leftPos, int topPos) {
        ImageButton withdrawButton = new ImageButton(leftPos + 52, topPos + 109, 10, 10, WITHDRAW_XP_SPRITES,
                button -> withdrawXP());
        withdrawButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.xp.withdraw")));
        this.addRenderableWidget(withdrawButton);

        ImageButton depositButton = new ImageButton(leftPos + 65, topPos + 109, 10, 10, DEPOSIT_XP_SPRITES,
                button -> depositXP());
        depositButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.xp.deposit")));
        this.addRenderableWidget(depositButton);

        ImageButton withdrawAllButton = new ImageButton(leftPos + 77, topPos + 109, 10, 10, WITHDRAW_ALL_XP_SPRITES,
                button -> withdrawAllXP());
        withdrawAllButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.xp.withdraw_all")));
        this.addRenderableWidget(withdrawAllButton);

        ImageButton depositAllButton = new ImageButton(leftPos + 90, topPos + 109, 10, 10, DEPOSIT_ALL_XP_SPRITES,
                button -> depositAllXP());
        depositAllButton.setTooltip(Tooltip.create(Component.translatable("tooltip.flowtech.collector.xp.deposit_all")));
        this.addRenderableWidget(depositAllButton);
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
        ModConfigPacket.ConfigType configType = switch (side) {
            case "top" -> ModConfigPacket.ConfigType.COLLECTOR_TOP_SIDE;
            case "east" -> ModConfigPacket.ConfigType.COLLECTOR_EAST_SIDE;
            case "front" -> ModConfigPacket.ConfigType.COLLECTOR_FRONT_SIDE;
            case "west" -> ModConfigPacket.ConfigType.COLLECTOR_WEST_SIDE;
            case "bottom" -> ModConfigPacket.ConfigType.COLLECTOR_BOTTOM_SIDE;
            case "back" -> ModConfigPacket.ConfigType.COLLECTOR_BACK_SIDE;
            default -> null;
        };

        if (configType != null) {
            boolean newValue = switch (side) {
                case "top" -> !topSideActive;
                case "east" -> !eastSideActive;
                case "front" -> !frontSideActive;
                case "west" -> !westSideActive;
                case "bottom" -> !bottomSideActive;
                case "back" -> !backSideActive;
                default -> false;
            };

            switch (side) {
                case "top" -> topSideActive = newValue;
                case "east" -> eastSideActive = newValue;
                case "front" -> frontSideActive = newValue;
                case "west" -> westSideActive = newValue;
                case "bottom" -> bottomSideActive = newValue;
                case "back" -> backSideActive = newValue;
            }

            PacketDistributor.sendToServer(new ModConfigPacket(
                    ModConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                    menu.blockEntity.getBlockPos(),
                    configType,
                    0,
                    newValue));

            this.init();
        }
    }

    private void adjustOffset(String axis, int delta) {
        ModConfigPacket.ConfigType configType;
        int currentValue;
        int newValue;

        switch (axis) {
            case "downUp" -> {
                configType = ModConfigPacket.ConfigType.COLLECTOR_DOWN_UP_OFFSET;
                currentValue = downUpOffset;
                newValue = Math.max(-10, Math.min(10, currentValue + delta));
                downUpOffset = newValue;
            }
            case "northSouth" -> {
                configType = ModConfigPacket.ConfigType.COLLECTOR_NORTH_SOUTH_OFFSET;
                currentValue = northSouthOffset;
                newValue = Math.max(-10, Math.min(10, currentValue + delta));
                northSouthOffset = newValue;
            }
            case "eastWest" -> {
                configType = ModConfigPacket.ConfigType.COLLECTOR_EAST_WEST_OFFSET;
                currentValue = eastWestOffset;
                newValue = Math.max(-10, Math.min(10, currentValue + delta));
                eastWestOffset = newValue;
            }
            default -> {
                return;
            }
        }

        PacketDistributor.sendToServer(new ModConfigPacket(
                ModConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                menu.blockEntity.getBlockPos(),
                configType,
                newValue,
                false));
    }

    private void toggleWireframe() {
        showWireframe = !showWireframe;
        CollectorWireframeRenderer.toggleWireframe(menu.blockEntity.getBlockPos());
        this.init();
    }

    private void resetAllOffsets() {
        downUpOffset = 0;
        northSouthOffset = 0;
        eastWestOffset = 0;

        PacketDistributor.sendToServer(new ModConfigPacket(
                ModConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                menu.blockEntity.getBlockPos(),
                ModConfigPacket.ConfigType.COLLECTOR_DOWN_UP_OFFSET,
                0,
                false));

        PacketDistributor.sendToServer(new ModConfigPacket(
                ModConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                menu.blockEntity.getBlockPos(),
                ModConfigPacket.ConfigType.COLLECTOR_NORTH_SOUTH_OFFSET,
                0,
                false));

        PacketDistributor.sendToServer(new ModConfigPacket(
                ModConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                menu.blockEntity.getBlockPos(),
                ModConfigPacket.ConfigType.COLLECTOR_EAST_WEST_OFFSET,
                0,
                false));
    }

    private void withdrawXP() {
        try {
            int levels = Integer.parseInt(xpInputField.getValue());
            if (levels > 0) {
                int xpAmount = levelToXp(levels);
                if (xpAmount <= storedXP) {
                    PacketDistributor.sendToServer(new CollectorXpPacket(
                            menu.blockEntity.getBlockPos(), CollectorXpPacket.XpAction.WITHDRAW, xpAmount));

                    storedXP -= xpAmount;
                    xpInputField.setValue("0");
                }
            }
        } catch (NumberFormatException e) {
            xpInputField.setValue("0");
        }
    }

    private void depositXP() {
        try {
            int levels = Integer.parseInt(xpInputField.getValue());
            if (levels > 0) {
                int xpAmount = levelToXp(levels);
                PacketDistributor.sendToServer(new CollectorXpPacket(
                        menu.blockEntity.getBlockPos(), CollectorXpPacket.XpAction.DEPOSIT, xpAmount));

                storedXP = Math.min(maxStoredXP, storedXP + xpAmount);
                xpInputField.setValue("0");
            }
        } catch (NumberFormatException e) {
            xpInputField.setValue("0");
        }
    }

    private void withdrawAllXP() {
        if (storedXP > 0) {
            PacketDistributor.sendToServer(new CollectorXpPacket(
                    menu.blockEntity.getBlockPos(), CollectorXpPacket.XpAction.WITHDRAW, storedXP));

            storedXP = 0;
            xpInputField.setValue("0");
        }
    }

    private void depositAllXP() {
        int playerXP = minecraft.player.totalExperience;
        if (playerXP > 0) {
            PacketDistributor.sendToServer(new CollectorXpPacket(
                    menu.blockEntity.getBlockPos(), CollectorXpPacket.XpAction.DEPOSIT, playerXP));

            storedXP = Math.min(maxStoredXP, storedXP + playerXP);
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

        renderOffsetValues(guiGraphics, x, y);
        renderXPCollectionToggle(guiGraphics, x, y);
        renderXPDisplay(guiGraphics, x, y);
    }

    private void renderOffsetValues(GuiGraphics guiGraphics, int x, int y) {
        var poseStack = guiGraphics.pose();
        float scale = 0.65f;

        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0f);

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

        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0f);

        guiGraphics.drawString(this.font, Component.translatable("gui.flowtech.collector.offset.down_up").getString(), (int)((x + 190) / scale), (int)((y + 58) / scale), 0x000000, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.flowtech.collector.offset.north_south").getString(), (int)((x + 190) / scale), (int)((y + 80) / scale), 0x000000, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.flowtech.collector.offset.east_west").getString(), (int)((x + 190) / scale), (int)((y + 102) / scale), 0x000000, false);

        poseStack.popPose();
    }

    private void renderXPCollectionToggle(GuiGraphics guiGraphics, int x, int y) {
        ResourceLocation toggleHandle = ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "toggle_scroller_handle");
        int handleX = xpCollectionEnabled ? x + 125 : x + 117;
        int handleY = y + 109;
        guiGraphics.blitSprite(toggleHandle, handleX, handleY, 6, 10);
    }

    private void renderXPDisplay(GuiGraphics guiGraphics, int x, int y) {
        var poseStack = guiGraphics.pose();
        float scale = 0.65f;

        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0f);

        int storedLevels = xpToLevel(storedXP);

        String xpDisplayText = String.format(Component.translatable("gui.flowtech.collector.xp_levels_stored").getString(), storedLevels);

        guiGraphics.drawString(this.font, xpDisplayText,
                (int)((x + 8) / scale),
                (int)((y + 125) / scale),
                0x000000, false);

        poseStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (mouseX >= x + 118 && mouseX <= x + 134 && mouseY >= y + 110 && mouseY <= y + 118) {
            xpCollectionEnabled = !xpCollectionEnabled;

            PacketDistributor.sendToServer(new ModConfigPacket(
                    ModConfigPacket.ConfigTarget.COLLECTOR_BLOCK,
                    menu.blockEntity.getBlockPos(),
                    ModConfigPacket.ConfigType.COLLECTOR_XP_COLLECTION_TOGGLE,
                    0,
                    xpCollectionEnabled));

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

        if (mouseX >= x + 117 && mouseX <= x + 131 && mouseY >= y + 110 && mouseY <= y + 120) {
            Component tooltipText = xpCollectionEnabled ?
                    Component.translatable("tooltip.flowtech.collector.xp_collection.enabled") :
                    Component.translatable("tooltip.flowtech.collector.xp_collection.disabled");
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }

        float scale = 0.65f;
        int scaledX = (int)((x + 8) / scale);
        int scaledY = (int)((y + 125) / scale);
        int displayWidth = this.font.width(getXpDisplayText()) * (int)(scale * 100) / 100;
        int displayHeight = (int)(this.font.lineHeight * scale);

        int actualX = x + 8;
        int actualY = y + 125;
        int actualWidth = (int)(displayWidth * scale);
        int actualHeight = displayHeight;

        if (mouseX >= actualX && mouseX <= actualX + actualWidth &&
                mouseY >= actualY && mouseY <= actualY + actualHeight) {

            Component tooltipText = Component.translatable("tooltip.flowtech.collector.xp_display",
                    String.format("%,d", storedXP));
            guiGraphics.renderTooltip(this.font, tooltipText, mouseX, mouseY);
        }

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

    private String getXpDisplayText() {
        int storedLevels = xpToLevel(storedXP);
        return String.format("Levels Stored: %,d", storedLevels);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        syncFromBlockEntity();
    }
}