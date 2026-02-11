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

/**
 * Class to hold text content
 */
public class TextContent implements Element {
    public static final String ALIGN_LEFT = "left";
    public static final String ALIGN_RIGHT = "right";
    public static final String ALIGN_CENTER = "center";
    private Font font;
    private String type;
    private String fontId;
    private Color color;
    private int size;
    private List<String> lines;
    private float xPos;
    private float yPos;
    private int mcid;
    private boolean isUnderline = false;
    private boolean isStrikeThrough = false;
    private int lineThickness = 1;
    private String alignment;
    private int leftMargin;

    /**
     * Constructor - Initialize variables
     * @param page Page of the page the content appears
     * @param type String of the structure type
     * @param font Font of the desired font
     * @param size int of the font size
     * @param mcid int of the mcid
     */
    public TextContent(Page page, String type, Font font, int size, int mcid) {
        this.font = font;
        this.type = type;
        this.size = size;
        this.mcid = mcid;
        this.fontId = "F" + page.addFont(font);
    }

    /**
     * Sets the position of the content on the page
     * @param xPos float of the X position
     * @param yPos float of the Y position
     */
    public void setPosition(float xPos, float yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    /**
     * Sets the X Position of the text content
     * @param xPos float of the X position
     */
    public void setXPosition(float xPos) {
        this.xPos = xPos;
    }

    /**
     * Add lines to the content
     * @param lines List of String of the lines
     */
    public void addLines(List<String> lines) {
        this.lines = lines;
    }

    /**
     * Get the lines of the Text content
     * @return List of String of the lines
     */
    public List<String> getLines() {
        return lines;
    }

    /**
     * Sets the text color
     * @param color Color object
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Sets if the text should be underlined
     * @param isUnderline boolean is Underlined
     */
    public void setIsUnderline(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    /**
     * Sets if the text should have a strike through
     * @param isStrikeThrough boolean is strike through
     */
    public void setIsStrikeThrough(boolean isStrikeThrough) {
        this.isStrikeThrough = isStrikeThrough;
    }

    /**
     * Sets the thickness of the underline or strikethrough
     * @param lineThickness int of the line thickness, default is 1
     */
    public void setLineThickness(int lineThickness) {
        this.lineThickness = lineThickness;
    }

    /**
     * Sets the alignment of the text
     * @param alignment String alignment
     */
    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    /**
     * Sets the left margin for the text
     * @param leftMargin int of the left margin
     */
    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    @Override
    public void setObjectId(int objectId) {
        // Not needed for this Element type
        throw new UnsupportedOperationException("TextContent has an Unimplemented method 'setObjectId'");
    }

    @Override
    public int getObjectId() {
        // Not needed for this element type
        throw new UnsupportedOperationException("TextContent has an Unimplemented method 'getObjectId'");
    }

    @Override
    public byte[] toByte(byte[] encryptionKey) {
        float[] xStarts = new float[lines.size()];
        StringBuilder sb = new StringBuilder();
        sb.append("/" + type + " <</MCID " + mcid + ">> BDC\n");
        sb.append("BT\n");
        sb.append("/" + fontId + " " + size + " Tf\n");
        if (color != null) {
            sb.append(color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " rg\n");
        } else {
            sb.append("0 0 0 rg\n");
        }
        for (int i = 0; i < lines.size(); i++) {
            if (i == 0) {
                sb.append(xPos + " " + yPos + " Td\n");
                xStarts[i] = xPos;
            } else {
                sb.append(getXOffset(i) + " " + (0 - size) + " Td\n");
                xStarts[i] = xPos + getXOffset(i);
            }
            sb.append("(" + Util.escapeText(lines.get(i)) + ") Tj\n");
        }
        sb.append("ET\n");
        sb.append("EMC\n");
        if (isStrikeThrough || isUnderline) {
            sb.append("/Artifact BMC\n");
            if (isUnderline) {
                if (color != null) {
                    sb.append(color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " RG\n");
                } else {
                    sb.append("0 0 0 RG\n");
                }
                sb.append(lineThickness + " w\n");
                float y = 0.0f;
                for (int i = 0; i < lines.size(); i++) {
                    if (i == 0) {
                        y = yPos - 2;
                        sb.append(xStarts[i] + " " + y + " m\n");
                    } else {
                        y = y - size;
                        sb.append(xStarts[i] + " " + y + " m\n");
                    }
                    sb.append((xStarts[i] + Util.getTextLength(lines.get(i), font, size)) + " " + y + " l\n");
                    sb.append("S\n");
                }
                sb.append("EMC\n");
            }
            if (isStrikeThrough) {
                if (color != null) {
                    sb.append(color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " RG\n");
                } else {
                    sb.append("0 0 0 RG\n");
                }
                sb.append(lineThickness + " w\n");
                float y = 0.0f;
                for (int i = 0; i < lines.size(); i++) {
                    if (i == 0) {
                        y = yPos + (Util.getTextHeight(font, size) / 4);
                        sb.append(xStarts[i] + " " + y + " m\n");
                    } else {
                        y = (y - size);
                        sb.append(xStarts[i] + " " + y + " m\n");
                    }
                    sb.append((xStarts[i] + Util.getTextLength(lines.get(i), font, size)) + " " + y + " l\n");
                    sb.append("S\n");
                }
                sb.append("EMC\n");
            }
        }

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        // Not needed for this Element type
        throw new UnsupportedOperationException("TextContent has an Unimplemented method 'buildElementList'");
    }
    
    private int getXOffset(int index) {
        if (alignment == null) {
            return 0;
        }

        int lastL = (size * font.getTextWidth(lines.get(index - 1))) / 1000;
        int l = (size * font.getTextWidth(lines.get(index))) / 1000;
        switch (alignment) {
            case TextContent.ALIGN_RIGHT:
                return lastL - l;
            case TextContent.ALIGN_CENTER:
                return (lastL / 2) - (l / 2);
            default:
                int x = 0;
                if (xPos > leftMargin) {
                    x = (int) (leftMargin - xPos);
                    xPos = 0;
                }
                return x;
        }
    }
}
