package com.joniski.kibtech;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.joniski.kibtech.block.ModBlockEntity;
import com.joniski.kibtech.block.ModBlocks;
import com.joniski.kibtech.component.ModDataComponents;
import com.joniski.kibtech.item.ModItems;
import com.joniski.kibtech.menus.ModMenus;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(KibTech.MODID)
public class KibTech {
    public static final String MODID = "kibtech";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    public static final Supplier<CreativeModeTab> KIB_TECH_TAB = CREATIVE_MODE_TABS.register("kibtech", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup." + MODID + ".kibtech"))
        .icon(() -> new ItemStack(ModItems.WEAK_BATTERY.get()))
        .displayItems((params, output) ->{
            output.accept(ModItems.WEAK_BATTERY.get());
            output.accept(ModBlocks.SOLAR_PANEL.get());
            output.accept(ModBlocks.BATTERY_CHARGER.get());
        })
        .build()
    ); 

    public KibTech(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntity.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModMenus.register(modEventBus);

        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
 
    private void commonSetup(FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

}
