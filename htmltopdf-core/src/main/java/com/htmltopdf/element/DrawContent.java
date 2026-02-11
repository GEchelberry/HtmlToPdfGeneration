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

import com.htmltopdf.draw.DrawBox;
import com.htmltopdf.draw.DrawLine;
import com.htmltopdf.fonts.Color;

public class DrawContent implements Element {
    private String type;
    private int mcid = -1;
    private float lineThickness;
    private Color color;
    private Color fillColor;
    private List<DrawBox> boxes;
    private List<DrawLine> lines;

    /**
     * Constructor - Initialize object
     * @param lineThickness float of the line thickness
     */
    public DrawContent(float lineThickness) {
        this.lineThickness = lineThickness;
    }

    /**
     * Adds a box to the content stream
     * @param box DrawBox of the box
     */
    public void addBox(DrawBox box) {
        boxes.add(box);
    }

    /**
     * Adds a line to the content stream
     * @param line DrawLine of the line
     */
    public void addLine(DrawLine line) {
        lines.add(line);
    }

    /**
     * Sets the line color
     * @param color Color object
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Sets the fill color
     * @param fillColor Color object
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * Set the type
     * @param type String of the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the mcid
     * @param mcid int of the mcid
     */
    public void setMcid(int mcid) {
        this.mcid = mcid;
    }

    @Override
    public void setObjectId(int objectId) {
        // Not needed for this Element type
        throw new UnsupportedOperationException("DrawContent has an Unimplemented method 'setObjectId'");
    }

    @Override
    public int getObjectId() {
        // Not needed for this Element type
        throw new UnsupportedOperationException("DrawContent has an Unimplemented method 'getObjectId'");
    }

    @Override
    public byte[] toByte(byte[] encryptionKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("q\n");
        if (type != null && mcid != -1) {
            sb.append("/" + type + " << /MCID " + mcid + " >> DBC\n");
        } else {
            sb.append("/Artifact BMC\n");
        }
        if (color != null) {
            sb.append(color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " RG\n");
        } else {
            sb.append("0 0 0 RG\n");
        }
        if (fillColor != null) {
            sb.append(color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " rg\n");
        } else {
            sb.append("1 1 1 rg\n");
        }
        sb.append(lineThickness + " w\n");
        for (DrawBox box : boxes) {
            sb.append(box.toString() + "\n");
            sb.append("B\n");
        }
        for (DrawLine line : lines) {
            sb.append(line.toString() + "\n");
        }
        sb.append("Q\n");
        sb.append("EMC\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        // Not needed for this Element type
        throw new UnsupportedOperationException("DrawContent has an Unimplemented method 'buildElementList'");
    }
    
}
