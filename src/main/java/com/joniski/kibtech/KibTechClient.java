package com.joniski.kibtech;

import com.joniski.kibtech.entity.ModEntities;
import com.joniski.kibtech.entity.client.DiamondRobotModel;
import com.joniski.kibtech.entity.client.DiamondRobotRenderer;
import com.joniski.kibtech.entity.client.IronRobotModel;
import com.joniski.kibtech.entity.client.IronRobotRenderer;
import com.joniski.kibtech.entity.client.NetheriteRobotModel;
import com.joniski.kibtech.entity.client.NetheriteRobotRenderer;
import com.joniski.kibtech.entity.client.CopperRobotModel;
import com.joniski.kibtech.entity.client.CopperRobotRenderer;
import com.joniski.kibtech.entity.custom.DiamondRobotEntity;
import com.joniski.kibtech.entity.custom.RobotEntity;
import com.joniski.kibtech.item.ModItems;
import com.joniski.kibtech.menus.ModMenus;
import com.joniski.kibtech.menus.custom.AdvancedSolarPanelScreen;
import com.joniski.kibtech.menus.custom.BatteryChargerScreen;
import com.joniski.kibtech.menus.custom.RobotScreen;
import com.joniski.kibtech.menus.custom.RobotStationScreen;
import com.joniski.kibtech.menus.custom.SolarPanelScreen;
import com.joniski.kibtech.packets.RobotFollowerPacket;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handlers.ClientPayloadHandler;
import net.neoforged.neoforge.network.handlers.ServerPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(value = KibTech.MODID, dist = Dist.CLIENT)

@EventBusSubscriber(modid = KibTech.MODID, value = Dist.CLIENT)
public class KibTechClient {
    public KibTechClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.COPPER_ROBOT.get(), CopperRobotRenderer::new);
        EntityRenderers.register(ModEntities.NETHERITE_ROBOT.get(), NetheriteRobotRenderer::new);
        EntityRenderers.register(ModEntities.IRON_ROBOT.get(), IronRobotRenderer::new);
        EntityRenderers.register(ModEntities.DIAMOND_ROBOT.get(), DiamondRobotRenderer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event){
        event.register(ModMenus.SOLAR_PANEL_MENU.get(), SolarPanelScreen::new);
        event.register(ModMenus.ADVANCED_SOLAR_PANEL_MENU.get(), AdvancedSolarPanelScreen::new);
        event.register(ModMenus.BATTERY_CHARGER_MENU.get(), BatteryChargerScreen::new);
        event.register(ModMenus.ROBOT_MENU.get(), RobotScreen::new);
        event.register(ModMenus.ROBOT_STATION_MENU.get(), RobotStationScreen::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(CopperRobotModel.LAYER_LOCATION, CopperRobotModel::createBodyLayer);
        event.registerLayerDefinition(NetheriteRobotModel.LAYER_LOCATION, NetheriteRobotModel::createBodyLayer);
        event.registerLayerDefinition(IronRobotModel.LAYER_LOCATION, IronRobotModel::createBodyLayer);
        event.registerLayerDefinition(DiamondRobotModel.LAYER_LOCATION, DiamondRobotModel::createBodyLayer);
    }
 
}

