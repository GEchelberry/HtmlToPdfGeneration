/*
 * This file is part of HTML To PDF Generator.
 *
 * Copyright (C) 2025 Gregory Echelberry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.htmltopdf.fonts;

/**
 * Class to manage the Font Color
 */
public class Color {
    private int red;
    private int green;
    private int blue;

    /**
     * Constructor - Initialize a new Color object
     * @param red int red value 0-255
     * @param green int green value 0-255
     * @param blue int blue value 0-255
     */
    public Color(int red, int green, int blue) {
        this.red = red > 255 ? 255 : red;
        this.green = green > 255 ? 255 : green;
        this.blue = blue > 255 ? 255 : blue;
    }

    /**
     * Get the red value as a float between 0 and 1
     * @return float of the red value
     */
    public float getRed() {
        return red / 255f;
    }

    /**
     * Get the green value as a float between 0 and 1
     * @return float of the green value
     */
    public float getGreen() {
        return green / 255f;
    }

    /**
     * Get the blue value as a float between 0 and 1
     * @return float of the blue value
     */
    public float getBlue() {
        return blue / 255f;
    }
}
