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
 * Class to hold the struct tree root structure
 */
public class StructTreeRoot implements Element {
    private int objectId;
    private Element parentTree;
    private int parentTreeNextKey;
    private List<StructElement> kids;

    /**
     * Constructor - Initialize variables
     */
    public StructTreeRoot() {
        this.kids = new ArrayList<>();
    }

    /**
     * Add a child element to the structure tree
     * @param kid StructElement of the child element
     */
    public void addKid(StructElement kid) {
        kids.add(kid);
    }

    /**
     * Sets the Parent Tree Next Key
     * @param parentTreeNextKey int of the next key value
     */
    public void setParentTreeNextKey(int parentTreeNextKey) {
        this.parentTreeNextKey = parentTreeNextKey;
    }

    /**
     * Sets the parent tree element
     * @param parentTree Element of the parent tree
     */
    public void setParentTree(Element parentTree) {
        this.parentTree = parentTree;
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
        sb.append("<< /Type /StructTreeRoot\n");
        if (!kids.isEmpty()) {
            if (kids.size() == 1) {
                sb.append("/K " + kids.get(0).getObjectId() + " 0 R\n");
            } else {
                sb.append("/K [");
                for (int i = 0; i < kids.size(); i++) {
                    sb.append(kids.get(i).getObjectId() + " 0 R");
                    if (i == kids.size() - 1) {
                        sb.append("]\n");
                    } else {
                        sb.append(" ");
                    }
                }
            }
        }
        sb.append("/RoleMap << /Span /Span /Page /Part >>\n");
        if (parentTree != null) {
            sb.append("/ParentTree " + parentTree.getObjectId() + " 0 R\n");
        }
        sb.append("/ParentNextKey " + parentTreeNextKey + "\n");
        sb.append(">>\n");
        sb.append("endobj\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        List<Element> elements = new ArrayList<>();
        elements.add(this);
        for (StructElement child : kids) {
            elements.addAll(child.buildElementList());
        }
        elements.addAll(parentTree.buildElementList());

        return elements;
    }
    
}
