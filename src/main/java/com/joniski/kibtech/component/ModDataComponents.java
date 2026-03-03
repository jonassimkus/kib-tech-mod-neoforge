package com.joniski.kibtech.component;

import java.util.function.UnaryOperator;

import com.joniski.kibtech.KibTech;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;



public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES =DeferredRegister.DataComponents.createDataComponents(Registries.DATA_COMPONENT_TYPE, KibTech.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PowerRecord>> POWER_COMPONENT = register("power", builder -> builder.persistent(PowerRecord.BASIC_CODEC));


    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, 
            UnaryOperator<DataComponentType.Builder<T>> buildOperator){
 
                
        return DATA_COMPONENT_TYPES.register(name, () -> buildOperator.apply(DataComponentType.builder()).build());
    }


    public static void register(IEventBus modEventBus){
        DATA_COMPONENT_TYPES.register(modEventBus);
    }
}
