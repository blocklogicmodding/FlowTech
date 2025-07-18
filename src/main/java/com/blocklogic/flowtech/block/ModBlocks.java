package com.blocklogic.flowtech.block;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.block.custom.*;
import com.blocklogic.flowtech.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FlowTech.MODID);

    public static final DeferredBlock<Block> FAST_FLOW_PAD = registerBlock("flow_pad_fast",
            () -> new FastFlowPadBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> FASTER_FLOW_PAD = registerBlock("flow_pad_faster",
            () -> new FasterFlowPadBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> FASTEST_FLOW_PAD = registerBlock("flow_pad_fastest",
            () -> new FastestFlowPadBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> ATTACK_PAD = registerBlock("attack_pad",
            () -> new AttackPadBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> FLOWTECH_COLLECTOR = registerBlock("flowtech_collector",
            () -> new FlowtechCollectorBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> FLOWTECH_CONTROLLER = registerBlock("flowtech_controller",
            () -> new FlowtechControllerBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> UMBRITE_STONE_ORE_BLOCK = registerBlock("umbrite_stone_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> UMBRITE_DEEPSLATE_ORE_BLOCK = registerBlock("umbrite_deepslate_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> UMBRITE_NETHER_ORE_BLOCK = registerBlock("umbrite_nether_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> UMBRITE_END_ORE_BLOCK = registerBlock("umbrite_end_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> UMBRITE_BLOCK = registerBlock("umbrite_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> RAW_UMBRITE_BLOCK = registerBlock("raw_umbrite_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
            ));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                tooltipComponents.add(Component.translatable("block.flowtech." + name + ".subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        });
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
