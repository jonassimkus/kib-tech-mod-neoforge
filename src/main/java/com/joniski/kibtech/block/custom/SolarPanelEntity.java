package com.joniski.kibtech.block.custom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jetbrains.annotations.Debug;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.block.ModBlockEntity;
import com.joniski.kibtech.block.ModBlocks;
import com.joniski.kibtech.item.custom.WeakBatteryItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

public class SolarPanelEntity extends BlockEntity implements IEnergyStorage{

    int count = 0;
    int power = 0;
    int maxPower = 1000;

    public SolarPanelEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntity.SOLAR_PANEL_BE.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("solar_panel.power", power);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        power = tag.getInt("solar_panel.power");
    }

    public void tick(Level level, BlockPos pos, BlockState state){
        receiveEnergy(1, true);

        BlockEntity blockEntity = level.getBlockEntity(pos.below());
        if (blockEntity instanceof IEnergyStorage){
            IEnergyStorage storage = (IEnergyStorage)blockEntity;
            if(storage.canReceive()){
                if(power >= 5){
                    int amount = storage.receiveEnergy(5, true);
                    power -= 5;
                    power += amount;
                }
                else if(power >= 0){
                    int amount = storage.receiveEnergy(power, true);
                    power -= power;
                    power += amount;
                }
            }
        }


        KibTech.LOGGER.debug("" + power);
    }


    @Override
    public boolean canExtract() {
        return true;
    }


    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public int extractEnergy(int arg0, boolean arg1) {
        if (power >= arg0){
            power -= arg0;
            return arg0;
        }

        power = 0;
        return power;
    }


    @Override
    public int getEnergyStored() {
        return power;
    }


    @Override
    public int getMaxEnergyStored() {
        return maxPower;
    }


    @Override
    public int receiveEnergy(int arg0, boolean arg1) {
        power += arg0;
            
        int excess = 0;
        if (power > maxPower){
            excess = power - maxPower;
            power = maxPower;
        }

        return excess;

    }

}
