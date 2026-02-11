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

import com.htmltopdf.security.PdfR4V4Security;

/**
 * Class to hold the XObject information
 */
public class XObject implements Element {
    public static final String IMAGE = "Image";
    private int objectId;
    private int identifier;
    private String subType;
    private int width;
    private int height;
    private byte[] image;

    /**
     * Constructor - Initializes the Element with sub type
     * @param subType String of the sub type
     */
    public XObject(String subType) {
        this.subType = subType;
    }

    /**
     * Set the size of the image
     * @param width int of the width
     * @param height int of the height
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Set the image
     * @param image byte array of the image
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     * Set the Identifier for the imaage
     * @param identifier int identifier of the image
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Get the image identifier
     * @return int of the image identifier
     */
    public int getIdentifier() {
        return identifier;
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
        sb.append("<< /Type /XObject\n");
        sb.append("/Subtype /" + subType + "\n");
        sb.append("/Width " + width + "\n");
        sb.append("/Height " + height + "\n");
        sb.append("/ColorSpace /DeviceRGB\n");
        sb.append("/BitsPerComponent 8\n");
        sb.append("/Filter /DCTDecode\n");
        sb.append("/Length " + image.length + "\n");
        sb.append(">>\n");
        sb.append("stream\n");

        if (encryptionKey != null) {
            image = PdfR4V4Security.encryptObject(encryptionKey, objectId, 0, image);
        }

        byte[] end = "\nendstream\nendobj\n".getBytes(StandardCharsets.ISO_8859_1);
        byte[] start = sb.toString().getBytes(StandardCharsets.ISO_8859_1);
        byte[] output = new byte[start.length + image.length + end.length];
        System.arraycopy(start, 0, output, 0, start.length);
        System.arraycopy(image, 0, output, start.length, image.length);
        System.arraycopy(end, 0, output, start.length + image.length, end.length);

        return output;
    }

    @Override
    public List<Element> buildElementList() {
        // Not needed for this element type
        throw new UnsupportedOperationException("XObject has an Unimplemented method 'buildElementList'");
    }
    
}
