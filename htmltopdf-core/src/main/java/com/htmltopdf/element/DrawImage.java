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

public class DrawImage implements Element {
    private int identifier;
    private float x;
    private float y;
    private float width;
    private float height;
    private int scaleX = 0;
    private int scaleY = 0;
    private int mcid = -1;

    /**
     * Constructor - Initialize object
     * @param identifier int of the image identifier
     * @param x float of the X coordinate
     * @param y float of the Y coordinate
     * @param width float of the width
     * @param height float of the height
     */
    public DrawImage(int identifier, float x, float y, float width, float height) {
        this.identifier = identifier;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the mcid if the image is marked content
     * @param mcid int of the mcid
     */
    public void setMicd(int mcid) {
        this.mcid = mcid;
    }

    @Override
    public void setObjectId(int objectId) {
        // Not needed for this Element type
        throw new UnsupportedOperationException("DrawImage has an Unimplemented method 'setObjectId'");
    }

    @Override
    public int getObjectId() {
        // Not needed for this Element type
        throw new UnsupportedOperationException("DrawImage has an Unimplemented method 'getObjectId'");
    }

    @Override
    public byte[] toByte(byte[] encryptionKey) {
        StringBuilder sb = new StringBuilder();
        if (mcid != -1) {
            sb.append("/Figure << /MCID " + mcid + " >> BDC\n");
        }
        sb.append("/Artifact BMC\n");
        sb.append("q\n");
        sb.append(width + " " + scaleX + " " + scaleY + " " + height + " " + x + " " + y + " cm\n");
        sb.append("/Im" + identifier +  " Do\n");
        sb.append("Q\n");
        sb.append("EMC\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public List<Element> buildElementList() {
        // Not needed for this Element type
        throw new UnsupportedOperationException("DrawImage has an Unimplemented method 'buildElementList'");
    }
    
}
