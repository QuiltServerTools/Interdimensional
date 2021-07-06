package com.github.quiltservertools.interdimensional.util;

import net.minecraft.world.Difficulty;

public class WorldConfigUtils {
    public static Difficulty getDifficulty(int d) {
        if (d == 0) return Difficulty.PEACEFUL;
        if (d == 1) return Difficulty.EASY;
        if (d == 3) return Difficulty.HARD;
        return Difficulty.NORMAL;
    }
}
