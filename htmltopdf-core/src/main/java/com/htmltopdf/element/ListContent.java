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

/**
 * Class to hold the List Content
 */
public class ListContent implements Element {
    private List<ListItemContent> contents;
    private float xPos;
    private float yPos;
    private int currentLevel = 0;

    /**
     * Constructor - Initialize object
     */
    public ListContent() {
        contents = new ArrayList<>();
    }

    /**
     * Add a List Item Content
     * @param content ListItemContent object
     */
    public void addListItem(ListItemContent content) {
        contents.add(content);
    }

    /**
     * Get the content list size
     * @return int the size of the content
     */
    public int getListSize() {
        return contents.size();
    }

    /**
     * Set the position on the page of the text
     * @param xPos float the X position
     * @param yPos float the Y position
     */
    public void setPosition(float xPos, float yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    @Override
    public void setObjectId(int objectId) {
        // Not needed for this Element type
        throw new UnsupportedOperationException("ListContent has an Unimplemented method 'setObjectId'");
    }

    @Override
    public int getObjectId() {
        // Not needed for this Element type
        throw new UnsupportedOperationException("ListContent has an Unimplemented method 'getObjectId'");
    }

    @Override
    public byte[] toByte(byte[] encryptionKey) {
        byte[] start = ("BT\n " + xPos + " " + yPos + " Td\n").getBytes(StandardCharsets.ISO_8859_1);
        byte[] end = "ET\n".getBytes(StandardCharsets.ISO_8859_1);
        byte[] content = new byte[0];

        for (int i = 0; i < contents.size(); i++) {
            int yOffset = 0;
            int xOffset = 0;
            if (i > 0) {
                if (contents.get(i).getLevel() == currentLevel) {
                    xOffset = 0;
                } else if (contents.get(i).getLevel() < currentLevel) {
                    xOffset = (contents.get(i).getLevel() * 25) - (currentLevel * 25);
                    currentLevel = contents.get(i).getLevel();
                } else {
                    xOffset = contents.get(i).getLevel() * 25;
                    currentLevel = contents.get(i).getLevel();
                }
                yOffset = Util.getTextHeight(contents.get(i).getFont(), contents.get(i).getSize());
                String pos = (0 + xOffset) + " " + (0 - yOffset) + " Td\n";
                byte[] c = new byte[pos.length()];
                System.arraycopy(c, 0, content, content.length, c.length);
            }
        }

        for (ListItemContent c : contents) {
            byte[] annot = c.getAnnotationBytes();
            System.arraycopy(annot, 0, content, content.length, annot.length);
        }

        byte[] output = new byte[start.length + content.length + end.length];
        System.arraycopy(start, 0, output, 0, start.length);
        System.arraycopy(content, 0, output, start.length, content.length);
        System.arraycopy(end, 0, output, start.length + content.length, end.length);

        return output;
    }

    @Override
    public List<Element> buildElementList() {
        // Not needed for this Element type
        throw new UnsupportedOperationException("ListContent has an Unimplemented method 'buildElementList'");
    }
    
}
