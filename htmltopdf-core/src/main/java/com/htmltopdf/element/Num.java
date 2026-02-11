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
 * Class to hold the number array
 */
public class Num implements Element {
    private int objectId;
    private List<Element> refs;
    private boolean isAnnotation;

    /**
     * Constructor - Initialize variables
     */
    public Num() {
        this.refs = new ArrayList<>();
    }

    /**
     * Add an element to the reference array
     * @param element Element to add to the reference array
     */
    public void addReference(Element element) {
        refs.add(element);
    }

    /**
     * Get the list of references
     * @return List of Element references
     */
    public List<Element> getReference() {
        return refs;
    }

    /**
     * Removes the last entry in the reference list
     */
    public void removeLastReference() {
        refs.remove(refs.size() - 1);
    }

    /**
     * Sets if the reference is an annotation
     * @param isAnnotation boolean is annotation
     */
    public void setIsAnnotation(boolean isAnnotation) {
        this.isAnnotation = isAnnotation;
    }

    /**
     * Get is the object an annotation
     * @return boolean is annotation
     */
    public boolean getIsAnnotation() {
        return isAnnotation;
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
        if (!isAnnotation) {
            sb.append(objectId + " 0 obj\n");
            sb.append("[");
            if (!refs.isEmpty()) {
                for (int i = 0; i < refs.size(); i++) {
                    sb.append(refs.get(i).getObjectId() + " 0 R");
                    if (i < refs.size() - 1) {
                        sb.append(" ");
                    }
                }
            }
            sb.append("]\n");
            sb.append("endobj\n");
        }

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        List<Element> elements = new ArrayList<>();
        elements.add(this);
        return elements;
    }
    
}
