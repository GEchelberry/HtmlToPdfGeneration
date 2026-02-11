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

package com.htmltopdf.draw;

/**
 * Class to render a box
 */
public class DrawLine {
    private float x;
    private float y;
    private float width;
    private float height;

    /**
     * Constructor - Initialize the Line object
     * @param x float of the X coordinate
     * @param y flaot of the y coordinate
     * @param width float of the width
     * @param height float of the height
     */
    public DrawLine(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return x + " " + y + " " + width + " " + height + " l S";
    }
}
