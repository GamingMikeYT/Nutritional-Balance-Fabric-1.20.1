package com.dannyandson.nutritionalbalance.nutrients;

import net.minecraft.client.resource.language.I18n;

public class Nutrient {

    public String name;

    public Nutrient(String name) {
        this.name = name;
    }

    public String getLocalizedName() {
        return I18n.translate(
                "nutritionalbalance.nutrient." + this.name,
                new Object[0]
        );
    }
}