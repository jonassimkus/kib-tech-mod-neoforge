package com.joniski.kibtech.block.custom;

import com.joniski.kibtech.block.ModBlockEntity;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
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
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        // TODO ADD BREAKING
        return;
    }
    
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (!level.isClientSide){
            if (level.getBlockEntity(pos) instanceof BatteryChargerEntity batteryChargerEntity){
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(batteryChargerEntity, Component.literal("Battery Charger")), pos);
                return ItemInteractionResult.SUCCESS;
            }
        }


        return ItemInteractionResult.SUCCESS;
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
