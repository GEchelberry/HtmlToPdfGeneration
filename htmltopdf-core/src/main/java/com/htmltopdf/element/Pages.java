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
 * Class to Hold the pages PDF structure
 */
public class Pages implements Element {
    private int objectId;
    private List<Page> kids;

    /**
     * Constructor - Initialize variables and set object Id
     * @param objectId
     */
    public Pages(int objectId) {
        this.setObjectId(objectId);
    }

    /**
     * Add a new page to the pages object
     * @param page Page element
     */
    public void addKid(Page page) {
        kids.add(page);
    }

    /**
     * Gets the number of pages in the PDF
     * @return int of the page count
     */
    public int getKidCount() {
        return kids.size();
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
        sb.append("<< /Type /Pages\n");
        sb.append("/Count " + kids.size() + "\n");
        sb.append("/Kids [");
        for (int i = 0; i < kids.size(); i++) {
            sb.append(kids.get(i).getObjectId() + " 0 R");
            if (i < kids.size() - 1) {
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
