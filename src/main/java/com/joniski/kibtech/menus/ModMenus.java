package com.joniski.kibtech.menus;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.menus.custom.BatteryChargerMenu;
import com.joniski.kibtech.menus.custom.SolarPanelMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = 
            DeferredRegister.create(Registries.MENU, KibTech.MODID);


    public static final DeferredHolder<MenuType<?>, MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU =
            registerMenuType("solar_panel_menu", SolarPanelMenu::new);


    public static final DeferredHolder<MenuType<?>, MenuType<BatteryChargerMenu>> BATTERY_CHARGER_MENU =
            registerMenuType("battery_charger_menu", BatteryChargerMenu::new);


    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory){
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus){
        MENUS.register(eventBus);
    }
}
