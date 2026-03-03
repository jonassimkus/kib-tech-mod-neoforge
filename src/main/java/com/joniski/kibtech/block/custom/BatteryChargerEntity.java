package com.joniski.kibtech.block.custom;

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
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BatteryChargerEntity extends BlockEntity implements IEnergyStorage {

    public final ItemStackHandler inventory = new ItemStackHandler(1){
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        };


        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        };
    };

    private int power = 0;
    private int maxPower = 1000;

    public BatteryChargerEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntity.BATTERY_CHARGER_BE.get(), pos, blockState);
    }


    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("battery_charger.power", power);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        power = tag.getInt("battery_charger.power");
    }


    public void tick(Level level, BlockPos pos, BlockState state){
        KibTech.LOGGER.debug("" + power);

        if (inventory.getStackInSlot(0).isEmpty()){
            return;
        }
        WeakBatteryItem b = (WeakBatteryItem) inventory.getStackInSlot(0).getItem();

        if (b.isFull(inventory.getStackInSlot(0))){
            return;
        }

        if(power >= 20){
            int excess = b.charge(inventory.getStackInSlot(0), 20);
            power -= 20;
            power += excess;
        }
        else if (power >= 0){
            int excess = b.charge(inventory.getStackInSlot(0), power);
            power -= power;
            power += excess;
        }

    }


    @Override
    public boolean canExtract() {
        return false;
    }


    @Override
    public boolean canReceive() {
        return true;
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
