package com.joniski.kibtech.item;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.block.ModBlocks;
import com.joniski.kibtech.item.custom.WeakBatteryItem;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KibTech.MODID);

        public static final DeferredItem<Item> WEAK_BATTERY = ITEMS.register("weak_battery", () -> new WeakBatteryItem(new Item.Properties().stacksTo(1)));

        public static void register(IEventBus modEventBus){
            ITEMS.register(modEventBus);
        }
}
