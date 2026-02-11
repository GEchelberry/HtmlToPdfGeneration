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
import java.util.ArrayList;
import java.util.List;

import com.htmltopdf.security.PdfR4V4Security;

public class AppearanceStream implements Element {
    public static final String OFF = "Off";
    public static final String ON = "Yes";
    private int objectId;
    private String type = null;
    private String font;
    private int size;
    private String text;
    private float height = 0;
    private float width = 0;

    /**
     * Default Constructor
     */
    public AppearanceStream() {

    }

    /**
     * Constructor - Initialize variables
     * @param font Stirng of the font identifier
     * @param size int of the font size
     * @param text Stirng of the text
     */
    public AppearanceStream(String font, int size, String text) {
        this.font = font;
        this.size = size;
        this.text = text;
    }

    /**
     * Sets the stream type
     * @param type String of the type of stream
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the stream type
     * @return String of the type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the Box Size for the Appearance
     * @param width int of the width
     * @param height int of the height
     */
    public void setBoxSize(float width, float height) {
        this.width = width;
        this.height = height;
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
        if (type == null) {
            sb.append("q\n");
            sb.append("BT\n");
            sb.append("/" + font + " " + size + " Tf\n");
            sb.append("0 0 0 rg\n");
            sb.append("2 4 Td\n");
            sb.append("(" + text + ") Tj\n");
            sb.append("ET\n");
            sb.append("Q\n");
        } else {
            sb.append("q\n");
            sb.append("0.8 0.8 0.8 rg\n");
            sb.append("0 0 " + width + " " + height + " re\n");
            sb.append("f\n");
            if (type.equals(AppearanceStream.ON)) {
                float w = width - 2;
                float h = height - 2;
                sb.append("0.5w\n");
                sb.append("0 0 0 RG\n");
                sb.append("2 " + h + " m\n");
                sb.append(w + " 2 l\n");
                sb.append("S\n");
                sb.append(w + " " + h + " m\n");
                sb.append("2 2 l\n");
                sb.append("S\n");
            }
            sb.append("Q\n");
        }

        byte[] bytes = sb.toString().getBytes(StandardCharsets.ISO_8859_1);

        StringBuilder start = new StringBuilder();
        if (type == null) {
            start.append(objectId + " 0 obj\n");
            start.append("<< /Type /XObject\n");
            start.append("/Subtype /Form\n");
            start.append("/BBox [0 0 " + width + " " + height + "]\n");
            start.append("/Length " + bytes.length + "\n");
            start.append(">>\n");
            start.append("stream\n");
        } else {
            start.append(objectId + " 0 obj\n");
            start.append("<< /Type /XObject\n");
            start.append("/Subtype /Form\n");
            start.append("/BBox [0 0 " + width + " " + height + "]\n");
            start.append("/Length " + bytes.length + "\n");
            start.append(">>\n");
            start.append("stream\n");
        }
        byte[] starting = start.toString().getBytes(StandardCharsets.ISO_8859_1);
        byte[] ending = "\nendstream\nendobj\n".getBytes(StandardCharsets.ISO_8859_1);
        
        if (encryptionKey != null) {
            bytes = PdfR4V4Security.encryptObject(encryptionKey, objectId, 0, bytes);
        }
        byte[] output = new byte[starting.length + bytes.length + ending.length];
        System.arraycopy(starting, 0, output, 0, starting.length);
        System.arraycopy(bytes, 0, output, starting.length, bytes.length);
        System.arraycopy(ending, 0, output, starting.length + bytes.length, ending.length);

        return output;
    }

    @Override
    public List<Element> buildElementList() {
        List<Element> elements = new ArrayList<>();
        elements.add(this);

        return elements;
    }
    
}
