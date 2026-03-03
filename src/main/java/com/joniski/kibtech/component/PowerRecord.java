package com.joniski.kibtech.component;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PowerRecord(int power) {

    public static final Codec<PowerRecord> BASIC_CODEC = RecordCodecBuilder.create(instance ->
    instance.group(
        Codec.INT.fieldOf("power").forGetter(PowerRecord::power)
    ).apply(instance, PowerRecord::new)
    );
    public static final StreamCodec<ByteBuf, PowerRecord> BASIC_STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, PowerRecord::power,
        PowerRecord::new
    );

    // Unit stream codec if nothing should be sent across the network
    public static final StreamCodec<ByteBuf, PowerRecord> UNIT_STREAM_CODEC = StreamCodec.unit(new PowerRecord(0));



}

