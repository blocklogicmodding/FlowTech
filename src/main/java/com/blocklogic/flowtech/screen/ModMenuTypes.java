package com.blocklogic.flowtech.screen;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.screen.custom.FlowtechCollectorMenu;
import com.blocklogic.flowtech.screen.custom.FlowtechControllerMenu;
import com.blocklogic.flowtech.screen.custom.VoidFilterMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, FlowTech.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<FlowtechControllerMenu>> CONTROLLER_MENU =
            registerMenuType("flowtech_controller_menu", FlowtechControllerMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<FlowtechCollectorMenu>> COLLECTOR_MENU =
            registerMenuType("flowtech_collector_menu", FlowtechCollectorMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<VoidFilterMenu>> VOID_FILTER_MENU =
            registerMenuType("void_filter_menu", VoidFilterMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
