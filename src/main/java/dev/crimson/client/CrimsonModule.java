package dev.crimson.client;

import java.util.List;

public enum CrimsonModule {
    ATTACK_COOLDOWN("Attack Cooldown", "Shows vanilla attack recharge", Category.COMBAT, false),
    ZOOM("Zoom", "Hold C for zoom", Category.MOVEMENT, false),
    SPRINT_STATUS("Sprint Status", "Shows sprint state", Category.MOVEMENT, false),
    FULLBRIGHT("Fullbright", "Raises local gamma", Category.RENDER, false),
    HUD_PANEL("HUD Panel", "Compact Crimson HUD", Category.RENDER, false),
    COORDINATES("Coordinates", "Displays XYZ position", Category.PLAYER, false),
    FPS("FPS Counter", "Displays client FPS", Category.PLAYER, false),
    DIRECTION("Direction", "Displays facing direction", Category.PLAYER, false),
    HEALTH("Health", "Displays current health", Category.PLAYER, false),
    HUNGER("Hunger", "Displays food meter", Category.PLAYER, false),
    BIOME("Biome", "Displays current biome", Category.WORLD, false),
    WORLD_CLOCK("World Clock", "Displays world time", Category.WORLD, false),
    REAL_CLOCK("Local Clock", "Displays device time", Category.MISC, false),
    KEYSTROKES("Keystrokes", "Displays movement input", Category.MISC, false);

    public enum Category {
        COMBAT("Combat"), MOVEMENT("Movement"), RENDER("Render"),
        PLAYER("Player"), WORLD("World"), MISC("Misc");
        private final String title;
        Category(String title) { this.title = title; }
        public String title() { return title; }
    }

    private final String title;
    private final String description;
    private final Category category;
    private final boolean permissionGated;

    CrimsonModule(String title, String description, Category category, boolean permissionGated) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.permissionGated = permissionGated;
    }

    public String title() { return title; }
    public String description() { return description; }
    public Category category() { return category; }
    public boolean permissionGated() { return permissionGated; }
    public static List<CrimsonModule> in(Category category) {
        return java.util.Arrays.stream(values()).filter(m -> m.category == category).toList();
    }
}