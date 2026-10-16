
package com.dannyandson.nutritionalbalance.mixin;

import com.dannyandson.nutritionalbalance.events.EventUseItem;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void nutritionalbalance$onUseItemFinish(
            World world,
            LivingEntity user,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        ItemStack originalStack = (ItemStack) (Object) this;

        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            EventUseItem.onUseItemFinish(originalStack, world, player);
        }
    }
}
