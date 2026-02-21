package net.yxiao233.appliedsoul.common.key;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;

public class SoulAEKeyType extends AEKeyType {
    public static final SoulAEKeyType TYPE = new SoulAEKeyType();

    public SoulAEKeyType() {
        super(SoulKey.RL, SoulKey.class, Component.translatable("aekey.soulkey.description"));
    }
    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ITEM)) {
            AEKeyTypes.register(TYPE);
        }
    }

    @Override
    public @Nullable AEKey readFromPacket(FriendlyByteBuf friendlyByteBuf) {
        return SoulKey.INSTANCE;
    }

    @Override
    public @Nullable AEKey loadKeyFromTag(CompoundTag compoundTag) {
        return SoulKey.INSTANCE;
    }

    @Override
    public int getAmountPerByte() {
        return 1;
    }

    @Override
    public @Nullable String getUnitSymbol() {
        return "s";
    }
}
