package com.joniski.kibtech.block.custom;


import javax.annotation.Nullable;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.block.ModBlockEntity;
import com.joniski.kibtech.menus.custom.SolarPanelMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

@EventBusSubscriber(modid =  KibTech.MODID)
public class SolarPanelEntity extends BlockEntity implements MenuProvider{

    private EnergyStorage energyStorage;
    private IEnergyStorage downwardInterface;

    public SolarPanelEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntity.SOLAR_PANEL_BE.get(), pos, blockState);

        energyStorage = new EnergyStorage(1000);
        // Make sure that the solar panel can only give power from the bottom interface, and they cant recieve any.
        downwardInterface = new IEnergyStorage() {

            @Override
            public boolean canExtract() {
                return energyStorage.canExtract();
            }

            @Override
            public boolean canReceive() {
                return false;
            }

            @Override
            public int extractEnergy(int arg0, boolean arg1) {
                return energyStorage.extractEnergy(arg0, arg1);
            }

            @Override
            public int getEnergyStored() {
                return energyStorage.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return energyStorage.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int arg0, boolean arg1) {
                return 0;
            }
            
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("solar_panel.storage", energyStorage.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.get("solar_panel.storage") != null){
            energyStorage.deserializeNBT(registries, tag.get("solar_panel.storage"));
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int arg0, Inventory arg1, Player arg2) {
        return new SolarPanelMenu(arg0, arg1, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.kibtech.solar_panel");
    }


    public static IEnergyStorage getCapabilities(SolarPanelEntity solarPanelEntity, Direction direction){
        // Cool stuff lets this mod work with other electricity mods :)
        if (direction == Direction.DOWN){
            return solarPanelEntity.downwardInterface;
        }

        return null;
    }

    public void tick(Level level, BlockPos pos, BlockState state){
        setChanged();
        energyStorage.receiveEnergy(3, false);

        IEnergyStorage otherStorage =  level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.below(), Direction.UP);
        if (otherStorage != null){
            int energyStolen = energyStorage.extractEnergy(3, true);
            int energySent = otherStorage.receiveEnergy(energyStolen, false);
            energyStorage.extractEnergy(energySent, false);
        }
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @SubscribeEvent
    public static void onCapabilitiesRegister(final RegisterCapabilitiesEvent event){
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntity.SOLAR_PANEL_BE.get(), SolarPanelEntity::getCapabilities);
    }
    

    public EnergyStorage getEnergyStorage(){
        return energyStorage;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
