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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;

import com.htmltopdf.element.Element;

/**
 * Class to hold the font information
 */
public class Font implements Element {
    protected int objectId;
    private FontName fontName;
    protected FontDecriptor fontDecriptor;
    protected int firstChar = 0;
    protected int lastChar = 0;
    protected int unitsPerEm;
    protected List<Integer> widths;

    /**
     * Constructor - Initialize variables
     * @param objectId int of the object id
     * @param fontName FontName for the font
     */
    public Font(int objectId, FontName fontName) {
        setObjectId(objectId);
        this.fontName = fontName;
        this.widths = new ArrayList<>();
    }

    /**
     * Sets the Font Descriptor
     * @param fontDecriptor FontDescriptor Element
     */
    public void setFontDescriptor(FontDecriptor fontDecriptor) {
        this.fontDecriptor = fontDecriptor;
    }

    /**
     * Get the Font Descriptor object
     * @return FontDescriptor element
     */
    public FontDecriptor getFontDecriptor() {
        return fontDecriptor;
    }

    /**
     * Set the first character
     * @param firstChar int of the first character
     */
    public void setFirstChar(int firstChar) {
        this.firstChar = firstChar;
    }

    /**
     * Set the last character
     * @param lastChar int of the last character
     */
    public void setLastChar(int lastChar) {
        this.lastChar = lastChar;
    }

    /**
     * Get the font name
     * @return FontName object
     */
    public FontName getFontName() {
        return fontName;
    }

    /**
     * Sets the Units per Em
     * @param unitsPerEm int of the units per em value
     */
    public void setUnitsPerEm(int unitsPerEm) {
        this.unitsPerEm = unitsPerEm;
    }

    /**
     * Get the Units per EM value
     * @return int of the Units per Em value
     */
    public int getUnitsPerEm() {
        return unitsPerEm;
    }

    /**
     * Gets the width of a text string
     * @param text String of text
     * @return int of the advance length
     */
    public int getTextWidth(String text) {
        int advanceWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            advanceWidth += widths.get(text.charAt(i) - firstChar);
        }

        return advanceWidth;
    }

    public void buildWidthArray() {
        TTFParser parser = new TTFParser();

        try (InputStream inputStream = new FileInputStream(fontName.getPath());
                TrueTypeFont ttFont = parser.parseEmbedded(inputStream)) {
            HorizontalMetricsTable hmtx = ttFont.getHorizontalMetrics();
            int unitsPerEm = ttFont.getHeader().getUnitsPerEm();
            CmapLookup cmap = ttFont.getUnicodeCmapLookup();

            for (int code = firstChar; code <= lastChar; code++) {
                int glyphId = cmap.getGlyphId(code);
                if (glyphId > 0) {
                    widths.add(Math.round((hmtx.getAdvanceWidth(glyphId) * 1000f) / unitsPerEm));
                }
            }
        } catch (FileNotFoundException notFoundEx) {
            throw new RuntimeException("Font File not found at specified Path " + fontName.getPath());
        } catch (IOException ioEx) {
            throw new RuntimeException("IO Exception encountered while building the width array for font " + fontName.getName());
        }
    }

    @Override
    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    @Override
    public int getObjectId() {
        return objectId;
    }

    @Override
    public byte[] toByte(byte[] encryptionKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(objectId + " 0 obj\n");
        sb.append("<< /Type /Font\n");
        sb.append("/Subtype /" + fontName.getSubType() + "\n");
        sb.append("/BaseFont /" + fontName.getName() + "\n");
        sb.append("/FirstChar " + firstChar + "\n");
        sb.append("/LastChar " + lastChar + "\n");
        sb.append("/Widths [");
        for (int i = 0; i < widths.size(); i++) {
            sb.append(widths.get(i));
            if (i < widths.size() - 1) {
                sb.append(" ");
            }
        }
        sb.append("]\n");
        sb.append("/Encoding /WinAnsiEncoding\n");
        sb.append("/FontDescriptor " + fontDecriptor.getObjectId() + " 0 R\n");
        sb.append(">>\nendobj\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        List<Element> elements = new ArrayList<>();
        elements.add(this);
        elements.addAll(fontDecriptor.buildElementList());

        return elements;
    }
    
}
