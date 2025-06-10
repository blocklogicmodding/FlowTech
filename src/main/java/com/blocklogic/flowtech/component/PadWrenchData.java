package com.blocklogic.flowtech.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public record PadWrenchData(
        OperationMode operationMode,
        SelectionMode selectionMode,
        @Nullable BlockPos selectedController,
        @Nullable BlockPos firstMultiPos
) {

    public static final Codec<PadWrenchData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    OperationMode.CODEC.optionalFieldOf("operationMode", OperationMode.ADD).forGetter(PadWrenchData::operationMode),
                    SelectionMode.CODEC.optionalFieldOf("selectionMode", SelectionMode.SINGLE).forGetter(PadWrenchData::selectionMode),
                    BlockPos.CODEC.optionalFieldOf("selectedController").forGetter(data ->
                            java.util.Optional.ofNullable(data.selectedController())),
                    BlockPos.CODEC.optionalFieldOf("firstMultiPos").forGetter(data ->
                            java.util.Optional.ofNullable(data.firstMultiPos()))
            ).apply(instance, (opMode, selMode, controller, multiPos) ->
                    new PadWrenchData(opMode, selMode, controller.orElse(null), multiPos.orElse(null)))
    );

    public static final PadWrenchData DEFAULT = new PadWrenchData(
            OperationMode.ADD,
            SelectionMode.SINGLE,
            null,
            null
    );

    public PadWrenchData withOperationMode(OperationMode operationMode) {
        return new PadWrenchData(operationMode, this.selectionMode, this.selectedController, this.firstMultiPos);
    }

    public PadWrenchData withSelectionMode(SelectionMode selectionMode) {
        return new PadWrenchData(this.operationMode, selectionMode, this.selectedController, this.firstMultiPos);
    }

    public PadWrenchData withSelectedController(@Nullable BlockPos selectedController) {
        return new PadWrenchData(this.operationMode, this.selectionMode, selectedController, this.firstMultiPos);
    }

    public PadWrenchData withFirstMultiPos(@Nullable BlockPos firstMultiPos) {
        return new PadWrenchData(this.operationMode, this.selectionMode, this.selectedController, firstMultiPos);
    }

    public enum OperationMode {
        ADD,
        REMOVE;

        public static final Codec<OperationMode> CODEC = Codec.stringResolver(
                OperationMode::name,
                name -> {
                    try {
                        return OperationMode.valueOf(name.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return ADD;
                    }
                }
        );
    }

    public enum SelectionMode {
        SINGLE,
        MULTI;

        public static final Codec<SelectionMode> CODEC = Codec.stringResolver(
                SelectionMode::name,
                name -> {
                    try {
                        return SelectionMode.valueOf(name.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return SINGLE;
                    }
                }
        );
    }
}