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

public class LinkElement implements Element {
    private int objectId;
    private Element parent;
    private Page page;
    private String altText;
    private int mcid;
    private Element annotation;

    /**
     * Sets the parent element
     * @param parent Element of the parent object
     */
    public void setParent(Element parent) {
        this.parent = parent;
    }

    /**
     * Sets the page for the link
     * @param page Page object
     */
    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * Sets the alternate text for the link
     * @param altText String alternate text
     */
    public void setAltText(String altText) {
        this.altText = altText;
    }

    /**
     * Sets the MCID of the object
     * @param mcid int of the mcid
     */
    public void setMcid(int mcid) {
        this.mcid = mcid;
    }

    /**
     * Sets the annotation object for the link
     * @param annotation Element of the annotation
     */
    public void setAnnotation(Element annotation) {
        this.annotation = annotation;
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
        sb.append("<< /Type /StructElem\n");
        sb.append("/S /Link\n");
        sb.append("/P " + parent.getObjectId() + " 0 R\n");
        if (page != null) {
            sb.append("/Pg " + page.getObjectId() + "\n");
        }
        sb.append("/K [" + mcid + " << /Type /OBJR /Obj " + annotation.getObjectId() + " 0 R >>]\n");
        if (altText != null) {
            sb.append("/A (" + Util.escapeText(altText) + ")\n");
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
