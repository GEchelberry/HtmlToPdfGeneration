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
 * Class to handle annotations
 */
public class Annotation implements Element {
    public static final String LINK = "Link";
    public static final String WIDGET = "Widget";
    private int objectId;
    private String type;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private String uri;
    private int structParents = -1;
    private Page page;
    private String content;

    /**
     * Constructor - Initialize the Annotation object
     * @param type String of the type
     * @param startX int of the starting X
     * @param startY int of the starting Y
     * @param endX int of the ending X
     * @param endY int of the ending Y
     */
    public Annotation(String type, int startX, int startY, int endX, int endY) {
        this.type = type;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    /**
     * Sets the URI for a link
     * @param uri String of the URI
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Sets the struct parents for the annotation
     * @param structParents int of the struct parents
     */
    public void setStructParents(int structParents) {
        this.structParents = structParents;
    }

    /**
     * Sets the page of the annotation
     * @param page Page object
     */
    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * Sets the annotations content
     * @param content String of the content
     */
    public void setContent(String content) {
        this.content = content;
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
        sb.append("<< /Type /Annot\n");
        sb.append("/Subtype /" + type + "\n");
        sb.append("/Rect [" + startX + " " + startY + " " + endX + " " + endY + "]\n");
        sb.append("/Border [0 0 0]\n");
        if (encryptionKey != null) {
            byte[] encryptedUri = PdfR4V4Security.encryptString(encryptionKey, objectId, 0, Util.escapeText(uri).getBytes(StandardCharsets.ISO_8859_1));
            byte[] encrytedContent = PdfR4V4Security.encryptString(encryptionKey, objectId, 0, Util.escapeText(content).getBytes(StandardCharsets.ISO_8859_1));
            sb.append("/A << /S /URI / URI <" + Util.byteToHex(encryptedUri) + " > >>\n");
            sb.append("/Contents <" + Util.byteToHex(encrytedContent) + ">\n");
        } else {
            sb.append("/A << /S /URI /URI (" + uri + ") >>\n");
            sb.append("/Contents (" + Util.escapeText(content) + ")\n");
        }
        sb.append("/P " + page.getObjectId() + " 0 R\n");
        if (structParents != -1) {
            sb.append("/StructParent " + structParents + "\n");
        }
        sb.append(">>\n");
        sb.append("endobj\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        List<Element> elements = new ArrayList<>();
        elements.add(this);
        return elements;
    }
    
}
