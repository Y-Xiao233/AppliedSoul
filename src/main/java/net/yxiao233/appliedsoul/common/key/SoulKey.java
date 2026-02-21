package net.yxiao233.appliedsoul.common.key;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.yxiao233.appliedsoul.AppliedSoul;

import java.util.List;

public class SoulKey extends AEKey {
    public static ResourceLocation RL = AppliedSoul.makeId("soulkey");
    public static SoulKey INSTANCE = new SoulKey();
    public static final MapCodec<SoulKey> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(Codec.STRING.fieldOf("uhhh_idk").forGetter(o -> ""))
                    .apply(builder,t -> new SoulKey())
    );
    public static final Codec<SoulKey> CODEC = MAP_CODEC.codec();
    @Override
    public AEKeyType getType() {
        return SoulAEKeyType.TYPE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag() {
        return new CompoundTag();
    }

    @Override
    public Object getPrimaryKey() {
        return this;
    }

    @Override
    public ResourceLocation getId() {
        return RL;
    }

    @Override
    public void writeToPacket(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    protected Component computeDisplayName() {
        return Component.translatable("aekey.soulkey.description");
    }

    @Override
    public void addDrops(long l, List<ItemStack> list, Level level, BlockPos blockPos) {

    }
}
