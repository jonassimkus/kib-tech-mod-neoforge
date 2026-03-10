package com.joniski.kibtech.packets;


import java.util.UUID;

import com.joniski.kibtech.KibTech;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RobotFollowerPacket(int robotId, String playerUUID) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<RobotFollowerPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(KibTech.MODID, "robot_follower_record"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, RobotFollowerPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        RobotFollowerPacket::robotId,
        ByteBufCodecs.STRING_UTF8,
        RobotFollowerPacket::playerUUID,
        RobotFollowerPacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}