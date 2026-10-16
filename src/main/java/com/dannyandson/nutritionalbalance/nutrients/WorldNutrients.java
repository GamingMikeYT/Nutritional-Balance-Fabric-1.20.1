package com.dannyandson.nutritionalbalance.nutrients;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.NutrientDataSyncTrigger;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.*;

public class WorldNutrients {

    private static final List<Nutrient> nutrients = new ArrayList<>();
    private static final Map<Item, List<Nutrient>> nutrientMap = new HashMap<>();

    private static Map<Item, List<Recipe<?>>> recipesByOutput = null;
    private static RecipeManager indexedRecipeManager = null;

    private static final Set<Item> resolving = new HashSet<>();
    private static final Set<Item> pendingClientSyncs = new HashSet<>();

    private static boolean prewarmComplete = false;
    private static boolean nutrientsRegistered = false;

    public static void register() {
        reset();

        // Discover nutrient definitions supplied by datapacks or mods.
        for (TagKey<Item> tagKey : Registries.ITEM.streamTags()
                .sorted((a, b) ->
                        a.id().getPath().compareTo(b.id().getPath()))
                .toList()) {

            Identifier id = tagKey.id();

            if (id.getNamespace().equals("c")
                    && id.getPath().startsWith("nutrients/")) {

                addNutrient(id.getPath().substring("nutrients/".length()));
            }
        }

        // Ensure the default configuration categories exist.
        addNutrient("fruits");
        addNutrient("proteins");
        addNutrient("carbs");
        addNutrient("vegetables");
        addNutrient("sugars");

        nutrientsRegistered = true;

        NutritionalBalance.LOGGER.info(
                "Nutritional Balance: registered nutrients: {}",
                nutrients.stream().map(n -> n.name).toList()
        );
    }

    private static void addNutrient(String name) {
        for (Nutrient nutrient : nutrients) {
            if (nutrient.name.equals(name)) {
                return;
            }
        }

        nutrients.add(new Nutrient(name));
    }

    public static List<Nutrient> get() {
        if (!nutrientsRegistered) {
            register();
        }

        return nutrients;
    }

    public static Nutrient getByName(String name) {
        for (Nutrient nutrient : get()) {
            if (nutrient.name.equals(name)) {
                return nutrient;
            }
        }

        return null;
    }

    public static List<Nutrient> getNutrients(Item item, World world) {
        return getNutrients(item.getDefaultStack(), world, 1);
    }

    public static List<Nutrient> getNutrients(ItemStack itemStack, World world) {
        return getNutrients(itemStack, world, 1);
    }

