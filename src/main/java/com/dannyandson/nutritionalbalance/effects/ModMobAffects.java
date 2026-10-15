
package com.dannyandson.nutritionalbalance.effects;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.setup.Registration;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class ModMobAffects {

    public static class Nourished extends StatusEffect {
        private static final int COLOR = 3949738;

        public Nourished() {
            super(StatusEffectCategory.BENEFICIAL, COLOR);
        }

        public void setAttributes() {
            addModifier(EntityAttributes.GENERIC_MAX_HEALTH, "8d4c1b21-6f72-4e35-9a10-2b7c5d8e4101", Config.NOURISHED_MAX_HEALTH.get(), EntityAttributeModifier.Operation.ADDITION);
            addModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, "8d4c1b21-6f72-4e35-9a10-2b7c5d8e4102", Config.NOURISHED_KNOCKBACK_RESISTANCE.get(), EntityAttributeModifier.Operation.ADDITION);
            addModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "8d4c1b21-6f72-4e35-9a10-2b7c5d8e4103", Config.NOURISHED_MOVEMENT_SPEED.get(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            addModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "8d4c1b21-6f72-4e35-9a10-2b7c5d8e4104", Config.NOURISHED_ATTACK_DAMAGE.get(), EntityAttributeModifier.Operation.ADDITION);
            addModifier(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, "8d4c1b21-6f72-4e35-9a10-2b7c5d8e4105", Config.NOURISHED_ATTACK_KNOCKBACK.get(), EntityAttributeModifier.Operation.ADDITION);
            addModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "8d4c1b21-6f72-4e35-9a10-2b7c5d8e4106", Config.NOURISHED_ATTACK_SPEED.get(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        }

        private void addModifier(EntityAttribute attribute, String uuid, double amount, EntityAttributeModifier.Operation operation) {
            if (amount != 0d) {
                addAttributeModifier(attribute, uuid, amount, operation);
            }
        }
    }

    public static class MalNourished extends StatusEffect {
        private static final int COLOR = 11546150;

        public MalNourished() {
            super(StatusEffectCategory.HARMFUL, COLOR);
        }

        public void setAttributes() {
            addModifier(EntityAttributes.GENERIC_MAX_HEALTH, "9d4c1b21-6f72-4e35-9a10-2b7c5d8e4101", Config.MALNOURISHED_MAX_HEALTH.get(), EntityAttributeModifier.Operation.ADDITION);
            addModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, "9d4c1b21-6f72-4e35-9a10-2b7c5d8e4102", Config.MALNOURISHED_KNOCKBACK_RESISTANCE.get(), EntityAttributeModifier.Operation.ADDITION);
            addModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "9d4c1b21-6f72-4e35-9a10-2b7c5d8e4103", Config.MALNOURISHED_MOVEMENT_SPEED.get(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            addModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "9d4c1b21-6f72-4e35-9a10-2b7c5d8e4104", Config.MALNOURISHED_ATTACK_DAMAGE.get(), EntityAttributeModifier.Operation.ADDITION);
            addModifier(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, "9d4c1b21-6f72-4e35-9a10-2b7c5d8e4105", Config.MALNOURISHED_ATTACK_KNOCKBACK.get(), EntityAttributeModifier.Operation.ADDITION);
            addModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "9d4c1b21-6f72-4e35-9a10-2b7c5d8e4106", Config.MALNOURISHED_ATTACK_SPEED.get(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        }

        private void addModifier(EntityAttribute attribute, String uuid, double amount, EntityAttributeModifier.Operation operation) {
            if (amount != 0d) {
                addAttributeModifier(attribute, uuid, amount, operation);
            }
        }
    }

    public static class Engorged extends StatusEffect {
        private static final int COLOR = 11546150;

        public Engorged() {
            super(StatusEffectCategory.HARMFUL, COLOR);
        }

        public void setAttributes() {
            addModifier(EntityAttributes.GENERIC_MAX_HEALTH, "ad4c1b21-6f72-4e35-9a10-2b7c5d8e4101", Config.ENGORGED_MAX_HEALTH.get(), EntityAttributeModifier.Operation.ADDITION);
            addModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, "ad4c1b21-6f72-4e35-9a10-2b7c5d8e4102", Config.ENGORGED_KNOCKBACK_RESISTANCE.get(), EntityAttributeModifier.Operation.ADDITION);
            addModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "ad4c1b21-6f72-4e35-9a10-2b7c5d8e4103", Config.ENGORGED_MOVEMENT_SPEED.get(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            addModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "ad4c1b21-6f72-4e35-9a10-2b7c5d8e4104", Config.ENGORGED_ATTACK_DAMAGE.get(), EntityAttributeModifier.Operation.ADDITION);
            addModifier(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, "ad4c1b21-6f72-4e35-9a10-2b7c5d8e4105", Config.ENGORGED_ATTACK_KNOCKBACK.get(), EntityAttributeModifier.Operation.ADDITION);
            addModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "ad4c1b21-6f72-4e35-9a10-2b7c5d8e4106", Config.ENGORGED_ATTACK_SPEED.get(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        }

        private void addModifier(EntityAttribute attribute, String uuid, double amount, EntityAttributeModifier.Operation operation) {
            if (amount != 0d) {
                addAttributeModifier(attribute, uuid, amount, operation);
            }
        }
    }

    public static void resetPlayerEffects(PlayerEntity player) {
        if (player.hasStatusEffect(Registration.NOURISHED_EFFECT)) {
            player.removeStatusEffect(Registration.NOURISHED_EFFECT);
        }

        if (player.hasStatusEffect(Registration.MALNOURISHED_EFFECT)) {
            player.removeStatusEffect(Registration.MALNOURISHED_EFFECT);
        }

        if (player.hasStatusEffect(Registration.ENGORGED_EFFECT)) {
            player.removeStatusEffect(Registration.ENGORGED_EFFECT);
        }
    }

    public static void loadModEffects() {
        if (NutritionalBalance.modEffectsLoaded) {
            return;
        }

        ((Nourished) Registration.NOURISHED_EFFECT).setAttributes();
        ((MalNourished) Registration.MALNOURISHED_EFFECT).setAttributes();
        ((Engorged) Registration.ENGORGED_EFFECT).setAttributes();

        NutritionalBalance.modEffectsLoaded = true;
    }
}
