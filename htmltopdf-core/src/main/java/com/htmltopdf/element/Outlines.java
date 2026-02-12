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

/**
 * Manages the bookmark list
 */
public class Outlines implements Element {
    private int objectId;
    private List<Outline> outlines;

    /**
     * Constructor - intialize variables
     */
    public Outlines() {
        this.outlines = new ArrayList<>();
    }

    /**
     * Add a new outline and set next and previous pointers
     * @param outline Outline object
     */
    public void addNewOutline(Outline outline) {
        if (outlines.size() > 0) {
            Outline o = outlines.get(outlines.size() - 1);
            o.setNext(outline);
            outline.setPrev(o);
        }
        outlines.add(outline);
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
        if (!outlines.isEmpty()) {
            sb.append(objectId + " 0 obj\n");
            sb.append("<< /Type /Outlines\n");
            sb.append("/First " + outlines.get(0).getObjectId() + " 0 R\n");
            sb.append("/Last " + outlines.get(outlines.size() - 1).getObjectId() + " 0 R\n");
            sb.append("/Count " + outlines.size() + "\n");
            sb.append(">>\n");
            sb.append("endobj\n");
        }

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        List<Element> elements = new ArrayList<>();
        elements.add(this);
        elements.addAll(outlines);
        return elements;
    }
    
}
