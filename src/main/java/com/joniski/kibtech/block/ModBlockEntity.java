package com.joniski.kibtech.block;

import java.util.function.Supplier;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.block.custom.AdvancedSolarPanelEntity;
import com.joniski.kibtech.block.custom.BatteryChargerEntity;
import com.joniski.kibtech.block.custom.RobotStationEntity;
import com.joniski.kibtech.block.custom.SolarPanelEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, KibTech.MODID);

    public static final Supplier<BlockEntityType<BatteryChargerEntity>> BATTERY_CHARGER_BE =
         BLOCK_ENTITIES.register("battery_charger_be", () -> BlockEntityType.Builder.of(
            BatteryChargerEntity::new,
            ModBlocks.BATTERY_CHARGER.get()).build(null));


    public static final Supplier<BlockEntityType<SolarPanelEntity>> SOLAR_PANEL_BE =
         BLOCK_ENTITIES.register("solar_panel_be", () -> BlockEntityType.Builder.of(
            SolarPanelEntity::new,
            ModBlocks.SOLAR_PANEL.get()).build(null));
    
    public static final Supplier<BlockEntityType<AdvancedSolarPanelEntity>> ADVANCED_SOLAR_PANEL_BE =
         BLOCK_ENTITIES.register("advanced_solar_panel_be", () -> BlockEntityType.Builder.of(
            AdvancedSolarPanelEntity::new,
            ModBlocks.ADVANCED_SOLAR_PANEL.get()).build(null));

    public static final Supplier<BlockEntityType<RobotStationEntity>> ROBOT_STATION_BE =
         BLOCK_ENTITIES.register("robot_station_be", () -> BlockEntityType.Builder.of(
            RobotStationEntity::new,
            ModBlocks.ROBOT_STATION.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
