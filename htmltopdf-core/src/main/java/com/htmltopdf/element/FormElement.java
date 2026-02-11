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
 * Class to hold the Form Element data
 */
public class FormElement implements Element {
    private int objectId;
    private Element parent;
    private Element widget;
    private Page page;

    /**
     * Sets the parent Element
     * @param parent Element of the parent
     */
    public void setParent(Element parent) {
        this.parent = parent;
    }

    /**
     * Sets the widget object
     * @param widget Element of the widget
     */
    public void setWidget(Element widget) {
        this.widget = widget;
    }

    /**
     * Sets the page element
     * @param page Page object
     */
    public void setPage(Page page) {
        this.page = page;
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
        sb.append("<< /Type /StructElem\n");
        sb.append("/S /Form\n");
        sb.append("/P " + parent.getObjectId() + " 0 R\n");
        sb.append("/Pg " + page.getObjectId() + " 0 R\n");
        if (widget != null) {
            sb.append("/K [<< /Type /OBJR /Obj " + widget.getObjectId() + " 0 R >>]\n");
        }
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
