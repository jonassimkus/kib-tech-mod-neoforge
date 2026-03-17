package com.joniski.kibtech.item;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.block.ModBlocks;
import com.joniski.kibtech.entity.ModEntities;
import com.joniski.kibtech.entity.custom.RobotEntity;
import com.joniski.kibtech.item.custom.BatteryItem;
import com.joniski.kibtech.item.custom.RobotItem;
import com.joniski.kibtech.item.custom.RobotWandItem;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KibTech.MODID);

        public static final DeferredItem<Item> WEAK_BATTERY = ITEMS.register("weak_battery", () -> new BatteryItem(new Item.Properties().stacksTo(1), 3250));
        public static final DeferredItem<Item> STRONG_BATTERY = ITEMS.register("strong_battery", () -> new BatteryItem(new Item.Properties().stacksTo(1), 55500));
        public static final DeferredItem<Item> COPPER_ROBOT_ITEM = ITEMS.register("copper_robot_item", () -> new RobotItem(new Item.Properties().stacksTo(1), ModEntities.COPPER_ROBOT.get()));
        public static final DeferredItem<Item> NETHERITE_ROBOT_ITEM = ITEMS.register("netherite_robot_item", () -> new RobotItem(new Item.Properties().stacksTo(1), ModEntities.NETHERITE_ROBOT.get()));
        public static final DeferredItem<Item> IRON_ROBOT_ITEM = ITEMS.register("iron_robot_item", () -> new RobotItem(new Item.Properties().stacksTo(1), ModEntities.IRON_ROBOT.get()));
        public static final DeferredItem<Item> DIAMOND_ROBOT_ITEM = ITEMS.register("diamond_robot_item", () -> new RobotItem(new Item.Properties().stacksTo(1), ModEntities.DIAMOND_ROBOT.get()));
        public static final DeferredItem<Item> CHIP_ITEM = ITEMS.register("chip_item", () -> new Item(new Item.Properties().stacksTo(64)));
        public static final DeferredItem<Item> PLASTIC_ITEM = ITEMS.register("plastic_item", () -> new Item(new Item.Properties().stacksTo(64)));
        public static final DeferredItem<Item> ADVANCED_CHIP_ITEM = ITEMS.register("advanced_chip_item", () -> new Item(new Item.Properties().stacksTo(64)));
        public static final DeferredItem<Item> ROBOT_WAND_ITEM = ITEMS.register("robot_wand_item", () -> new RobotWandItem(new Item.Properties().stacksTo(1)));

        public static void register(IEventBus modEventBus){
            ITEMS.register(modEventBus);
        }
}
