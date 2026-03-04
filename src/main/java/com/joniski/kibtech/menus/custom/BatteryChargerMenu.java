package com.joniski.kibtech.menus.custom;

import com.joniski.kibtech.block.ModBlocks;
import com.joniski.kibtech.block.custom.BatteryChargerEntity;
import com.joniski.kibtech.menus.ModMenus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BatteryChargerMenu extends AbstractContainerMenu{

    public final BatteryChargerEntity blockEntity;
    private final Level level;

    public BatteryChargerMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BatteryChargerMenu(int containerId, Inventory inventory, BlockEntity be){
        super(ModMenus.BATTERY_CHARGER_MENU.get(), containerId);

        blockEntity = (BatteryChargerEntity)be;
        this.level = inventory.player.level();
        
        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 0, 80, 35));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex == 36){
            if (!this.moveItemStackTo(slots.get(slotIndex).getItem(), 0, 36, false)){
                return ItemStack.EMPTY;
            }

            return slots.get(slotIndex).getItem().copy();
        }else{
            if (!this.moveItemStackTo(slots.get(slotIndex).getItem(), 36, 37, false)){
                return ItemStack.EMPTY;
            }
            return slots.get(slotIndex).getItem().copy();
        }
    }

    @Override
    public boolean stillValid(Player arg0) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), arg0, ModBlocks.BATTERY_CHARGER.get());
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
