package com.joniski.kibtech.item.custom;

import java.util.ArrayList;
import java.util.List;

import com.jcraft.jorbis.Block;
import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.entity.custom.RobotEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.context.UseOnContext;

public class RobotWandItem extends Item{

    public RobotEntity setRobot = null;
    private BlockPos startPos = null;
    private BlockPos endPos = null;

    public RobotWandItem(Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext context) {
        if (setRobot == null){
            return InteractionResult.FAIL;
        }

        if(!context.getLevel().isClientSide()){
            if(context.getPlayer().getCooldowns().isOnCooldown(this)){
                return InteractionResult.FAIL;
            }
            context.getPlayer().getCooldowns().addCooldown(this, 20);

            if (startPos != null && endPos == null){
                endPos = context.getClickedPos();

                List<BlockPos> pos = getLowestAndHigheBlockPos(startPos, endPos);

                if (pos.get(1).getX() - pos.get(0).getX() > setRobot.maxArea
                    || pos.get(1).getY() - pos.get(0).getY() > setRobot.maxArea 
                    || pos.get(1).getZ() - pos.get(0).getZ() > setRobot.maxArea){
                    context.getPlayer().sendSystemMessage((Component.literal("Robot area is too big! max area for this robot is: " + setRobot.maxArea + " blocks.")));
                    setRobot = null;
                    startPos = null;
                    endPos = null;
                    return InteractionResult.SUCCESS;
                }


                context.getPlayer().sendSystemMessage((Component.literal("Second boundry: " + endPos.toShortString())));

                setRobot.searchStart = pos.get(0);
                setRobot.searchEnd = pos.get(1);
                setRobot.setFollowEntity(null);
                setRobot.stopMoving();

                setRobot = null;
                startPos = null;
                endPos = null;
                return InteractionResult.SUCCESS;
            }

            if (startPos == null){
                startPos = context.getClickedPos();
                context.getPlayer().sendSystemMessage((Component.literal("First boundry: " + startPos.toShortString())));
            }
        }

        return InteractionResult.SUCCESS;
    }

    public List<BlockPos> getLowestAndHigheBlockPos(BlockPos first, BlockPos second){
        List<BlockPos> pos = new ArrayList<BlockPos>();

        int highX = 0;
        int highY = 0;
        int highZ = 0;
        int lowX = 0;
        int lowY = 0;
        int lowZ = 0;

        highX = Math.max(first.getX(), second.getX());
        highY = Math.max(first.getY(), second.getY());
        highZ = Math.max(first.getZ(), second.getZ());

        lowX = Math.min(first.getX(), second.getX());
        lowY = Math.min(first.getY(), second.getY());
        lowZ = Math.min(first.getZ(), second.getZ());

        pos.add(new BlockPos(lowX, lowY, lowZ));
        pos.add(new BlockPos(highX+1, highY+1, highZ+1));

        return pos;
    }

    public void setRobotTarget(RobotEntity entity){
        setRobot = entity;
        startPos = null;
        endPos = null;
    }
}
