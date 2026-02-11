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
import java.util.List;

import com.htmltopdf.renderer.Util;
import com.htmltopdf.security.PdfR4V4Security;

/**
 * Class to hold the data for one bookmark
 */
public class Outline implements Element {
    private int objectId;
    private String title;
    private Element parent;
    private Page page;
    private int xPos;
    private int yPos;
    private Outline next;
    private Outline prev;

    /**
     * Set the title of the bookmark
     * @param title String of the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set the parent element
     * @param parent Element of the parent object
     */
    public void setParent(Element parent) {
        this.parent = parent;
    }

    /**
     * Set page of the bookmark
     * @param page Page element
     */
    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * Set the position of the bookmark
     * @param x int of the x position
     * @param y int of the y position
     */
    public void setPosition(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }

    /**
     * Sets the next pointer
     * @param outline Outline the next outline
     */
    public void setNext(Outline outline) {
        this.next = outline;
    }

    /**
     * Sets the previous pointer
     * @param outline Outline the previous outline
     */
    public void setPrev(Outline outline) {
        this.prev = outline;
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
        if (encryptionKey != null) {
            byte[] encrypted = PdfR4V4Security.encryptString(encryptionKey, objectId, 0, title.getBytes(StandardCharsets.ISO_8859_1));
            sb.append("<< /Title <" + Util.byteToHex(encrypted) + ">\n");
        } else {
            sb.append("<< /Title (" + title + ")\n");
        }
        sb.append("/Parent " + parent.getObjectId() + " 0 R\n");
        sb.append("/Dest [" + page.getObjectId() + " 0 R /XYZ " + xPos + " " + yPos + " 0]\n");
        if (prev != null) {
            sb.append("/Prev " + prev.objectId + " 0 R\n");
        }
        if (next != null) {
            sb.append("/Next " + next.objectId + " 0 R\n");
        }
        sb.append(">>\n");
        sb.append("endobj\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        // Not needed for this element type
        throw new UnsupportedOperationException("Outline has an Unimplemented method 'buildElementList'");
    }
    
}
