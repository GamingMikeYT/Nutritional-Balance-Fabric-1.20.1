package com.dannyandson.nutritionalbalance.setup;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ClientBinding {

    public static boolean wheelEvent(double scrollDeltaY) {

        if (scrollDeltaY == 0) {
            return false;
        }

        PlayerEntity player = MinecraftClient.getInstance().player;

        if (
                player != null
                        && player.isSneaking()
                        && player.getMainHandStack().getItem()
                        instanceof LunchBoxItem lunchBoxItem
        ) {
            ItemStack lunchBoxStack = player.getMainHandStack();

            Integer activeSlot =
                    lunchBoxItem.getActiveFoodItemSlot(lunchBoxStack);

            Integer maxSlot = null;

            for (
                    int i = 0;
                    i < Config.LUNCHBOX_SLOT_COUNT.get();
                    i++
            ) {
                ItemStack stack =
                        lunchBoxItem.getItemStack(
                                lunchBoxStack,
                                i
                        );

                if (
                        stack != null
                                && stack != ItemStack.EMPTY
                ) {
                    maxSlot = i;
                }
            }

            if (maxSlot != null) {

                boolean reverse = scrollDeltaY < 0;

                ItemStack activeStack;

                if (activeSlot == null) {

                    activeStack =
                            lunchBoxItem.getItemStack(
                                    lunchBoxStack,
                                    reverse ? maxSlot : 0
                            );

                } else {

                    activeSlot += reverse ? -1 : 1;

                    if (activeSlot > maxSlot) {
                        activeSlot = 0;
                    }

                    if (activeSlot < 0) {
                        activeSlot = maxSlot;
                    }

                    activeStack =
                            lunchBoxItem.getItemStack(
                                    lunchBoxStack,
                                    activeSlot
                            );
                }

                if (activeStack != null) {
                    lunchBoxItem.setActiveFood(
                            lunchBoxStack,
                            activeStack,
                            true
                    );
                }
            }

            return true;
        }

        return false;
    }
}