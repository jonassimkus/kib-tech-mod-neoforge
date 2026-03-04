package com.joniski.kibtech;

import com.joniski.kibtech.menus.ModMenus;
import com.joniski.kibtech.menus.custom.BatteryChargerScreen;
import com.joniski.kibtech.menus.custom.SolarPanelScreen;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = KibTech.MODID, dist = Dist.CLIENT)

@EventBusSubscriber(modid = KibTech.MODID, value = Dist.CLIENT)
public class KibTechClient {
    public KibTechClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event){
        event.register(ModMenus.SOLAR_PANEL_MENU.get(), SolarPanelScreen::new);
        event.register(ModMenus.BATTERY_CHARGER_MENU.get(), BatteryChargerScreen::new);
    }
}

