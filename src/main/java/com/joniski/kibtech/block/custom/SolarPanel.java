package com.joniski.kibtech.block.custom;

import com.joniski.kibtech.block.ModBlockEntity;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanel extends BaseEntityBlock{

    public static final MapCodec<SolarPanel> CODEC = simpleCodec(SolarPanel::new);

    public SolarPanel(Properties properties) {
        super(properties);
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos arg0, BlockState arg1) {
        return new SolarPanelEntity(arg0, arg1);
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
            
        if (level.isClientSide()){
            return null;
        }


        return createTickerHelper(blockEntityType, ModBlockEntity.SOLAR_PANEL_BE.get(), 
                (levels, blockPos, blockState, blockEntity) -> blockEntity.tick(levels, blockPos, blockState));
    }

}
