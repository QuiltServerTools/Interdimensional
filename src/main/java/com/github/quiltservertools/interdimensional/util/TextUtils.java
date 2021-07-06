package com.github.quiltservertools.interdimensional.util;

import net.minecraft.util.Formatting;

public class TextUtils {
    public static final TextUtils INSTANCE = new TextUtils();

    public Formatting getInfoFormatting() {
        return Formatting.BLUE;
    }

    public Formatting getSuccessFormatting() {
        return Formatting.GREEN;
    }

    public Formatting getErrorFormatting() {
        return Formatting.RED;
    }
}
