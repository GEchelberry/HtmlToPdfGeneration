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

import com.htmltopdf.renderer.Util;
import com.htmltopdf.security.PdfR4V4Security;

/**
 * Class to hold all the Content stream data for a page
 */
public class ContentStream implements Element {
    private int objectId;
    private List<Element> contents;
    private boolean compressContent = true;

    /**
     * Constructor - Initialize variables
     * @param objectId
     */
    public ContentStream(int objectId) {
        setObjectId(objectId);
        contents = new ArrayList<>();
    }

    /**
     * Add content to the content stream
     * @param content Element of the content
     */
    public void addContent(Element content) {
        contents.add(content);
    }

    /**
     * Inserts content before the current content
     * @param content Element of the content to be inserted
     */
    public void insertContent(Element content) {
        contents.add(contents.size() - 1, content);
    }

    /**
     * Sets the compress content, default is true.
     * Set to false for debugging the content stream
     * @param compressContent boolean compress the content
     */
    public void setCompressContent(boolean compressContent) {
        this.compressContent = compressContent;
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
        byte[] content = new byte[0];
        for (Element c : contents) {
            byte[] childContent = c.toByte(encryptionKey);
            byte[] copy = new byte[content.length + childContent.length];
            System.arraycopy(content, 0, copy, 0, content.length);
            System.arraycopy(childContent, 0, copy, content.length, childContent.length);
            content = copy;
        }

        if (compressContent) {
            content = Util.compressStream(content);
        }
        if (encryptionKey != null) {
            content = PdfR4V4Security.encryptObject(encryptionKey, objectId, 0, content);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(objectId + " 0 obj\n");
        sb.append("<< /Length " + content.length + (compressContent ? " /Filter /FlateDecode >>\n" : ">>\n"));
        sb.append("stream\n");
        byte[] start = sb.toString().getBytes(StandardCharsets.ISO_8859_1);
        byte[] end = "\nendstream\nendobj\n".getBytes(StandardCharsets.ISO_8859_1);

        byte[] output = new byte[start.length + content.length + end.length];
        System.arraycopy(start, 0, output, 0, start.length);
        System.arraycopy(content, 0, output, start.length, content.length);
        System.arraycopy(end, 0, output, start.length + content.length, end.length);

        return output;
    }

    @Override
    public List<Element> buildElementList() {
        // Not needed by this element type
        throw new UnsupportedOperationException("ContentStream has an Unimplemented method 'buildElementList'");
    }
    
}
