package com.joniski.kibtech.block.custom;

import javax.annotation.Nullable;

import org.jetbrains.annotations.Debug;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.block.ModBlockEntity;
import com.joniski.kibtech.item.custom.WeakBatteryItem;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BatteryCharger extends BaseEntityBlock{

    public static final MapCodec<BatteryCharger> CODEC = simpleCodec(BatteryCharger::new);

    public BatteryCharger(Properties properties) {
        super(properties);
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos arg0, BlockState arg1) {
        return new BatteryChargerEntity(arg0, arg1);
    }


    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        if(level.getBlockEntity(pos) instanceof BatteryChargerEntity batteryChargerEntity){
            if (stack.getItem() instanceof WeakBatteryItem){
                if(batteryChargerEntity.inventory.getStackInSlot(0).isEmpty()){
                    batteryChargerEntity.inventory.setStackInSlot(0, stack.copy());
                    stack.shrink(1);        
            
                    
                    return ItemInteractionResult.SUCCESS;
                }
            }else if (stack.isEmpty()){
                if(!batteryChargerEntity.inventory.getStackInSlot(0).isEmpty()){
                    player.setItemInHand(hand, batteryChargerEntity.inventory.getStackInSlot(0).copy());
                    batteryChargerEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);
            
                    return ItemInteractionResult.SUCCESS;
                }
            }

        }

        return ItemInteractionResult.FAIL;
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
            
        if (level.isClientSide()){
            return null;
        }


        return createTickerHelper(blockEntityType, ModBlockEntity.BATTERY_CHARGER_BE.get(), 
                (levels, blockPos, blockState, blockEntity) -> blockEntity.tick(levels, blockPos, blockState));
    }

}
