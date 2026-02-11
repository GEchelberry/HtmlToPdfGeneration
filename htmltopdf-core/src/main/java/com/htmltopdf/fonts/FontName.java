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

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum FontName {
    COURIER("CourierPrime-Regular ", "TrueType", "src/main/resources/fonts/Courier_Prime/CourierPrime-Regular.ttf"),
    COURIER_BOLD("CourierPrime-Bold", "TrueType", "src/main/resources/fonts/Courier_Prime/CourierPrime-Bold.ttf"),
    COURIER_ITALIC("CourierPrime-Italic", "TrueType", "src/main/resources/fonts/Courier_Prime/CourierPrime-Italic.ttf"),
    COURIER_BOLD_ITALIC("CourierPrime-BoldItalic", "TrueType", "src/main/resources/fonts/Courier_Prime/CourierPrime-BoldItalic.ttf"),
    HELVETICA("Inter_18pt-Regular", "TrueType", "src/main/resources/fonts/Inter/static/Inter_18pt-Regular.ttf"),
    HELVETICA_BOLD("Inter_24pt-Bold", "TrueType", "src/main/resources/fonts/Inter/static/Inter_24pt-Bold.ttf"),
    HELVETICA_ITALIC("Inter_18pt-Italic", "TrueType", "src/main/resources/fonts/Inter/static/Inter_18pt-Italic.ttf"),
    HELVETICA_BOLD_ITALIC("Inter_24pt-BoldItalic", "TrueType", "src/main/resources/fonts/Inter/static/Inter_24pt-BoldItalic.ttf"),
    TIMES_ROMAN("PTSerif-Regular", "TrueType", "src/main/resources/fonts/PT_Serif/PTSerif-Regular.ttf"),
    TIMES_ROMAN_BOLD("PTSerif-Bold", "TrueType", "src/main/resources/fonts/PT_Serif/PTSerif-Bold.ttf"),
    TIMES_ROMAN_ITALIC("PTSerif-Italic", "TrueType", "src/main/resources/fonts/PT_Serif/PTSerif-Italic.ttf"),
    TIMES_ROMAN_BOLD_ITALIC("PTSerif-BoldItalic", "TrueType", "src/main/resources/fonts/PT_Serif/PTSerif-BoldItalic.ttf"),
    ARIAL("Arimo-Regular", "TrueType", "src/main/resources/fonts/Arimo/static/Arimo-Regular.ttf"),
    ARIAL_BOLD("Arimo-Bold", "TrueType", "src/main/resources/fonts/Arimo/static/Arimo-Bold.ttf"),
    ARIAL_ITALIC("Arimo-Italic", "TrueType", "src/main/resources/fonts/Arimo/static/Arimo-Italic.ttf"),
    ARIAL_BOLD_ITALIC("Arimo-BoldItalic", "TrueType", "src/main/resources/fonts/Arimo/static/Arimo-BoldItalic.ttf");

    private final String name;
    private final String subType;
    private final String path;
    
    /**
     * Initializes the font
     * @param name String of the constant name
     * @param subType String of the constant sub type
     * @param path String of the constant path
     */
    FontName(String name, String subType, String path) {
        this.name = name;
        this.subType = subType;
        this.path = path;
    }

    /**
     * Get the Font name
     * @return String of the font name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the Font Sub type
     * @return String of the sub type
     */
    public String getSubType() {
        return subType;
    }

    /**
     * Get the Font Relative Path
     * @return String of the font's relative path
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the FontName by name
     * @param value String name of the font
     * @return FontName for the font if it exists
     */
    public static FontName getValueOf(String value) {
        if (value == null) {
            return null;
        }
        return BY_NAME.get(value.trim().toLowerCase());
    }

    /**
     * Builds a list of all the font names
     */
    private static final Map<String, FontName> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(
        f -> f.name.trim().toLowerCase(),
        f -> f
    ));
}
