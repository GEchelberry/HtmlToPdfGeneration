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

package com.htmltopdf.element;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.htmltopdf.fonts.Color;
import com.htmltopdf.fonts.Font;
import com.htmltopdf.renderer.Util;

public class ListItemContent implements Element {
    private Font font;
    private String fontId;
    private Color color;
    private int size;
    private String text;
    private int mcid;
    private float xPos;
    private float yPos;
    private boolean isUnderline;
    private boolean isStrikeThrough;
    private int lineThickness = 1;
    private int level = 0;

    /**
     * Constructor - Initialize the List Item Content
     * @param page Page of the parent page object
     * @param font Font object
     * @param size int size of the font
     * @param mcid int of the MCID of the text
     */
    public ListItemContent(Page page, Font font, int size, int mcid) {
        this.font = font;
        this.size = size;
        this.mcid = mcid;
        this.fontId = "F" + page.addFont(font);
    }

    /**
     * Set the text of the list item
     * @param text String of the text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Get the list item text
     * @return String text
     */
    public String getText() {
        return text;
    }

    /**
     * Get the font size
     * @return int of the font size
     */
    public int getSize() {
        return size;
    }

    /**
     * Get the list item font
     * @return Font object
     */
    public Font getFont() {
        return font;
    }

    /**
     * Sets the text color
     * @param color Color object
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Sets if the text is underlined
     * @param isUnderline boolean is underlined
     */
    public void setIsUnderline(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    /**
     * Sets if the text is stiked through
     * @param isStrikeThrough boolean is strike through
     */
    public void setIsStrikeThrough(boolean isStrikeThrough) {
        this.isStrikeThrough = isStrikeThrough;
    }

    /**
     * Sets the line thickness for underline and strike through
     * @param lineThickness int of the line thickness
     */
    public void setLineThickness(int lineThickness) {
        this.lineThickness = lineThickness;
    }

    /**
     * Sets the position of the text
     * @param xPos float of the x position
     * @param yPos float of the y position
     */
    public void setPosition(float xPos, float yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    /**
     * Sets the list level
     * @param level int of the level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Gets the list level
     * @return int of the level
     */
    public int getLevel() {
        return level;
    }

    @Override
    public void setObjectId(int objectId) {
        // Not needed for this Element type
        throw new UnsupportedOperationException("LineItemContent has an Unimplemented method 'setObjectId'");
    }

    @Override
    public int getObjectId() {
        // Not needed for this Element type
        throw new UnsupportedOperationException("LineItemContent has an Unimplemented method 'getObjectId'");
    }

    @Override
    public byte[] toByte(byte[] encryptionKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("/Span << /MCID " + mcid + " >> BDC\n");
        sb.append("/" + fontId + " " + size + " Tf\n");
        if (color != null) {
            sb.append(color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " rg\n");
        } else {
            sb.append("0 0 0 rg\n");
        }
        sb.append("(" + Util.escapeText(text) + ") Tj\n");
        sb.append("EMC\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    /**
     * Gets the bytes for Annotations, underline or strike through text
     * @return byte array of the annotation
     */
    public byte[] getAnnotationBytes() {
        StringBuilder sb = new StringBuilder();
        if (isStrikeThrough || isUnderline) {
            sb.append("/Artifact BMC\n");
            if (isUnderline) {
                if (color != null) {
                    sb.append(color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " RG\n");
                } else {
                    sb.append("0 0 0 RG\n");
                }
                sb.append(lineThickness + " w\n");
                sb.append(xPos + " " + (yPos - 2) + " m\n");
                sb.append((xPos + Util.getTextLength(text, font, size)) + " " + (yPos - 2) + " l\n");
                sb.append("S\n");
            }
            if (isStrikeThrough) {
                if (color != null) {
                    sb.append(color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " RG\n");
                } else {
                    sb.append("0 0 0 RG\n");
                }
                sb.append(lineThickness + " w\n");
                sb.append(xPos + " " + (yPos + (Util.getTextHeight(font, size) / 4)) + " m\n");
                sb.append((xPos + Util.getTextLength(text, font, size)) + " " + (yPos + (Util.getTextHeight(font, size) / 4)) + " l\n");
                sb.append("S\n");
            }
            sb.append("EMC\n");
        }

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        // Not needed for this Element type
        throw new UnsupportedOperationException("LineItemContent has an Unimplemented method 'buildElementList'");
    }
    
}
