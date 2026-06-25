package com.gamemaker.gmrules.GameElements;

import com.gamemaker.gmrules.*;

public class Material extends GameElement {
// *** MEMBERS ***
    private int hardness = 0;
    private float weight = 0.0f; // / per cubic volume

// *** CONSTRUCTORS ***
    public Material(String name) {
        super(name);
    }

    public Material(String name, String description) {
        super(name, description);
    }

// *** METHODS ***
}
