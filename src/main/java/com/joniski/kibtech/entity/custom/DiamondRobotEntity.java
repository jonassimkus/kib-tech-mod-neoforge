package com.joniski.kibtech.entity.custom;
import com.joniski.kibtech.item.ModItems;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class DiamondRobotEntity extends RobotEntity{

    public DiamondRobotEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        maxToolTier = Tiers.DIAMOND;
        moveSpeed = 1.3f;
        maxArea = 13;
        dropItem = ModItems.DIAMOND_ROBOT_ITEM.asItem();
        inventory.setSize(8);
    }
}
    