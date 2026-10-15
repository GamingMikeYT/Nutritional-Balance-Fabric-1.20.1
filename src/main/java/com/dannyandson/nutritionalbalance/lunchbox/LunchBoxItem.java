package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.network.LunchBoxActiveItemSync;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LunchBoxItem extends Item {

    public LunchBoxItem() {
        super(new Item.Settings().maxCount(1));
    }

    @Override
    public @Nullable FoodComponent getFoodComponent() {
        return null;
    }

    public @Nullable FoodComponent getFoodProperties(
            ItemStack stack,
            @Nullable LivingEntity entity
    ) {
        if (stack.getItem() instanceof LunchBoxItem) {
            ItemStack activeStack = getActiveFoodItemStack(stack);

            if (activeStack != null)
                return activeStack.getItem().getFoodComponent();

            return null;
        }

        return super.getFoodComponent();
    }

    @Override
    public TypedActionResult<ItemStack> use(
            World world,
            PlayerEntity player,
            Hand hand
    ) {
        ItemStack stack = player.getStackInHand(hand);

        if (stack.getItem() instanceof LunchBoxItem) {

            ItemStack activeStack = getActiveFoodItemStack(stack);

            if (activeStack != null && !player.isSneaking()) {

                FoodComponent activeFoodProperties =
                        activeStack.getItem().getFoodComponent();

                if (
                        !activeStack.isEmpty()
                                && activeFoodProperties != null
                                && !player.getItemCooldownManager()
                                .isCoolingDown(activeStack.getItem())
                                && player.canConsume(activeFoodProperties.isAlwaysEdible())
                ) {
                    player.setCurrentHand(hand);
                    return TypedActionResult.consume(stack);
                } else {
                    return TypedActionResult.fail(stack);
                }

            } else if (
                    !world.isClient
                            && !(player.currentScreenHandler instanceof LunchBoxMenu)
                            && hand == Hand.MAIN_HAND
            ) {
                LunchBoxContainer container = LunchBoxContainer.get(stack);

                if (container != null) {
                    player.openHandledScreen(
                            new SimpleNamedScreenHandlerFactory(
                                    (syncId, playerInventory, playerEntity) ->
                                            new LunchBoxMenu(
                                                    syncId,
                                                    playerInventory,
                                                    container
                                            ),
                                    Text.translatable(this.getTranslationKey(stack))
                            )
                    );
                }
            }

            return TypedActionResult.pass(stack);
        }

        return super.use(world, player, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {

        if (stack.getItem() instanceof LunchBoxItem) {

            if (entity instanceof PlayerEntity) {

                Integer activeSlot = getActiveFoodItemSlot(stack);

                if (activeSlot != null) {

                    LunchBoxContainer container = LunchBoxContainer.get(stack);

                    if (container != null) {

                        ItemStack selectedStack = container.getStack(activeSlot);

                        if (!selectedStack.isEmpty()) {

                            ItemStack resultStack =
                                    selectedStack.finishUsing(world, entity);

                            container.setStack(activeSlot, resultStack);
                            container.save();
                        }
                    }
                }
            }

            return stack;
        }

        return super.finishUsing(stack, world, entity);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {

        if (stack.getItem() instanceof LunchBoxItem) {

            ItemStack activeStack = getActiveFoodItemStack(stack);

            if (activeStack != null)
                return activeStack.getMaxUseTime();
        }

        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {

        if (stack.getItem() instanceof LunchBoxItem) {

            ItemStack activeStack = getActiveFoodItemStack(stack);

            if (activeStack != null)
                return activeStack.getUseAction();
        }

        return super.getUseAction(stack);
    }

    @Override
    public Text getName(ItemStack stack) {

        if (stack.getItem() instanceof LunchBoxItem) {

            ItemStack activeStack = getActiveFoodItemStack(stack);

            if (activeStack != null) {
                return Text.translatable(this.getTranslationKey(stack))
                        .append(" (")
                        .append(activeStack.getName())
                        .append(")");
            }
        }

        return super.getName(stack);
    }

    @Override
    public void appendTooltip(
            ItemStack stack,
            World world,
            List<Text> list,
            TooltipContext context
    ) {
        if (Screen.hasShiftDown()) {

            list.add(
                    Text.translatable("message.item.lunchbox")
                            .formatted(Formatting.GRAY)
            );

        } else {

            list.add(
                    Text.translatable("nutritionalbalance.tooltip.press_shift")
                            .formatted(Formatting.DARK_GRAY)
            );
        }
    }

    public void setActiveFood(
            ItemStack lunchBoxStack,
            ItemStack targetItemStack
    ) {
        setActiveFood(lunchBoxStack, targetItemStack, false);
    }

    public void setActiveFood(
            ItemStack lunchBoxStack,
            ItemStack targetItemStack,
            Boolean sync
    ) {
        if (
                lunchBoxStack.getItem() instanceof LunchBoxItem
                        && targetItemStack.getItem().getFoodComponent() != null
        ) {
            setActiveFood(
                    lunchBoxStack,
                    targetItemStack.getItem().getTranslationKey()
            );

            if (sync) {
                ModNetworkHandler.sendToServer(
                        new LunchBoxActiveItemSync(
                                targetItemStack.getItem().getTranslationKey()
                        )
                );
            }
        }
    }

    public void setActiveFood(
            ItemStack lunchBoxStack,
            String descriptionId
    ) {
        if (!(lunchBoxStack.getItem() instanceof LunchBoxItem))
            return;

        net.minecraft.nbt.NbtCompound tag =
                lunchBoxStack.getOrCreateNbt();

        tag.putString("active", descriptionId);

        lunchBoxStack.setNbt(tag);
    }

    public @Nullable Integer getActiveFoodItemSlot(
            ItemStack lunchBoxStack
    ) {
        if (lunchBoxStack.getItem() instanceof LunchBoxItem) {

            net.minecraft.nbt.NbtCompound tag =
                    lunchBoxStack.getNbt();

            if (tag != null && tag.contains("active")) {

                LunchBoxContainer container =
                        LunchBoxContainer.get(lunchBoxStack);

                if (container != null) {

                    String activeStack =
                            tag.getString("active");

                    for (
                            int i = 0;
                            i < container.size();
                            i++
                    ) {
                        if (
                                container.getStack(i)
                                        .getItem()
                                        .getTranslationKey()
                                        .equals(activeStack)
                        ) {
                            return i;
                        }
                    }
                }
            }
        }

        return null;
    }

    public @Nullable ItemStack getItemStack(
            ItemStack lunchBoxStack,
            int slot
    ) {
        if (lunchBoxStack.getItem() instanceof LunchBoxItem) {

            LunchBoxContainer container =
                    LunchBoxContainer.get(lunchBoxStack);

            if (container != null)
                return container.getStack(slot);
        }

        return null;
    }

    public @Nullable ItemStack getActiveFoodItemStack(
            ItemStack lunchBoxStack
    ) {
        Integer slot =
                getActiveFoodItemSlot(lunchBoxStack);

        if (slot != null)
            return getItemStack(lunchBoxStack, slot);

        return null;
    }
}