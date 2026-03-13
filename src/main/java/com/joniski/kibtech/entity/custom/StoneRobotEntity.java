package com.joniski.kibtech.entity.custom;

import com.joniski.kibtech.item.ModItems;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class StoneRobotEntity extends RobotEntity{

    public StoneRobotEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        maxToolTier = Tiers.STONE;
        moveSpeed = 0.9f;
        maxArea = 7;
        dropItem = ModItems.STONE_ROBOT_ITEM.asItem();
        inventory.setSize(5);
    }
}
    