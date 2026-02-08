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

import java.util.List;

/**
 * Interface for all PDF Structure elements
 */
public interface Element {
    
    /**
     * Sets the element object Id
     * @param objectId int of the object id
     */
    public void setObjectId(int objectId);

    /**
     * Gets the element object Id
     * @return int of the object id
     */
    public int getObjectId();

    /**
     * Implementation to create a byte array of the
     * Element
     * @param encryptionKey byte array of the encryption key
     * @return byte array of the element
     */
    public byte[] toByte(byte[] encryptionKey);

    /**
     * Builds a List of the element and child elements
     * If unneeded throws Unsupported Operation Exception
     * @return List of elements
     */
    public List<Element> buildElementList();
}
