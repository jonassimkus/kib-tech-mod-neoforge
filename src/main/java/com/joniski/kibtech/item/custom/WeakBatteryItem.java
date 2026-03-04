package com.joniski.kibtech.item.custom;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.joniski.kibtech.block.ModBlocks;
import com.joniski.kibtech.block.custom.BatteryCharger;
import com.joniski.kibtech.component.ModDataComponents;
import com.joniski.kibtech.component.PowerRecord;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class WeakBatteryItem extends Item {

    private int maxPower = 600;

    public WeakBatteryItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {

        PowerRecord power = stack.get(ModDataComponents.POWER_COMPONENT);

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if (power == null){
            tooltipComponents.add(Component.translatable("§90" + "§7/§9" + maxPower + " §dKE"));
        }else{
            tooltipComponents.add(Component.translatable("§9" + power.power() + "§7/§9" + maxPower + " §dKE"));
        }
    }

    public boolean isFull(ItemStack stack){
        PowerRecord power = stack.get(ModDataComponents.POWER_COMPONENT);

        if (power == null){
            return false;
        }

        return power.power() >= maxPower;
    }

    public int charge(ItemStack stack, int amount){
        PowerRecord power = stack.get(ModDataComponents.POWER_COMPONENT);
        int powerCurrent = 0;

        if (power != null){
            powerCurrent = power.power();
        }

        powerCurrent += amount;
        int excess = 0;
        if (powerCurrent >= maxPower){
            excess = powerCurrent - maxPower;
            powerCurrent = maxPower;
        }

        stack.set(ModDataComponents.POWER_COMPONENT, new PowerRecord(powerCurrent));

        return amount - excess;
    }


    
}