    private static List<Nutrient> getNutrients(
            ItemStack item,
            World world,
            int iteration
    ) {
        if (item.getItem() instanceof LunchBoxItem lunchBoxItem) {
            item = lunchBoxItem.getActiveFoodItemStack(item);
        }

        if (item == null || item.isEmpty()) {
            return new ArrayList<>();
        }

        Item targetItem = item.getItem();

        if (!nutrientMap.containsKey(targetItem)) {

            if (world != null && world.isClient()) {
                if (pendingClientSyncs.add(targetItem)) {
                    ModNetworkHandler.sendToServer(
                            new NutrientDataSyncTrigger(targetItem)
                    );
                }

            } else if (world != null) {

                List<Nutrient> nutrientList = new ArrayList<>();

                // Match the item's tags against configured category tags.
                for (TagKey<Item> tagKey : item.streamTags()
                        .sorted((a, b) ->
                                a.id().getPath().compareTo(b.id().getPath()))
                        .toList()) {

                    Identifier tag = tagKey.id();
                    String tagName =
                            "#" + tag.getNamespace() + ":" + tag.getPath();

                    if (Config.LIST_Fruits.get().contains(tagName)) {
                        addIfPresent(nutrientList, "fruits");
                    }

                    if (Config.LIST_PROTEINS.get().contains(tagName)) {
                        addIfPresent(nutrientList, "proteins");
                    }

                    if (Config.LIST_CARBS.get().contains(tagName)) {
                        addIfPresent(nutrientList, "carbs");
                    }

                    if (Config.LIST_VEGETABLES.get().contains(tagName)) {
                        addIfPresent(nutrientList, "vegetables");
                    }

                    if (Config.LIST_SUGARS.get().contains(tagName)) {
                        addIfPresent(nutrientList, "sugars");
                    }

                    if (tag.getNamespace().equals("c")
                            && tag.getPath().startsWith("nutrients/")) {
                        addIfPresent(
                                nutrientList,
                                tag.getPath().substring("nutrients/".length())
                        );
                    }
                }

                // Match explicit item IDs in the config.
                Identifier itemId = Registries.ITEM.getId(targetItem);
                String itemRegistryName =
                        itemId.getNamespace() + ":" + itemId.getPath();

                if (Config.LIST_CARBS.get().contains(itemRegistryName)) {
                    addIfPresent(nutrientList, "carbs");
                }

                if (Config.LIST_Fruits.get().contains(itemRegistryName)) {
                    addIfPresent(nutrientList, "fruits");
                }

                if (Config.LIST_PROTEINS.get().contains(itemRegistryName)) {
                    addIfPresent(nutrientList, "proteins");
                }

                if (Config.LIST_SUGARS.get().contains(itemRegistryName)) {
                    addIfPresent(nutrientList, "sugars");
                }

                if (Config.LIST_VEGETABLES.get().contains(itemRegistryName)) {
                    addIfPresent(nutrientList, "vegetables");
                }

                boolean depthLimited = false;

                if (nutrientList.isEmpty() && iteration < 5) {
                    boolean isMeat = false;

                    for (TagKey<Item> tagKey : item.streamTags().toList()) {
                        String path = tagKey.id().getPath();
                        String namespace = tagKey.id().getNamespace();

                        if ((namespace.equals("c")
                                && (path.contains("meat")
                                || path.contains("raw_meat")))
                                || (namespace.equals("minecraft")
                                && path.equals("meat"))) {
                            isMeat = true;
                            break;
                        }
                    }

                    if (isMeat) {
                        addIfPresent(nutrientList, "proteins");

                    } else if (resolving.add(targetItem)) {
                        try {
                            for (Recipe<?> recipe :
                                    getRecipesForOutput(world, targetItem)) {

                                DefaultedList<Ingredient> ingredients =
                                        recipe.getIngredients();

                                // Inspect every matching item in each ingredient.
                                for (Ingredient ingredient : ingredients) {
                                    ItemStack[] matchingStacks =
                                            ingredient.getMatchingStacks();

                                    for (ItemStack matchingStack : matchingStacks) {
                                        if (matchingStack.isEmpty()) {
                                            continue;
                                        }

                                        List<Nutrient> inherited =
                                                getNutrients(
                                                        matchingStack,
                                                        world,
                                                        iteration + 1
                                                );

                                        for (Nutrient nutrient : inherited) {
                                            if (!nutrientList.contains(nutrient)) {
                                                nutrientList.add(nutrient);
                                            }
                                        }
                                    }
                                }
                            }
                        } finally {
                            resolving.remove(targetItem);
                        }

                    } else {
                        depthLimited = true;
                    }
                }

                if (!depthLimited) {
                    nutrientMap.put(targetItem, nutrientList);
                } else {
                    return nutrientList;
                }
            }
        }

        return nutrientMap.containsKey(targetItem)
                ? nutrientMap.get(targetItem)
                : new ArrayList<>();
    }

    private static void addIfPresent(
            List<Nutrient> target,
            String name
    ) {
        Nutrient nutrient = getByName(name);

        if (nutrient != null && !target.contains(nutrient)) {
            target.add(nutrient);
        }
    }

    private static List<Recipe<?>> getRecipesForOutput(
            World world,
            Item item
    ) {
        RecipeManager rm = world.getRecipeManager();

        if (recipesByOutput == null || indexedRecipeManager != rm) {
            buildRecipeIndex(world, rm);
        }

        List<Recipe<?>> bucket = recipesByOutput.get(item);

        return bucket != null ? bucket : Collections.emptyList();
    }

