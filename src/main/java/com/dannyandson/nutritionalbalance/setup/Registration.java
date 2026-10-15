package com.dannyandson.nutritionalbalance.setup;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.effects.ModMobAffects;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxMenu;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class Registration {

    public static final StatusEffect ENGORGED_EFFECT =
            Registry.register(
                    Registries.STATUS_EFFECT,
                    Identifier.of(NutritionalBalance.MODID, "engorged_effect"),
                    new ModMobAffects.Engorged()
            );

    public static final StatusEffect MALNOURISHED_EFFECT =
            Registry.register(
                    Registries.STATUS_EFFECT,
                    Identifier.of(NutritionalBalance.MODID, "malnourished_effect"),
                    new ModMobAffects.MalNourished()
            );

    public static final StatusEffect NOURISHED_EFFECT =
            Registry.register(
                    Registries.STATUS_EFFECT,
                    Identifier.of(NutritionalBalance.MODID, "nourished_effect"),
                    new ModMobAffects.Nourished()
            );

    public static final ScreenHandlerType<LunchBoxMenu> LUNCHBOX_MENU_TYPE =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(NutritionalBalance.MODID, "lunchbbox"),
                    new ScreenHandlerType<>(
                            LunchBoxMenu::createMenu,
                            FeatureFlags.VANILLA_FEATURES
                    )
            );

    public static final Item LUNCHBOX_ITEM =
            Registry.register(
                    Registries.ITEM,
                    Identifier.of(NutritionalBalance.MODID, "lunchbox"),
                    new LunchBoxItem()
            );

    public static void register() {
        // Registration happens when the static fields above are initialized.
    }
}