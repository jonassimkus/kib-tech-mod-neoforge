package com.joniski.kibtech.block.custom;

import java.util.List;

import com.joniski.kibtech.block.ModBlockEntity;
import com.joniski.kibtech.component.ModDataComponents;
import com.joniski.kibtech.component.PowerRecord;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AdvancedSolarPanel extends BaseEntityBlock{

    public static final MapCodec<AdvancedSolarPanel> CODEC = simpleCodec(AdvancedSolarPanel::new);

    public static final VoxelShape shape = box(0,0,0,16,8,16);

    public AdvancedSolarPanel(Properties properties) {
        super(properties);
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos arg0, BlockState arg1) {
        return new AdvancedSolarPanelEntity(arg0, arg1);
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
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return shape;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        level.updateNeighbourForOutputSignal(pos, this);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        

        if (!level.isClientSide){
            if (level.getBlockEntity(pos) instanceof AdvancedSolarPanelEntity solarPanelEntity){
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(solarPanelEntity, Component.literal("Advanced Solar Panel")), pos);
                return ItemInteractionResult.SUCCESS;
            }
        }


        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(Component.literal("Generates power with sunlight"));
        tooltipComponents.add(Component.literal("§7" + 30 + ".0 §dKE/t"));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
            
        if (level.isClientSide()){
            return null;
        }


        return createTickerHelper(blockEntityType, ModBlockEntity.ADVANCED_SOLAR_PANEL_BE.get(), 
                (levels, blockPos, blockState, blockEntity) -> blockEntity.tick(levels, blockPos, blockState));
    }

}
