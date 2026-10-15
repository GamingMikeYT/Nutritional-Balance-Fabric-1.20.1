package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.Config;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class LunchBoxContainer extends SimpleInventory {

    public final ItemStack lunchBox;

    public static LunchBoxContainer get(ItemStack lunchBox) {
        if (lunchBox.getItem() instanceof LunchBoxItem)
            return new LunchBoxContainer(lunchBox);
        return null;
    }

    private LunchBoxContainer(ItemStack lunchBox) {
        super(Config.LUNCHBOX_SLOT_COUNT.get());
        this.lunchBox = lunchBox;

        NbtCompound tag = lunchBox.getNbt();

        if (tag != null && tag.contains("contents", NbtElement.LIST_TYPE)) {
            readNbtList(tag.getList("contents", NbtElement.COMPOUND_TYPE));
        }
    }

    public void save() {
        NbtList contentsTag = toNbtList();

        NbtCompound tag = lunchBox.getOrCreateNbt();
        tag.put("contents", contentsTag);

        lunchBox.setNbt(tag);
    }
}