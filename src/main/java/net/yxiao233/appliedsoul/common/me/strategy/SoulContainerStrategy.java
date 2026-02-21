package net.yxiao233.appliedsoul.common.me.strategy;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.yxiao233.appliedsoul.common.key.SoulKey;

import javax.annotation.Nullable;

public class SoulContainerStrategy implements ContainerItemStrategy<SoulKey, ItemStack> {

    @Override
    public @Nullable GenericStack getContainedStack(ItemStack itemStack) {
        if (BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getNamespace().equals("industrialforegoingsouls")){
            return new GenericStack(SoulKey.INSTANCE, 5);
        }
        return null;
    }

    @Override
    public @Nullable ItemStack findCarriedContext(Player player, AbstractContainerMenu abstractContainerMenu) {
        return abstractContainerMenu.getCarried();
    }

    @Override
    public long extract(ItemStack stack, SoulKey soulKey, long l, Actionable actionable) {
        return 0;
    }

    @Override
    public long insert(ItemStack stack, SoulKey soulKey, long l, Actionable actionable) {
        return 0;
    }

    @Override
    public void playFillSound(Player player, SoulKey soulKey) {

    }

    @Override
    public void playEmptySound(Player player, SoulKey soulKey) {

    }

    @Override
    public @Nullable GenericStack getExtractableContent(ItemStack stack) {
        return null;
    }
}