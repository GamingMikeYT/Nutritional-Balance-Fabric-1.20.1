package com.dannyandson.nutritionalbalance;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class Config {

    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_NUTRIENT_LEVELS = "nutrient_levels";
    public static final String CATEGORY_EFFECTS = "nutrition_affects";
    public static final String CATEGORY_EFFECTS_NOURISHED = "nourished_affects";
    public static final String CATEGORY_EFFECTS_MALNOURISHED = "malnourished_affects";
    public static final String CATEGORY_EFFECTS_ENGORGED = "engorged_affects";
    public static final String CATEGORY_FOODS = "nutrient_foods";

    public static ForgeConfigSpec.DoubleValue NUTRIENT_INITIAL;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_MALNOURISHED;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_LOW_TARGET;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_TARGET_HIGH;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_ENGORGED;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_MAX;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_INCREMENT_RATE;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_DECAY_RATE;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_DEATH_LOSS;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_MAX_FOOD_VALUE;

    public static ForgeConfigSpec.ConfigValue<List<String>> BAD_NUTRIENTS;
    public static ForgeConfigSpec.ConfigValue<List<String>> GOOD_NUTRIENTS;

    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_VEGETABLES;
    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_SUGARS;
    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_CARBS;
    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_PROTEINS;
    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_Fruits;

    public static ForgeConfigSpec.IntValue NUTRIENT_BUTTON_X;
    public static ForgeConfigSpec.IntValue NUTRIENT_BUTTON_Y;
    public static ForgeConfigSpec.BooleanValue NUTRIENT_BUTTON_ENABLED;
    public static ForgeConfigSpec.BooleanValue SHOW_THRESHOLD_TOAST;

    public static ForgeConfigSpec.DoubleValue NOURISHED_MAX_HEALTH;
    public static ForgeConfigSpec.DoubleValue NOURISHED_KNOCKBACK_RESISTANCE;
    public static ForgeConfigSpec.DoubleValue NOURISHED_MOVEMENT_SPEED;
    public static ForgeConfigSpec.DoubleValue NOURISHED_ATTACK_DAMAGE;
    public static ForgeConfigSpec.DoubleValue NOURISHED_ATTACK_KNOCKBACK;
    public static ForgeConfigSpec.DoubleValue NOURISHED_ATTACK_SPEED;

    public static ForgeConfigSpec.DoubleValue MALNOURISHED_MAX_HEALTH;
    public static ForgeConfigSpec.DoubleValue MALNOURISHED_KNOCKBACK_RESISTANCE;
    public static ForgeConfigSpec.DoubleValue MALNOURISHED_MOVEMENT_SPEED;
    public static ForgeConfigSpec.DoubleValue MALNOURISHED_ATTACK_DAMAGE;
    public static ForgeConfigSpec.DoubleValue MALNOURISHED_ATTACK_KNOCKBACK;
    public static ForgeConfigSpec.DoubleValue MALNOURISHED_ATTACK_SPEED;

    public static ForgeConfigSpec.DoubleValue ENGORGED_MAX_HEALTH;
    public static ForgeConfigSpec.DoubleValue ENGORGED_KNOCKBACK_RESISTANCE;
    public static ForgeConfigSpec.DoubleValue ENGORGED_MOVEMENT_SPEED;
    public static ForgeConfigSpec.DoubleValue ENGORGED_ATTACK_DAMAGE;
    public static ForgeConfigSpec.DoubleValue ENGORGED_ATTACK_KNOCKBACK;
    public static ForgeConfigSpec.DoubleValue ENGORGED_ATTACK_SPEED;

    public static ForgeConfigSpec.IntValue LUNCHBOX_SLOT_COUNT;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);
        SERVER_BUILDER.comment("Nutrient Levels").push(CATEGORY_NUTRIENT_LEVELS);

        NUTRIENT_MALNOURISHED = SERVER_BUILDER.comment(
                        "Malnourishment value, below which bad effects can occur. (default:1.0)")
                .defineInRange("nutrient_level_malnourished", 1.0, 0.0, Double.MAX_VALUE);

        NUTRIENT_INITIAL = SERVER_BUILDER.comment(
                        "Initial nutrient level for new player. (default:50.0)")
                .defineInRange("nutrient_initial", 50.0, 0.0, Double.MAX_VALUE);

        NUTRIENT_LOW_TARGET = SERVER_BUILDER.comment(
                        "Boundary between low and target, above which good effects can occur. (default:100.0)")
                .defineInRange("nutrient_level_low_target", 100.0, 0.0, Double.MAX_VALUE);

        NUTRIENT_TARGET_HIGH = SERVER_BUILDER.comment(
                        "Boundary between target and high, below which good effects can occur. (default:120.0)")
                .defineInRange("nutrient_level_target_high", 120.0, 0.0, Double.MAX_VALUE);

        NUTRIENT_ENGORGED = SERVER_BUILDER.comment(
                        "Engorgement value, above which bad effects can occur. (default:170.0)")
                .defineInRange("nutrient_level_engorged", 170.0, 0.0, Double.MAX_VALUE);

        NUTRIENT_MAX = SERVER_BUILDER.comment(
                        "Maximum nutrient level. Nutrients will cap out at this value. (default:180.0)")
                .defineInRange("nutrient_level_max", 180.0, 0.0, Double.MAX_VALUE);

        NUTRIENT_INCREMENT_RATE = SERVER_BUILDER.comment(
                        "Nutrient increment rate relative to food saturation and points. (default:1.0)")
                .defineInRange("nutrient_increment_rate", 1.0, 0.0, Double.MAX_VALUE);

        NUTRIENT_DECAY_RATE = SERVER_BUILDER.comment(
                        "Nutrient decay rate relative to player saturation and food level decay. (default:1.0)")
                .defineInRange("nutrient_decay_rate", 1.0, 0.0, Double.MAX_VALUE);

        NUTRIENT_DEATH_LOSS = SERVER_BUILDER.comment(
                        "Nutrient loss on death, bottoming out at the initial value. (default:10.0)")
                .defineInRange("nutrient_death_loss", 10.0, 0.0, Double.MAX_VALUE);

        NUTRIENT_MAX_FOOD_VALUE = SERVER_BUILDER.comment(
                        "Maximum food value a single nutrient can provide. (default:20.0)")
                .defineInRange("nutrient_max_food_value", 20.0, 1.0, Double.MAX_VALUE);

        LUNCHBOX_SLOT_COUNT = SERVER_BUILDER.comment(
                        "Number of inventory slots the lunchbox contains. (default:5)")
                .defineInRange("lunchbox_slot_count", 5, 1, 9);

        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Effects").push(CATEGORY_EFFECTS);

        BAD_NUTRIENTS = SERVER_BUILDER.comment(
                        "Nutrients which only give bad effects when engorged. (default:sugars)")
                .define("effect_bad_nutrients", new ArrayList<>(Arrays.asList("sugars")));

        GOOD_NUTRIENTS = SERVER_BUILDER.comment(
                        "Nutrients which only give good effects. (default:vegetables)")
                .define("effect_good_nutrients", new ArrayList<>(Arrays.asList("vegetables")));

        SERVER_BUILDER.comment("Nourished Effects").push(CATEGORY_EFFECTS_NOURISHED);

        NOURISHED_MAX_HEALTH = SERVER_BUILDER.comment(
                        "Health points to add or subtract. (default:10.0)")
                .defineInRange("nourished_max_health", 10d, -40d, 40d);

        NOURISHED_KNOCKBACK_RESISTANCE = SERVER_BUILDER.comment(
                        "Knockback resistance to add. (default:0.0)")
                .defineInRange("nourished_knockback_resistance", 0d, 0d, 1d);

        NOURISHED_MOVEMENT_SPEED = SERVER_BUILDER.comment(
                        "Movement speed modifier. (default:0.2)")
                .defineInRange("nourished_movement_speed", 0.2d, -0.7d, 2.0d);

        NOURISHED_ATTACK_DAMAGE = SERVER_BUILDER.comment(
                        "Attack damage modifier. (default:0.2)")
                .defineInRange("nourished_attack_damage", 0.2d, -2.0d, 4.0d);

        NOURISHED_ATTACK_KNOCKBACK = SERVER_BUILDER.comment(
                        "Attack knockback modifier. (default:0.0)")
                .defineInRange("nourished_knockback", 0d, 0d, 4d);

        NOURISHED_ATTACK_SPEED = SERVER_BUILDER.comment(
                        "Mining speed modifier. (default:0.1)")
                .defineInRange("nourished_mining_speed", 0.1d, -4.0d, 4.0d);

        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Malnourished Effects").push(CATEGORY_EFFECTS_MALNOURISHED);

        MALNOURISHED_MAX_HEALTH = SERVER_BUILDER.comment(
                        "Health points to add or subtract. (default:-4.0)")
                .defineInRange("malnourished_max_health", -4d, -40d, 40d);

        MALNOURISHED_KNOCKBACK_RESISTANCE = SERVER_BUILDER.comment(
                        "Knockback resistance modifier. (default:0.0)")
                .defineInRange("malnourished_knockback_resistance", 0d, 0d, 1d);

        MALNOURISHED_MOVEMENT_SPEED = SERVER_BUILDER.comment(
                        "Movement speed modifier. (default:-0.15)")
                .defineInRange("malnourished_movement_speed", -0.15d, -0.7d, 2.0d);

        MALNOURISHED_ATTACK_DAMAGE = SERVER_BUILDER.comment(
                        "Attack damage modifier. (default:-0.15)")
                .defineInRange("malnourished_attack_damage", -0.15d, -2.0d, 4.0d);

        MALNOURISHED_ATTACK_KNOCKBACK = SERVER_BUILDER.comment(
                        "Knockback resistance modifier. (default:0.0)")
                .defineInRange("malnourished_knockback", 0d, 0d, 4d);

        MALNOURISHED_ATTACK_SPEED = SERVER_BUILDER.comment(
                        "Mining speed modifier. (default:0.0)")
                .defineInRange("malnourished_mining_speed", 0.0d, -4.0d, 4.0d);

        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Engorged Effects").push(CATEGORY_EFFECTS_ENGORGED);

        ENGORGED_MAX_HEALTH = SERVER_BUILDER.comment(
                        "Health points to add or subtract. (default:0.0)")
                .defineInRange("engorged_max_health", 0d, -40d, 40d);

        ENGORGED_KNOCKBACK_RESISTANCE = SERVER_BUILDER.comment(
                        "Knockback resistance modifier. (default:0.0)")
                .defineInRange("engorged_knockback_resistance", 0d, 0d, 1d);

        ENGORGED_MOVEMENT_SPEED = SERVER_BUILDER.comment(
                        "Movement speed modifier. (default:-0.15)")
                .defineInRange("engorged_movement_speed", -0.15d, -0.7d, 2.0d);

        ENGORGED_ATTACK_DAMAGE = SERVER_BUILDER.comment(
                        "Attack damage modifier. (default:0.0)")
                .defineInRange("engorged_attack_damage", 0.0d, -2.0d, 4.0d);

        ENGORGED_ATTACK_KNOCKBACK = SERVER_BUILDER.comment(
                        "Knockback resistance modifier. (default:0.0)")
                .defineInRange("engorged_knockback", 0d, 0d, 4d);

        ENGORGED_ATTACK_SPEED = SERVER_BUILDER.comment(
                        "Mining speed modifier. (default:-0.1)")
                .defineInRange("engorged_mining_speed", -0.1d, -4.0d, 4.0d);

        SERVER_BUILDER.pop();
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment(
                        "Food tags and item IDs used to assign nutrients. Cooked and crafted foods may inherit nutrients from recipe ingredients.")
                .push(CATEGORY_FOODS);

        LIST_VEGETABLES = SERVER_BUILDER.comment("List of vegetables.")
                .define("vegetables_item_list", new ArrayList<>(Arrays.asList(
                        "#minecraft:flowers",
                        "#c:vegetables",
                        "#nourish:vegetables"
                )));

        LIST_CARBS = SERVER_BUILDER.comment("List of carbohydrates.")
                .define("carbs_item_list", new ArrayList<>(Arrays.asList(
                        "#c:carbs",
                        "#c:grain",
                        "#c:bread/wheat",
                        "#c:foods/breads",
                        "#c:foods/breads/wheat",
                        "#nourish:carbohydrates"
                )));

        LIST_SUGARS = SERVER_BUILDER.comment("List of simple sugars.")
                .define("sugars_item_list", new ArrayList<>(Arrays.asList(
                        "#nourish:sweets",
                        "#c:sugars",
                        "#c:candies",
                        "#c:foods/candy"
                )));

        LIST_PROTEINS = SERVER_BUILDER.comment("List of proteins.")
                .define("protein_item_list", new ArrayList<>(Arrays.asList(
                        "#c:protein",
                        "#c:proteins",
                        "#c:milk",
                        "#c:yogurt",
                        "#c:nuts",
                        "#nourish:protein"
                )));

        LIST_Fruits = SERVER_BUILDER.comment("List of fruits.")
                .define("fruits_item_list", new ArrayList<>(Arrays.asList(
                        "#c:fruits",
                        "#c:fruit",
                        "#c:fruits/berry",
                        "#c:berries",
                        "#nourish:fruit"
                )));

        SERVER_BUILDER.pop().pop();

        SERVER_CONFIG = SERVER_BUILDER.build();

        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        CLIENT_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);

        NUTRIENT_BUTTON_ENABLED = CLIENT_BUILDER.comment(
                        "Show the nutrient button in the player inventory screen. (default:true)")
                .define("nutrient_button_enabled", true);

        CLIENT_BUILDER.comment("Nutrient Button Location").push("button");

        NUTRIENT_BUTTON_X = CLIENT_BUILDER.comment(
                        "X offset of nutrition button. (default:131)")
                .defineInRange("nutrient_button_x", 131, 0, 512);

        NUTRIENT_BUTTON_Y = CLIENT_BUILDER.comment(
                        "Y offset of nutrition button. (default:61)")
                .defineInRange("nutrient_button_y", 61, 0, 512);

        CLIENT_BUILDER.pop();

        SHOW_THRESHOLD_TOAST = CLIENT_BUILDER.comment(
                        "Show a toast when nutrition status changes. (default:true)")
                .define("show_threshold_toast", true);

        CLIENT_BUILDER.pop();

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