    private static void buildRecipeIndex(
            World world,
            RecipeManager rm
    ) {
        long start = System.currentTimeMillis();
        Map<Item, List<Recipe<?>>> idx = new HashMap<>();

        for (Recipe<?> recipe : rm.values()) {
            try {
                ItemStack output =
                        recipe.getOutput(world.getRegistryManager());

                if (output != null && !output.isEmpty()) {
                    idx.computeIfAbsent(
                            output.getItem(),
                            key -> new ArrayList<>()
                    ).add(recipe);
                }
            } catch (Throwable t) {
                // Skip recipes whose outputs cannot be queried.
            }
        }

        for (List<Recipe<?>> bucket : idx.values()) {
            bucket.sort((a, b) ->
                    b.getId().getPath().compareTo(a.getId().getPath()));
        }

        recipesByOutput = idx;
        indexedRecipeManager = rm;

        NutritionalBalance.LOGGER.info(
                "Nutritional Balance: built recipe-output index ({} output items, {} ms)",
                idx.size(),
                System.currentTimeMillis() - start
        );
    }

    public static void prewarm(World world) {
        if (prewarmComplete || world == null) {
            return;
        }

        long start = System.currentTimeMillis();
        int considered = 0;
        int succeeded = 0;

        for (Item item : Registries.ITEM) {
            try {
                FoodComponent food = item.getFoodComponent();

                if (food != null) {
                    considered++;
                    getNutrients(item, world);
                    succeeded++;
                }
            } catch (Throwable t) {
                NutritionalBalance.LOGGER.debug(
                        "Nutritional Balance: failed to prewarm nutrients for {}",
                        Registries.ITEM.getId(item),
                        t
                );
            }
        }

        prewarmComplete = true;

        NutritionalBalance.LOGGER.info(
                "Nutritional Balance: pre-warmed nutrient cache for {}/{} food items in {} ms",
                succeeded,
                considered,
                System.currentTimeMillis() - start
        );
    }

    public static Map<Identifier, List<String>> snapshotForSync() {
        Map<Identifier, List<String>> snapshot =
                new HashMap<>(nutrientMap.size());

        for (Map.Entry<Item, List<Nutrient>> entry : nutrientMap.entrySet()) {
            Identifier itemId = Registries.ITEM.getId(entry.getKey());

            if (itemId == null) {
                continue;
            }

            List<String> names = new ArrayList<>(entry.getValue().size());

            for (Nutrient nutrient : entry.getValue()) {
                names.add(nutrient.name);
            }

            snapshot.put(itemId, names);
        }

        return snapshot;
    }

    public static void applyBulkSync(Map<Identifier, List<String>> data) {
        int applied = 0;

        for (Map.Entry<Identifier, List<String>> entry : data.entrySet()) {
            Identifier key = entry.getKey();

            if (!Registries.ITEM.containsId(key)) {
                continue;
            }

            Item item = Registries.ITEM.get(key);

            if (item == null) {
                continue;
            }

            List<Nutrient> nuts = new ArrayList<>(entry.getValue().size());

            for (String name : entry.getValue()) {
                if (name.isEmpty()) {
                    continue;
                }

                Nutrient nutrient = getByName(name);

                if (nutrient != null) {
                    nuts.add(nutrient);
                }
            }

            nutrientMap.put(item, nuts);
            pendingClientSyncs.remove(item);
            applied++;
        }

        NutritionalBalance.LOGGER.info(
                "Nutritional Balance: applied bulk nutrient sync for {} items",
                applied
        );
    }


    public static float getEffectiveFoodQuality(
            FoodComponent foodItem,
            int numberOfNutrients
    ) {
        float actualSaturation = foodItem.getHunger()
                * foodItem.getSaturationModifier()
                * 2.0F;

        return getEffectiveFoodQuality(
                foodItem.getHunger(),
                actualSaturation,
                numberOfNutrients
        );
    }


    public static float getEffectiveFoodQuality(
            float healing,
            float saturation,
            int numberOfNutrients
    ) {
        return Math.min(
                healing + saturation,
                Config.NUTRIENT_MAX_FOOD_VALUE.get().floatValue()
                        * numberOfNutrients
        );
    }

    public static void setItemNutrients(Item item, List<Nutrient> nutrients) {
        nutrientMap.put(item, nutrients);
        pendingClientSyncs.remove(item);
    }

    public static void reset() {
        nutrients.clear();
        nutrientMap.clear();

        recipesByOutput = null;
        indexedRecipeManager = null;

        resolving.clear();
        pendingClientSyncs.clear();

        prewarmComplete = false;
        nutrientsRegistered = false;
    }
}
