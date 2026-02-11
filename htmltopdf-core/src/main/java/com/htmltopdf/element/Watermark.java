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

import com.htmltopdf.fonts.Font;

/**
 * Class to apply a watermark to a page
 */
public class Watermark implements Element {
    public static final String WATERMARK_TEXT = "DRAFT";
    private int fontId;
    private float pageHeight;
    private float pageWidth;

    /**
     * Constructore - initialize object
     * @param page Page object to add the watermark
     * @param pageSize Page.Size of the desired page
     * @param font Font for the desired font
     */
    public Watermark(Page page, Page.Size pageSize, Font font) {
        this.fontId = page.addFont(font);
        this.pageHeight = pageSize.getY();
        this.pageWidth = pageSize.getX();
    }

    @Override
    public void setObjectId(int objectId) {
        // Not needed for this element type
        throw new UnsupportedOperationException("Watermark has an Unimplemented method 'setObjectId'");
    }

    @Override
    public int getObjectId() {
        // Not needed for this element type
        throw new UnsupportedOperationException("Watermark has an Unimplemented method 'getObjectId'");
    }

    @Override
    public byte[] toByte(byte[] encryptionKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("/Artifact << /Subtype /Watermark >> BDC\n");
        sb.append("q\n");
        sb.append("/GS1 gs\n");
        sb.append("BT\n");
        sb.append("/F" + fontId + " " + Math.sqrt(Math.pow(pageHeight, 2.0) + Math.pow(pageWidth, 2.0)) / (WATERMARK_TEXT.length()) + "Tf\n");
        sb.append("0.707 0.707 -0.707 0.707 " + (140) + " " + (150) + "Tm\n");
        sb.append("(" + WATERMARK_TEXT + ") Tj\n");
        sb.append("ET\n");
        sb.append("Q\n");
        sb.append("EMC\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        // Not needed for this element type
        throw new UnsupportedOperationException("Wartermark has an Unimplemented method 'buildElementList'");
    }
    
}
