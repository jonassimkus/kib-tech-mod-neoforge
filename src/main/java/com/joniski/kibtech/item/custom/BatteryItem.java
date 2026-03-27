package com.joniski.kibtech.item.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.block.ModBlocks;
import com.joniski.kibtech.block.custom.BatteryCharger;
import com.joniski.kibtech.component.ModDataComponents;
import com.joniski.kibtech.component.PowerRecord;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BatteryItem extends Item {

    private int maxPower = 3240;

    public BatteryItem(Properties properties, int maxPower) {
        super(properties);
        this.maxPower = maxPower;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {

        PowerRecord power = stack.get(ModDataComponents.POWER_COMPONENT);

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if (power == null){
            tooltipComponents.add(Component.literal("§70" + "/" + maxPower + " §dKE"));
        }else{
            tooltipComponents.add(Component.literal("§7" + power.power() + "/" + maxPower + " §dKE"));
        }
    }

    public boolean isFull(ItemStack stack){
        PowerRecord power = stack.get(ModDataComponents.POWER_COMPONENT);

        if (power == null){
            return false;
        }

        // Return true if almost full
        return power.power() >= maxPower-5;
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

    public int getPower(ItemStack stack){
        PowerRecord power = stack.get(ModDataComponents.POWER_COMPONENT);
        int powerCurrent = 0;

        if (power != null){
            powerCurrent = power.power();
        }

        return powerCurrent;
    }

    public int getMaxPower(){
        return maxPower;
    }

    
}
