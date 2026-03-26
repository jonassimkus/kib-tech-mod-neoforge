package com.joniski.kibtech.menus.custom;

import com.joniski.kibtech.block.ModBlocks;
import com.joniski.kibtech.block.custom.AdvancedSolarPanelEntity;
import com.joniski.kibtech.block.custom.SolarPanelEntity;
import com.joniski.kibtech.menus.ModMenus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AdvancedSolarPanelMenu extends AbstractContainerMenu{

    public final AdvancedSolarPanelEntity blockEntity;
    private final Level level;

    public AdvancedSolarPanelMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public AdvancedSolarPanelMenu(int containerId, Inventory inventory, BlockEntity be){
        super(ModMenus.ADVANCED_SOLAR_PANEL_MENU.get(), containerId);

        blockEntity = (AdvancedSolarPanelEntity)be;
        this.level = inventory.player.level();
        
        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    }


    public AdvancedSolarPanelMenu(int containerId, Inventory inventory){
        super(ModMenus.ADVANCED_SOLAR_PANEL_MENU.get(), containerId);
        this.blockEntity = null;

        this.level = inventory.player.level();
        
        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    }

    @Override
    public ItemStack quickMoveStack(Player arg0, int arg1) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player arg0) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), arg0, ModBlocks.ADVANCED_SOLAR_PANEL.get());
    }

    private void addPlayerInventory(Inventory playerInventory){
        for (int i = 0; i < 3; ++i){
            for (int v = 0; v < 9; ++v){
                this.addSlot(new Slot(playerInventory, v + i * 9 + 9, 8 + v * 18, 84 + i * 18));
            }
        }
    }
    
    private void addPlayerHotbar(Inventory playerInventory){
        for (int i = 0; i < 9; ++i){
            this.addSlot(new Slot(playerInventory, i, 8+i *18, 142));
        }
    }
}
