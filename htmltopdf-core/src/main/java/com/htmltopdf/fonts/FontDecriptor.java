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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.htmltopdf.element.Element;

/**
 * Class to hold the Font Descriptor information
 */
public class FontDecriptor implements Element {
    private int objectId;
    private String fontName;
    private int flags;
    private String fontBox;
    private float italicAngle;
    private int ascent;
    private int descent;
    private int capHeight;
    private String stemV = "80";
    private FontFile fontFile;

    /**
     * Constructor - Initialize all the variables
     * @param fontName
     */
    public FontDecriptor(String fontName) {
        this.fontName = fontName;
    }

    /**
     * Set the Font File
     * @param fontFile FontFile Element
     */
    public void setFontFile(FontFile fontFile) {
        this.fontFile = fontFile;
    }

    /**
     * Set the flags
     * @param flags int of the flags value
     */
    public void setFlags(int flags) {
        this.flags = flags;
    }

    /**
     * Set the Font Box
     * @param fontBox String of the font box
     */
    public void setFontBox(String fontBox) {
        this.fontBox = fontBox;
    }

    /**
     * Sets the Italic Angle
     * @param italicAngle float of the italic angle
     */
    public void setItalicAngle(float italicAngle) {
        this.italicAngle = italicAngle;
    }

    /**
     * Set the ascent value
     * @param ascent int of the ascent
     */
    public void setAscent(int ascent) {
        this.ascent = ascent;
    }

    /**
     * Get the ascent value
     * @return int of the ascent value
     */
    public int getAscent() {
        return ascent;
    }

    /**
     * Set the Descent value
     * @param descent int of the descent value
     */
    public void setDescent(int descent) {
        this.descent = descent;
    }

    /**
     * Get the Descent value
     * @return int of the descent value
     */
    public int getDescent() {
        return descent;
    }

    /**
     * Set the cap height value
     * @param capHeight int of the cap height value
     */
    public void setCapHeight(int capHeight) {
        this.capHeight = capHeight;
    }

    /**
     * Set the Stem V value
     * @param stemV String of the stem V value
     */
    public void setStemV(String stemV) {
        this.stemV = stemV;
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
        sb.append("<< /Type /FontDescriptor\n");
        sb.append("/FontName /" + fontName + "\n");
        sb.append("/Flags " + flags + "\n");
        sb.append("/FontBBox " + fontBox + "\n");
        sb.append("/ItalicAngle " + italicAngle + "\n");
        sb.append("/Ascent " + ascent + "\n");
        sb.append("/Descent " + descent + "\n");
        sb.append("/CapHeight " + capHeight + "\n");
        sb.append("/StemV " + stemV + "\n");
        sb.append("/FontFile2 " + fontFile.getObjectId() + " 0 R\n");
        sb.append(">>\n");
        sb.append("endobj\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        List<Element> elements = new ArrayList<>();
        elements.add(this);
        elements.add(fontFile);

        return elements;
    }
}
