package com.joniski.kibtech.entity.custom;

import com.joniski.kibtech.item.ModItems;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class IronRobotEntity extends RobotEntity{

    public IronRobotEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        maxToolTier = Tiers.IRON;
        moveSpeed = 1.1f;
        maxArea = 9;
        dropItem = ModItems.IRON_ROBOT_ITEM.asItem();
        inventory.setSize(6);
    }
}
    