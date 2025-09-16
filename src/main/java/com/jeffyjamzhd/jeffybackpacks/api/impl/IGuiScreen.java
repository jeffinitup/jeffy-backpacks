package com.jeffyjamzhd.jeffybackpacks.api.impl;

public interface IGuiScreen {
    /**
     * Callback for mouse scrolling in GUI. {@code true} if blocks RetroEMI
     * @param x Mouse x position
     * @param y Mouse y position
     * @param scroll Scroll value (1 - up, -1 - down)
     */
    boolean jbp$handleMouseScroll(int x, int y, int scroll);
}
