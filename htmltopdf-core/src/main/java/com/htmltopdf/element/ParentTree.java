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
 * Class to build the Parent tree
 */
public class ParentTree implements Element {
    private int objectId;
    private List<Num> nums;

    /**
     * Constructor - Initialize variables
     */
    public ParentTree() {
        this.nums = new ArrayList<>();
    }

    /**
     * Add a number array to the Parent tree
     * @param num Num array
     */
    public void addNums(Num num) {
        nums.add(num);
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
        sb.append("<< /Nums [");
        if (!nums.isEmpty()) {
            for (int i = 0; i < nums.size(); i++) {
                if (nums.get(i).getIsAnnotation()) {
                    sb.append(i + " " + nums.get(i).getReference().get(0).getObjectId() + " 0 R");
                } else {
                    sb.append(i + " " + nums.get(i).getObjectId() + " 0 R");
                }
                if (i < nums.size() - 1) {
                    sb.append(" ");
                }
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
        for (Element num : nums) {
            elements.addAll(num.buildElementList());
        }

        return elements;
    }
    
}
