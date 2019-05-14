package noppes.npcs.constants;

import noppes.npcs.api.constants.AnimationType;

public enum EnumCompanionStage {
    BABY(0, AnimationType.CRAWL, "companion.baby"),
    CHILD(72000, AnimationType.NORMAL, "companion.child"),
    TEEN(180000, AnimationType.NORMAL, "companion.teenager"),
    ADULT(324000, AnimationType.NORMAL, "companion.adult"),
    FULLGROWN(450000, AnimationType.NORMAL, "companion.fullgrown");

    private int matureAge;
    private int animation;
    private String name;

    EnumCompanionStage(int age, int animation, String name) {
        this.matureAge = age;
        this.animation = animation;
        this.name = name;
    }

    public int getMatureAge() {
        return matureAge;
    }

    public int getAnimation() {
        return animation;
    }

    public String getName() {
        return name;
    }
}
