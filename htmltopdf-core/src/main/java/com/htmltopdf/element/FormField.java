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
 * Class to hold the form field data
 */
public class FormField implements Element {
    private int objectId;
    private String type;
    private String text;
    private int flags;
    private List<Element> annots;

    /**
     * Constructor - Initialize object
     * @param type String of the type
     * @param flags int of the flags
     */
    public FormField(String type, int flags) {
        this.type = type;
        this.flags = flags;
    }

    /**
     * Sets the field text
     * @param text String text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Adds the widget annotation to the form fields
     * @param widget Element of the widget
     */
    public void addWidget(Element widget) {
        annots.add(widget);
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
        sb.append("<< /FT /" + type + "\n");
        if (encryptionKey != null) {
            byte[] encrypted = PdfR4V4Security.encryptString(encryptionKey, objectId, 0, Util.escapeText(text).getBytes(StandardCharsets.ISO_8859_1));
            sb.append("/T <" + Util.byteToHex(encrypted) + ">\n");
        } else {
            sb.append("/T (" + Util.escapeText(text) + ")\n");
        }
        sb.append("/Ff " + flags + "\n");
        sb.append("/Kids [");
        for (int i = 0; i < annots.size(); i++) {
            sb.append(annots.get(i).getObjectId() + " 0 R");
            if (i < annots.size() - 1) {
                sb.append(" ");
            }
        }
        sb.append("]\n");
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
