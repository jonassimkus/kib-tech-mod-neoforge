package com.joniski.kibtech.packets;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.entity.ModEntities;
import com.joniski.kibtech.entity.custom.RobotEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = KibTech.MODID)
public class ModNetworking {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // Packet to let robot follow player from GUI button
        registrar.playToServer(
            RobotFollowerPacket.TYPE, RobotFollowerPacket.STREAM_CODEC,
            (payload, context) -> {
                context.enqueueWork(() -> {
                    Player player = context.player();
                    if (player == null) {
                        return;
                    }

                    if (!(player.level() instanceof ServerLevel serverLevel)) return;

                    Entity entity = serverLevel.getEntity(payload.robotId());
                    if (!(entity instanceof RobotEntity)){
                        return;
                    }

                    RobotEntity robot = (RobotEntity)entity;

                    if (player.distanceToSqr(robot) > 100.0D) {
                        return;
                    }

                    Entity follower = robot.getFollowEntity();
                    if (follower != null && player.getUUID().equals(follower.getUUID())){
                        robot.setFollowEntity(null);
                    }else{
                        robot.setFollowEntity(player);
                    }
                });
            }
        );
    }

    // TODO: MOVE TO ANOTHER CLASS 
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(ModEntities.COPPER_ROBOT.get(), RobotEntity.createAttributes().build());
        event.put(ModEntities.NETHERITE_ROBOT.get(), RobotEntity.createAttributes().build());
        event.put(ModEntities.IRON_ROBOT.get(), RobotEntity.createAttributes().build());
        event.put(ModEntities.DIAMOND_ROBOT.get(), RobotEntity.createAttributes().build());
    }

}
