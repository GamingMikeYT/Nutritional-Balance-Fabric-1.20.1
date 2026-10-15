package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.setup.Registration;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import org.jetbrains.annotations.NotNull;

public class LunchBoxMenu extends ScreenHandler {

    public static @NotNull LunchBoxMenu createMenu(
            int containerId,
            PlayerInventory playerInventory
    ) {
        return createMenu(
                containerId,
                playerInventory,
                LunchBoxContainer.get(
                        Registration.LUNCHBOX_ITEM.getDefaultStack()
                )
        );
    }

    public static LunchBoxMenu createMenu(
            int containerId,
            PlayerInventory playerInventory,
            Inventory inventory
    ) {
        return new LunchBoxMenu(containerId, playerInventory, inventory);
    }

    private Inventory container;
    private PlayerInventory playerInventory;
    private ItemStack lunchBox;

    protected LunchBoxMenu(
            int containerId,
            PlayerInventory playerInventory,
            Inventory container
    ) {
        super(Registration.LUNCHBOX_MENU_TYPE, containerId);

        checkSize(container, Config.LUNCHBOX_SLOT_COUNT.get());

        this.container = container;
        this.playerInventory = playerInventory;
        this.lunchBox = playerInventory.getMainHandStack();

        int leftCol = 47;
        int ySize = 252;

        container.onOpen(playerInventory.player);

        for (
                int lunchBoxRow = 0;
                lunchBoxRow < container.size();
                lunchBoxRow++
        ) {
            this.addSlot(
                    new Slot(
                            container,
                            lunchBoxRow,
                            leftCol + lunchBoxRow * 18,
                            ySize - 6 * 18 - 10
                    ) {
                        @Override
                        public void markDirty() {
                            onContentChanged(container);
                            super.markDirty();
                        }

                        @Override
                        public boolean canInsert(ItemStack stack) {
                            return stack.getItem().getFoodComponent() != null
                                    && !(stack.getItem() instanceof LunchBoxItem);
                        }
                    }
            );
        }

        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
                this.addSlot(
                        new Slot(
                                playerInventory,
                                playerInvCol + playerInvRow * 9 + 9,
                                leftCol + playerInvCol * 18,
                                ySize - (4 - playerInvRow) * 18 - 10
                        )
                );
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            this.addSlot(
                    new Slot(
                            playerInventory,
                            hotbarSlot,
                            leftCol + hotbarSlot * 18,
                            ySize - 24
                    )
            );
        }
    }

    @Override
    public @NotNull ItemStack quickMove(
            PlayerEntity player,
            int index
    ) {
        ItemStack itemstack = ItemStack.EMPTY;

        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasStack()) {

            ItemStack itemstack1 = slot.getStack();

            itemstack = itemstack1.copy();

            if (index < Config.LUNCHBOX_SLOT_COUNT.get()) {

                if (
                        !this.insertItem(
                                itemstack1,
                                Config.LUNCHBOX_SLOT_COUNT.get(),
                                this.slots.size(),
                                true
                        )
                ) {
                    return ItemStack.EMPTY;
                }

            } else if (
                    !this.insertItem(
                            itemstack1,
                            0,
                            Config.LUNCHBOX_SLOT_COUNT.get(),
                            false
                    )
            ) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return itemstack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.container.canPlayerUse(player)
                && player.getMainHandStack() == this.lunchBox;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if (this.container instanceof LunchBoxContainer lunchBoxContainer) {
            lunchBoxContainer.save();
        }

        super.onContentChanged(inventory);
    }

    public void setActiveSlot(int slot) {
        if (this.lunchBox.getItem() instanceof LunchBoxItem lunchBoxItem) {

            lunchBoxItem.setActiveFood(
                    this.lunchBox,
                    container.getStack(slot),
                    this.playerInventory.player.getWorld().isClient()
            );
        }
    }

    public ItemStack getLunchBoxItemStack() {
        return this.lunchBox;
    }
}